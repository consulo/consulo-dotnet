/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.microsoft.debugger;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.consulo.lombok.annotations.Logger;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.Processor;
import com.intellij.util.TimeoutUtil;
import com.intellij.util.containers.MultiMap;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.settings.XDebuggerSettingManagerImpl;
import com.intellij.xdebugger.impl.ui.XDebugSessionTab;
import com.intellij.xdebugger.impl.ui.XDebuggerUIConstants;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetSuspendContext;
import consulo.dotnet.debugger.breakpoint.DotNetBreakpointEngine;
import consulo.dotnet.debugger.breakpoint.DotNetBreakpointUtil;
import consulo.dotnet.microsoft.debugger.breakpoint.MicrosoftBreakpointUtil;
import consulo.dotnet.microsoft.debugger.proxy.MicrosoftThreadProxy;
import consulo.dotnet.microsoft.debugger.proxy.MicrosoftVirtualMachineProxy;
import mssdw.DebugInformationResult;
import mssdw.EventKind;
import mssdw.NotSuspendedException;
import mssdw.SocketAttachingConnector;
import mssdw.VMDisconnectedException;
import mssdw.VirtualMachine;
import mssdw.connect.Connector;
import mssdw.event.BreakpointEvent;
import mssdw.event.Event;
import mssdw.event.EventQueue;
import mssdw.event.EventSet;
import mssdw.event.ModuleLoadEvent;
import mssdw.event.VMDeathEvent;
import mssdw.request.BreakpointRequest;
import mssdw.request.EventRequestManager;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
@Logger
public class MicrosoftDebugThread extends Thread
{
	private final XDebugSession mySession;
	private final MicrosoftDebugProcess myDebugProcess;
	private final DebugConnectionInfo myDebugConnectionInfo;
	private final DotNetBreakpointEngine myBreakpointEngine = new DotNetBreakpointEngine();
	private final Queue<Processor<MicrosoftVirtualMachineProxy>> myQueue = new ConcurrentLinkedQueue<Processor<MicrosoftVirtualMachineProxy>>();

	private MicrosoftVirtualMachineProxy myVirtualMachine;
	private boolean myStop;

	public MicrosoftDebugThread(XDebugSession session, MicrosoftDebugProcess debugProcess, DebugConnectionInfo debugConnectionInfo)
	{
		super("MicrosoftDebugThread: " + new Random().nextInt());
		mySession = session;
		myDebugProcess = debugProcess;
		myDebugConnectionInfo = debugConnectionInfo;
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
		myVirtualMachine = null;
	}

	@Override
	public void run()
	{
		VirtualMachine virtualMachine = null;
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
				TimeoutUtil.sleep(100);
			}
		}

		if(virtualMachine == null)
		{
			return;
		}

		myVirtualMachine = new MicrosoftVirtualMachineProxy(virtualMachine);

		virtualMachine.enableEvents(EventKind.MODULE_LOAD);

		final Collection<? extends XLineBreakpoint<?>> breakpoints = myDebugProcess.getLineBreakpoints();
		for(XLineBreakpoint<?> breakpoint : breakpoints)
		{
			DotNetBreakpointUtil.updateLineBreakpointIcon(mySession.getProject(), null, breakpoint);
		}

		final MultiMap<String, XLineBreakpoint<?>> map = new MultiMap<String, XLineBreakpoint<?>>();

		ApplicationManager.getApplication().runReadAction(new Runnable()
		{
			@Override
			@RequiredReadAction
			public void run()
			{
				for(XLineBreakpoint<?> breakpoint : breakpoints)
				{
					String modulePath = MicrosoftBreakpointUtil.getModulePath(mySession.getProject(), breakpoint);
					if(modulePath != null)
					{
						map.putValue(modulePath, breakpoint);
					}
				}
			}
		});


		/*Collection<? extends XBreakpoint<DotNetExceptionBreakpointProperties>> exceptionBreakpoints = myDebugProcess.getExceptionBreakpoints();
		for(XBreakpoint<DotNetExceptionBreakpointProperties> exceptionBreakpoint : exceptionBreakpoints)
		{
			String vmQName = exceptionBreakpoint.getProperties().VM_QNAME;
			if(!StringUtil.isEmpty(vmQName))
			{
				continue;
			}
			MonoBreakpointUtil.createExceptionRequest(myVirtualMachine, exceptionBreakpoint, null);
		}

		Collection<? extends XLineBreakpoint<DotNetMethodBreakpointProperties>> methodBreakpoints = myDebugProcess.getMethodBreakpoints();
		for(XLineBreakpoint<DotNetMethodBreakpointProperties> lineBreakpoint : methodBreakpoints)
		{
			MonoBreakpointUtil.createMethodRequest(mySession, myVirtualMachine, lineBreakpoint);
		} */

		try
		{
			virtualMachine.eventQueue().remove(); //Wait VMStart
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
			return;
		}

		EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();

		while(!myStop)
		{
			processCommands(myVirtualMachine);

			EventQueue eventQueue = virtualMachine.eventQueue();
			EventSet eventSet;
			try
			{
				boolean stopped = false;
				boolean focusUI = false;

				while((eventSet = eventQueue.remove(1)) != null)
				{
					for(final Event event : eventSet)
					{
						if(event instanceof ModuleLoadEvent)
						{
							String path = ((ModuleLoadEvent) event).getPath();
							myVirtualMachine.addLoadedModule(path);
							Collection<XLineBreakpoint<?>> targetBreakpoints = map.get(path);
							for(XLineBreakpoint<?> targetBreakpoint : targetBreakpoints)
							{
								DebugInformationResult debugOffset = myVirtualMachine.getDelegate().findDebugOffset(targetBreakpoint.getPresentableFilePath(), targetBreakpoint.getLine() + 1, -1);
								if(debugOffset != null)
								{
									BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(debugOffset);
									myVirtualMachine.putRequest(targetBreakpoint, breakpointRequest);
									breakpointRequest.enable();
								}

								DotNetBreakpointUtil.updateLineBreakpointIcon(mySession.getProject(), debugOffset != null, targetBreakpoint);
							}
						}
						else if(event instanceof BreakpointEvent)
						{
							XBreakpoint<?> breakpoint = myVirtualMachine.findBreakpointByRequest(event.request());
							assert breakpoint instanceof XLineBreakpoint;

							stopped = true;

							DotNetDebugContext debugContext = myDebugProcess.createDebugContext(myVirtualMachine, breakpoint);
							if(breakpoint != null)
							{
								MicrosoftThreadProxy threadProxy = new MicrosoftThreadProxy(myVirtualMachine, eventSet.eventThread());

								myBreakpointEngine.tryEvaluateBreakpointLogMessage(threadProxy, (XLineBreakpoint<?>) breakpoint, debugContext);

								if(myBreakpointEngine.tryEvaluateBreakpointCondition(threadProxy, (XLineBreakpoint<?>) breakpoint, debugContext))
								{
									DotNetSuspendContext suspendContext = new DotNetSuspendContext(debugContext, eventSet.eventThread().id());

									mySession.breakpointReached(breakpoint, null, suspendContext);
								}
								else
								{
									stopped = false;
								}
							}
							else
							{
								mySession.positionReached(new DotNetSuspendContext(debugContext, eventSet.eventThread().id()));
								focusUI = true;
							}
						}
						/*else if(event instanceof StepEvent)
						{
							DotNetDebugContext context = myDebugProcess.createDebugContext(myVirtualMachine, null);

							mySession.positionReached(new DotNetSuspendContext(context, MonoThreadProxy.getIdFromThread(myVirtualMachine, eventSet.eventThread())));
							stopped = true;
						}
						else if(event instanceof UserBreakEvent)
						{
							DotNetDebugContext context = myDebugProcess.createDebugContext(myVirtualMachine, null);
							mySession.positionReached(new DotNetSuspendContext(context, MonoThreadProxy.getIdFromThread(myVirtualMachine, eventSet.eventThread())));
							stopped = true;
							focusUI = true;
						}   */
						else if(event instanceof VMDeathEvent)
						{
							connectionStopped();
							return;
						}
						/*else if(event instanceof UserLogEvent)
						{
							//int level = ((UserLogEvent) event).getLevel();
							String category = ((UserLogEvent) event).getCategory();
							String message = ((UserLogEvent) event).getMessage();

							ConsoleView consoleView = mySession.getConsoleView();
							consoleView.print("[" + category + "] " + message + "\n", ConsoleViewContentType.USER_INPUT);
						}
						else if(event instanceof ExceptionEvent)
						{
							XBreakpoint<?> breakpoint = myVirtualMachine.findBreakpointByRequest(event.request());
							DotNetDebugContext context = myDebugProcess.createDebugContext(myVirtualMachine, breakpoint);

							DotNetSuspendContext suspendContext = new DotNetSuspendContext(context, MonoThreadProxy.getIdFromThread(myVirtualMachine, eventSet.eventThread()));
							if(breakpoint != null)
							{
								mySession.breakpointReached(breakpoint, null, suspendContext);
							}
							else
							{
								mySession.positionReached(suspendContext);
								focusUI = true;
							}
							stopped = true;
						}
						else if(event instanceof MethodEntryEvent)
						{
							//
						}
						else if(event instanceof MethodExitEvent)
						{
							//
						}  */
						else
						{
							LOGGER.error("Unknown event " + event.getClass().getSimpleName());
						}
					}

					if(stopped)
					{
						myVirtualMachine.stopStepRequests();

						myDebugProcess.setPausedEventSet(eventSet);

						if(focusUI)
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
					else
					{
						try
						{
							virtualMachine.resume();
							break;
						}
						catch(NotSuspendedException ignored)
						{
							// when u attached - app is not suspended
						}
					}
				}
			}
			catch(VMDisconnectedException e)
			{
				// dont interest
			}
			catch(Throwable e)
			{
				LOGGER.error(e);
			}
		}
	}

	private void processCommands(MicrosoftVirtualMachineProxy virtualMachine)
	{
		Processor<MicrosoftVirtualMachineProxy> processor;
		while((processor = myQueue.poll()) != null)
		{
			if(processor.process(virtualMachine))
			{
				virtualMachine.resume();
			}
		}
	}

	public XDebugSession getSession()
	{
		return mySession;
	}

	public void processAnyway(Processor<MicrosoftVirtualMachineProxy> processor)
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

	public void addCommand(Processor<MicrosoftVirtualMachineProxy> processor)
	{
		myQueue.add(processor);
	}

	public boolean isConnected()
	{
		return myVirtualMachine != null;
	}
}

