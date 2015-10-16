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

package org.mustbe.consulo.dotnet.debugger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetBreakpointUtil;
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetLineBreakpointType;
import org.mustbe.consulo.dotnet.debugger.linebreakType.properties.DotNetLineBreakpointProperties;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.ArrayUtil;
import com.intellij.util.EventDispatcher;
import com.intellij.util.Processor;
import com.intellij.util.ThreeState;
import com.intellij.util.TimeoutUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.settings.XDebuggerSettingsManager;
import com.intellij.xdebugger.impl.ui.XDebugSessionTab;
import com.intellij.xdebugger.impl.ui.XDebuggerUIConstants;
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
public class DotNetDebugThread extends Thread
{
	private final XDebugSession mySession;
	private final XDebuggerManager myDebuggerManager;
	private final DotNetDebugProcess myDebugProcess;
	private final DebugConnectionInfo myDebugConnectionInfo;
	private final RunProfile myRunProfile;
	private boolean myStop;

	private Queue<Processor<DotNetVirtualMachine>> myQueue = new ConcurrentLinkedQueue<Processor<DotNetVirtualMachine>>();

	private DotNetVirtualMachine myVirtualMachine;

	private EventDispatcher<DotNetVirtualMachineListener> myEventDispatcher = EventDispatcher.create(DotNetVirtualMachineListener.class);

	public DotNetDebugThread(XDebugSession session, DotNetDebugProcess debugProcess, DebugConnectionInfo debugConnectionInfo, RunProfile runProfile)
	{
		super("DotNetDebugThread: " + new Random().nextInt());
		mySession = session;
		myDebugProcess = debugProcess;
		myDebugConnectionInfo = debugConnectionInfo;
		myRunProfile = runProfile;
		myDebuggerManager = XDebuggerManager.getInstance(session.getProject());
	}

	public void addListener(DotNetVirtualMachineListener listener)
	{
		myEventDispatcher.addListener(listener);
	}

	public void removeListener(DotNetVirtualMachineListener listener)
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
					tryCount --;
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

		myVirtualMachine = new DotNetVirtualMachine(virtualMachine);

		virtualMachine.enableEvents(/*EventKind.ASSEMBLY_LOAD, EventKind.THREAD_START, EventKind.THREAD_DEATH, EventKind.ASSEMBLY_UNLOAD,*/
				EventKind.USER_BREAK, EventKind.USER_LOG, EventKind.APPDOMAIN_CREATE, EventKind.APPDOMAIN_UNLOAD);

		Collection<? extends XLineBreakpoint<?>> breakpoints = getEnabledBreakpoints();
		for(XLineBreakpoint<?> breakpoint : breakpoints)
		{
			DotNetBreakpointUtil.updateBreakpointPresentation(mySession.getProject(), false, breakpoint);
		}

		TypeLoadRequest typeLoad = virtualMachine.eventRequestManager().createTypeLoad();
		if(virtualMachine.isAtLeastVersion(2, 9))
		{
			Set<String> files = new HashSet<String>();
			for(XLineBreakpoint<?> breakpoint : breakpoints)
			{
				files.add(breakpoint.getPresentableFilePath());
			}

			typeLoad.addSourceFileFilter(ArrayUtil.toStringArray(files));
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
						DotNetDebugContext debugContext = createDebugContext(xLineBreakpoint);
						if(xLineBreakpoint != null)
						{
							mySession.breakpointReached(xLineBreakpoint, null, new DotNetSuspendContext(debugContext, eventSet.eventThread()));
						}
						else
						{
							mySession.positionReached(new DotNetSuspendContext(debugContext, eventSet.eventThread()));
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
										if(XDebuggerSettingsManager.getInstanceImpl().getGeneralSettings().isShowDebuggerOnBreakpoint())
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

	private void insertBreakpoints(final DotNetVirtualMachine virtualMachine, final TypeMirror typeMirror)
	{
		final DotNetDebugContext debugContext = createDebugContext(null);

		DotNetTypeDeclaration[] typeDeclarations = ApplicationManager.getApplication().runReadAction(new Computable<DotNetTypeDeclaration[]>()
		{
			@Override
			public DotNetTypeDeclaration[] compute()
			{
				return DotNetVirtualMachineUtil.findTypesByQualifiedName(typeMirror, debugContext);
			}
		});

		if(typeDeclarations.length > 0)
		{
			Collection<? extends XLineBreakpoint<?>> breakpoints = getEnabledBreakpoints();
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

					DotNetLineBreakpointType type = (DotNetLineBreakpointType) breakpoint.getType();

					type.createRequest(mySession, virtualMachine, breakpoint, typeMirror);
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

	private void processCommands(DotNetVirtualMachine virtualMachine)
	{
		Processor<DotNetVirtualMachine> processor;
		while((processor = myQueue.poll()) != null)
		{
			if(processor.process(virtualMachine))
			{
				virtualMachine.resume();
			}
		}
	}

	@NotNull
	public DotNetDebugContext createDebugContext(@Nullable XLineBreakpoint<?> breakpoint)
	{
		assert myVirtualMachine != null;
		return new DotNetDebugContext(mySession.getProject(), myVirtualMachine, myRunProfile, mySession, breakpoint);
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

		XLineBreakpoint<?> breakpointAtLine = myDebuggerManager.getBreakpointManager().findBreakpointAtLine
				(DotNetLineBreakpointType.getInstance(), fileByPath, location.lineNumber() - 1); // .net asm - 1 index based, consulo - 0 based
		if(breakpointAtLine == null)
		{
			return null;
		}
		return breakpointAtLine;
	}

	public void normalizeBreakpoints()
	{
		for(XLineBreakpoint<?> lineBreakpoint : getEnabledBreakpoints())
		{
			myDebuggerManager.getBreakpointManager().updateBreakpointPresentation(lineBreakpoint, null, null);
		}
	}

	public XDebugSession getSession()
	{
		return mySession;
	}

	@NotNull
	public Collection<? extends XLineBreakpoint<?>> getEnabledBreakpoints()
	{
		return ApplicationManager.getApplication().runReadAction(new Computable<Collection<? extends XLineBreakpoint<DotNetLineBreakpointProperties>>>()
		{
			@Override
			public Collection<? extends XLineBreakpoint<DotNetLineBreakpointProperties>> compute()
			{
				Collection<? extends XLineBreakpoint<DotNetLineBreakpointProperties>> breakpoints = myDebuggerManager.getBreakpointManager().getBreakpoints
						(DotNetLineBreakpointType.class);
				return ContainerUtil.filter(breakpoints, new Condition<XLineBreakpoint<DotNetLineBreakpointProperties>>()
				{
					@Override
					public boolean value(XLineBreakpoint<DotNetLineBreakpointProperties> breakpoint)
					{
						return breakpoint.isEnabled();
					}
				});
			}
		});
	}

	public void processAnyway(Processor<DotNetVirtualMachine> processor)
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

	public void addCommand(Processor<DotNetVirtualMachine> processor)
	{
		myQueue.add(processor);
	}

	public boolean isConnected()
	{
		return myVirtualMachine != null;
	}
}
