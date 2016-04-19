/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.dotnet.mono.debugger;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.Exported;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.ArrayUtil;
import com.intellij.util.EventDispatcher;
import com.intellij.util.Processor;
import com.intellij.util.ThreeState;
import com.intellij.util.TimeoutUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import com.intellij.xdebugger.impl.settings.XDebuggerSettingManagerImpl;
import com.intellij.xdebugger.impl.ui.XDebugSessionTab;
import com.intellij.xdebugger.impl.ui.XDebuggerUIConstants;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetDebuggerProvider;
import consulo.dotnet.debugger.DotNetDebuggerSourceLineResolver;
import consulo.dotnet.debugger.DotNetDebuggerSourceLineResolverEP;
import consulo.dotnet.debugger.DotNetDebuggerUtil;
import consulo.dotnet.debugger.DotNetSuspendContext;
import consulo.dotnet.debugger.breakpoint.DotNetBreakpointUtil;
import consulo.dotnet.debugger.breakpoint.DotNetLineBreakpointType;
import consulo.dotnet.debugger.nodes.DotNetAbstractVariableMirrorNode;
import consulo.dotnet.debugger.proxy.value.DotNetBooleanValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.mono.debugger.breakpoint.MonoBreakpointUtil;
import consulo.dotnet.mono.debugger.proxy.MonoStackFrameProxy;
import consulo.dotnet.mono.debugger.proxy.MonoThreadProxy;
import consulo.dotnet.mono.debugger.proxy.MonoVirtualMachineProxy;
import mono.debugger.*;
import mono.debugger.connect.Connector;
import mono.debugger.event.*;
import mono.debugger.request.BreakpointRequest;
import mono.debugger.request.TypeLoadRequest;

/**
 * @author VISTALL
 * @since 10.04.14
 */
@Logger
public class MonoDebugThread extends Thread
{
	private final XDebugSession mySession;
	private final MonoDebugProcess myDebugProcess;
	private final DebugConnectionInfo myDebugConnectionInfo;
	private boolean myStop;

	private Queue<Processor<MonoVirtualMachineProxy>> myQueue = new ConcurrentLinkedQueue<Processor<MonoVirtualMachineProxy>>();

	private MonoVirtualMachineProxy myVirtualMachine;

	private EventDispatcher<MonoVirtualMachineListener> myEventDispatcher = EventDispatcher.create(MonoVirtualMachineListener.class);

	public MonoDebugThread(XDebugSession session, MonoDebugProcess debugProcess, DebugConnectionInfo debugConnectionInfo)
	{
		super("DotNetDebugThread: " + new Random().nextInt());
		mySession = session;
		myDebugProcess = debugProcess;
		myDebugConnectionInfo = debugConnectionInfo;
	}

	@Exported
	public void addListener(MonoVirtualMachineListener listener)
	{
		myEventDispatcher.addListener(listener);
	}

	@Exported
	public void removeListener(MonoVirtualMachineListener listener)
	{
		myEventDispatcher.removeListener(listener);
	}

	public void setStop()
	{
		if(myVirtualMachine != null)
		{
			try
			{
				myVirtualMachine.dispose();
			}
			catch(Exception e)
			{
				//
			}
		}
		connectionStopped();
	}

	private void connectionStopped()
	{
		myStop = true;
		myEventDispatcher.getMulticaster().connectionStopped();
		myVirtualMachine = null;
	}

	@Override
	public void run()
	{
		VirtualMachine virtualMachine = null;
		if(!myDebugConnectionInfo.isServer())
		{
			SocketListeningConnector l = new SocketListeningConnector();
			Map<String, Connector.Argument> argumentMap = l.defaultArguments();
			argumentMap.get(SocketListeningConnector.ARG_LOCALADDR).setValue(myDebugConnectionInfo.getHost());
			argumentMap.get(SocketListeningConnector.ARG_PORT).setValue(String.valueOf(myDebugConnectionInfo.getPort()));
			argumentMap.get("timeout").setValue("10000");


			try
			{
				virtualMachine = l.accept(argumentMap);
			}
			catch(Exception e)
			{
				//
			}
		}
		else
		{
			SocketAttachingConnector l = new SocketAttachingConnector();
			Map<String, Connector.Argument> argumentMap = l.defaultArguments();
			argumentMap.get("hostname").setValue(myDebugConnectionInfo.getHost());
			argumentMap.get("port").setValue(String.valueOf(myDebugConnectionInfo.getPort()));
			argumentMap.get("timeout").setValue("10");

			int tryCount = 5;
			while(tryCount != 0)
			{
				try
				{
					virtualMachine = l.attach(argumentMap);
					break;
				}
				catch(Exception e)
				{
					tryCount--;
				}
			}
		}

		if(virtualMachine == null)
		{
			myEventDispatcher.getMulticaster().connectionFailed();
			return;
		}
		else
		{
			myEventDispatcher.getMulticaster().connectionSuccess(virtualMachine);
		}

		myVirtualMachine = new MonoVirtualMachineProxy(virtualMachine);

		virtualMachine.enableEvents(/*EventKind.ASSEMBLY_LOAD, EventKind.THREAD_START, EventKind.THREAD_DEATH, EventKind.ASSEMBLY_UNLOAD,*/
				EventKind.USER_BREAK, EventKind.USER_LOG, EventKind.APPDOMAIN_CREATE, EventKind.APPDOMAIN_UNLOAD);

		Collection<? extends XLineBreakpoint<?>> breakpoints = myDebugProcess.getEnabledBreakpoints();
		for(XLineBreakpoint<?> breakpoint : breakpoints)
		{
			DotNetBreakpointUtil.updateBreakpointPresentation(mySession.getProject(), false, breakpoint);
		}

		TypeLoadRequest typeLoad = virtualMachine.eventRequestManager().createTypeLoad();
		if(virtualMachine.isAtLeastVersion(2, 9))
		{
			Set<String> types = new LinkedHashSet<String>();
			for(XLineBreakpoint<?> breakpoint : breakpoints)
			{
				collectTypeNames(breakpoint, types);
			}

			if(!types.isEmpty())
			{
				typeLoad.addTypeNameFilter(ArrayUtil.toStringArray(types));
			}
		}
		typeLoad.enable();

		try
		{
			virtualMachine.eventQueue().remove();  //Wait VMStart
			try
			{
				virtualMachine.resume();
			}
			catch(Exception e)
			{
				//
			}
		}
		catch(InterruptedException e)
		{
			LOGGER.error(e);
			myEventDispatcher.getMulticaster().connectionFailed();
			return;
		}

		while(!myStop)
		{
			processCommands(myVirtualMachine);

			EventQueue eventQueue = virtualMachine.eventQueue();
			EventSet eventSet;
			try
			{
				l:
				while((eventSet = eventQueue.remove(10)) != null)
				{
					ThreeState state = eventSet.suspendPolicy() == SuspendPolicy.ALL ? ThreeState.YES : ThreeState.NO;
					Location location = null;

					for(final Event event : eventSet)
					{
						if(event instanceof BreakpointEvent)
						{
							location = ((BreakpointRequest) event.request()).location();
						}
						else if(event instanceof StepEvent)
						{
							location = ((StepEvent) event).location();
						}
						else if(event instanceof AppDomainCreateEvent)
						{
							AppDomainMirror appDomainMirror = ((AppDomainCreateEvent) event).getAppDomainMirror();
							myVirtualMachine.loadAppDomain(appDomainMirror);
						}
						else if(event instanceof AppDomainUnloadEvent)
						{
							AppDomainMirror appDomainMirror = ((AppDomainUnloadEvent) event).getAppDomainMirror();
							myVirtualMachine.unloadAppDomain(appDomainMirror);
						}
						else if(event instanceof TypeLoadEvent)
						{
							TypeMirror typeMirror = ((TypeLoadEvent) event).typeMirror();

							insertBreakpoints(myVirtualMachine, typeMirror);
							continue l;
						}
						else if(event instanceof VMDeathEvent)
						{
							connectionStopped();
							return;
						}
						else if(event instanceof UserBreakEvent)
						{
							state = ThreeState.UNSURE;
						}
						else if(event instanceof UserLogEvent)
						{
							//int level = ((UserLogEvent) event).getLevel();
							String category = ((UserLogEvent) event).getCategory();
							String message = ((UserLogEvent) event).getMessage();

							ConsoleView consoleView = mySession.getConsoleView();
							consoleView.print("[" + category + "] " + message + "\n", ConsoleViewContentType.USER_INPUT);
						}
						else
						{
							LOGGER.error("Unknown event " + event.getClass().getSimpleName());
						}
					}

					if(state != ThreeState.NO)
					{
						if(state == ThreeState.UNSURE)
						{
							myVirtualMachine.suspend();
						}

						myVirtualMachine.stopStepRequests();

						myDebugProcess.setPausedEventSet(eventSet);
						XLineBreakpoint<?> xLineBreakpoint = resolveToBreakpoint(location);
						DotNetDebugContext debugContext = myDebugProcess.createDebugContext(myVirtualMachine, xLineBreakpoint);
						if(xLineBreakpoint != null)
						{
							DotNetSuspendContext suspendContext = new DotNetSuspendContext(debugContext, MonoThreadProxy.getIdFromThread(myVirtualMachine, eventSet.eventThread()));

							if(tryEvaluateBreakpoint(eventSet, xLineBreakpoint, debugContext))
							{
								mySession.breakpointReached(xLineBreakpoint, null, suspendContext);
							}
							else
							{
								myVirtualMachine.resume();
							}
						}
						else
						{
							mySession.positionReached(new DotNetSuspendContext(debugContext, MonoThreadProxy.getIdFromThread(myVirtualMachine, eventSet.eventThread())));
						}

						if(state == ThreeState.UNSURE)
						{
							UIUtil.invokeLaterIfNeeded(new Runnable()
							{
								@Override
								public void run()
								{
									XDebugSessionTab sessionTab = ((XDebugSessionImpl) mySession).getSessionTab();
									if(sessionTab != null)
									{
										if(XDebuggerSettingManagerImpl.getInstanceImpl().getGeneralSettings().isShowDebuggerOnBreakpoint())
										{
											sessionTab.toFront(true, null);
										}
										sessionTab.getUi().attractBy(XDebuggerUIConstants.LAYOUT_VIEW_BREAKPOINT_CONDITION);
									}
								}
							});
						}
						break;
					}
				}
			}
			catch(VMDisconnectedException e)
			{
				// dont interest
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}

			TimeoutUtil.sleep(50);
		}
	}

	private void collectTypeNames(@NotNull final XLineBreakpoint<?> breakpoint, @NotNull final Set<String> names)
	{
		final Project project = mySession.getProject();

		final VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(breakpoint.getFileUrl());
		if(fileByUrl == null)
		{
			return;
		}

		final PsiFile file = ApplicationManager.getApplication().runReadAction(new Computable<PsiFile>()
		{
			@Override
			public PsiFile compute()
			{
				return PsiManager.getInstance(project).findFile(fileByUrl);
			}
		});

		if(file == null)
		{
			return;
		}

		ApplicationManager.getApplication().runReadAction(new Runnable()
		{
			@Override
			public void run()
			{
				PsiElement psiElement = DotNetDebuggerUtil.findPsiElement(file, breakpoint.getLine());
				if(psiElement == null)
				{
					return;
				}
				DotNetDebuggerSourceLineResolver resolver = DotNetDebuggerSourceLineResolverEP.INSTANCE.forLanguage(file.getLanguage());
				assert resolver != null;
				String name = resolver.resolveParentVmQName(psiElement);
				if(name != null)
				{
					names.add(name);
				}
			}
		});
	}

	private boolean tryEvaluateBreakpoint(EventSet eventSet, final XLineBreakpoint<?> breakpoint, final DotNetDebugContext debugContext) throws Exception
	{
		final XExpression conditionExpression = breakpoint.getConditionExpression();
		if(conditionExpression != null)
		{
			final VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(breakpoint.getFileUrl());
			if(virtualFile == null)
			{
				return true;
			}

			final DotNetDebuggerProvider provider = DotNetDebuggerProvider.getProvider(conditionExpression.getLanguage());
			if(provider != null)
			{
				final List<StackFrameMirror> frames = eventSet.eventThread().frames();

				final XValue value = ApplicationManager.getApplication().runReadAction(new Computable<XValue>()
				{
					@Override
					public XValue compute()
					{
						Document document = virtualFile.isValid() ? FileDocumentManager.getInstance().getDocument(virtualFile) : null;
						if(document == null)
						{
							return null;
						}
						int line = breakpoint.getLine();

						int offset = line < document.getLineCount() ? document.getLineStartOffset(line) : -1;
						PsiFile file = PsiManager.getInstance(debugContext.getProject()).findFile(virtualFile);
						if(file == null)
						{
							return null;
						}
						PsiElement elementAt = offset >= 0 ? file.findElementAt(offset) : null;
						if(elementAt == null)
						{
							return null;
						}
						final Ref<XValue> valueRef = Ref.create();
						provider.evaluate(new MonoStackFrameProxy(0, myVirtualMachine, frames.get(0)), debugContext, conditionExpression.getExpression(), elementAt,
								new XDebuggerEvaluator.XEvaluationCallback()
						{
							@Override
							public void evaluated(@NotNull XValue result)
							{
								valueRef.set(result);
							}

							@Override
							public void errorOccurred(@NotNull String errorMessage)
							{
							}
						}, XSourcePositionImpl.createByElement(elementAt));
						return valueRef.get();
					}
				});

				if(value instanceof DotNetAbstractVariableMirrorNode)
				{
					DotNetValueProxy valueOfVariableSafe = ((DotNetAbstractVariableMirrorNode) value).getValueOfVariableSafe();
					if(valueOfVariableSafe instanceof DotNetBooleanValueProxy)
					{
						Boolean boolValue = ((DotNetBooleanValueProxy) valueOfVariableSafe).getValue();
						return boolValue != null && boolValue;
					}
				}
			}
		}
		return true;
	}

	private void insertBreakpoints(final MonoVirtualMachineProxy virtualMachine, final TypeMirror typeMirror)
	{
		final DotNetDebugContext debugContext = myDebugProcess.createDebugContext(virtualMachine, null);

		DotNetTypeDeclaration[] typeDeclarations = ApplicationManager.getApplication().runReadAction(new Computable<DotNetTypeDeclaration[]>()
		{
			@Override
			public DotNetTypeDeclaration[] compute()
			{
				return MonoDebugUtil.findTypesByQualifiedName(typeMirror, debugContext);
			}
		});

		if(typeDeclarations.length > 0)
		{
			Collection<? extends XLineBreakpoint<?>> breakpoints = myDebugProcess.getEnabledBreakpoints();
			for(DotNetTypeDeclaration dotNetTypeDeclaration : typeDeclarations)
			{
				VirtualFile typeVirtualFile = PsiUtilBase.getVirtualFile(dotNetTypeDeclaration);

				for(final XLineBreakpoint<?> breakpoint : breakpoints)
				{
					VirtualFile lineBreakpoint = VirtualFileManager.getInstance().findFileByUrl(breakpoint.getFileUrl());
					if(!Comparing.equal(typeVirtualFile, lineBreakpoint))
					{
						continue;
					}

					MonoBreakpointUtil.createRequest(mySession, virtualMachine, breakpoint, typeMirror);
				}
			}
		}

		try
		{
			virtualMachine.resume();
		}
		catch(NotSuspendedException ignored)
		{
			// when u attached - app is not suspended
		}
	}

	private void processCommands(MonoVirtualMachineProxy virtualMachine)
	{
		Processor<MonoVirtualMachineProxy> processor;
		while((processor = myQueue.poll()) != null)
		{
			if(processor.process(virtualMachine))
			{
				virtualMachine.resume();
			}
		}
	}

	@Nullable
	private XLineBreakpoint<?> resolveToBreakpoint(@Nullable Location location)
	{
		if(location == null)
		{
			return null;
		}
		String sourcePath = location.sourcePath();
		if(sourcePath == null)
		{
			return null;
		}
		VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(sourcePath);
		if(fileByPath == null)
		{
			return null;
		}

		XDebuggerManager debuggerManager = XDebuggerManager.getInstance(getSession().getProject());
		XLineBreakpoint<?> breakpointAtLine = debuggerManager.getBreakpointManager().findBreakpointAtLine(DotNetLineBreakpointType.getInstance(), fileByPath,
				location.lineNumber() - 1); // .net asm - 1 index based, consulo - 0 based
		if(breakpointAtLine == null)
		{
			return null;
		}
		return breakpointAtLine;
	}

	public XDebugSession getSession()
	{
		return mySession;
	}

	public void processAnyway(Processor<MonoVirtualMachineProxy> processor)
	{
		if(myVirtualMachine == null)
		{
			return;
		}
		try
		{
			processor.process(myVirtualMachine);
		}
		catch(VMDisconnectedException ignored)
		{
		}
	}

	public void addCommand(Processor<MonoVirtualMachineProxy> processor)
	{
		myQueue.add(processor);
	}

	public boolean isConnected()
	{
		return myVirtualMachine != null;
	}
}
