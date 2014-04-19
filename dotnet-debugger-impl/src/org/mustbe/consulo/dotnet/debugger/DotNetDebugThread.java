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
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetAbstractBreakpointType;
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetLineBreakpointType;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import mono.debugger.Location;
import mono.debugger.SocketListeningConnector;
import mono.debugger.VirtualMachine;
import mono.debugger.connect.Connector;
import mono.debugger.event.Event;
import mono.debugger.event.EventQueue;
import mono.debugger.event.EventSet;
import mono.debugger.request.BreakpointRequest;
import mono.debugger.request.EventRequest;
import mono.debugger.request.StepRequest;

/**
 * @author VISTALL
 * @since 10.04.14
 */
@Logger
public class DotNetDebugThread extends Thread
{
	public static Key<EventRequest> EVENT_REQUEST = Key.create("event-request-for-line-breakpoint");

	private final XDebugSession mySession;
	private final XDebuggerManager myDebuggerManager;
	private final DotNetDebugProcess myDebugProcess;
	private final DebugConnectionInfo myDebugConnectionInfo;
	private final RunProfile myRunProfile;
	private boolean myStop;

	private VirtualMachine myVirtualMachine;

	private Queue<Processor<VirtualMachine>> myQueue = new ConcurrentLinkedDeque<Processor<VirtualMachine>>();

	public DotNetDebugThread(XDebugSession session, DotNetDebugProcess debugProcess, DebugConnectionInfo debugConnectionInfo, RunProfile runProfile)
	{
		super("DotNetDebugThread: " + new Random().nextInt());
		mySession = session;
		myDebugProcess = debugProcess;
		myDebugConnectionInfo = debugConnectionInfo;
		myRunProfile = runProfile;
		myDebuggerManager = XDebuggerManager.getInstance(session.getProject());
	}

	public void setStop()
	{
		myStop = true;
	}

	@Override
	public void run()
	{
		if(!myDebugConnectionInfo.isServer())
		{
			SocketListeningConnector l = new SocketListeningConnector();
			Map<String, Connector.Argument> argumentMap = l.defaultArguments();
			argumentMap.get(SocketListeningConnector.ARG_LOCALADDR).setValue(myDebugConnectionInfo.getHost());
			argumentMap.get(SocketListeningConnector.ARG_PORT).setValue(String.valueOf(myDebugConnectionInfo.getPort()));
			argumentMap.get("timeout").setValue("10000");


			try
			{
				myVirtualMachine = l.accept(argumentMap);
				myVirtualMachine.resume();
			}
			catch(Exception e)
			{
				//
			}
		}
		else
		{
		}

		if(myVirtualMachine == null)
		{
			return;
		}

		while(!myStop)
		{
			Processor<VirtualMachine> processor;
			while((processor = myQueue.poll()) != null)
			{
				if(processor.process(myVirtualMachine))
				{
					myVirtualMachine.resume();
				}
			}

			boolean stoppedAlready = false;
			EventQueue eventQueue = myVirtualMachine.eventQueue();
			EventSet eventSet;
			try
			{
				while((eventSet = eventQueue.remove(100)) != null)
				{
					Location location = null;

					for(Event event : eventSet)
					{
						EventRequest request = event.request();
						if(request instanceof BreakpointRequest)
						{
							location = ((BreakpointRequest) request).location();
						}

						if(request instanceof StepRequest)
						{
							request.disable();
						}
					}

					if(!stoppedAlready && eventSet.suspendPolicy() == EventRequest.SUSPEND_ALL)
					{
						stoppedAlready = true;

						myDebugProcess.setPausedEventSet(eventSet);
						XLineBreakpoint<?> xLineBreakpoint = resolveToBreakpoint(location);
						DotNetDebugContext debugContext = createDebugContext();
						if(xLineBreakpoint != null)
						{
							mySession.breakpointReached(xLineBreakpoint, null, new DotNetSuspendContext(debugContext, eventSet.eventThread()));
						}
						else
						{
							mySession.positionReached(new DotNetSuspendContext(debugContext, eventSet.eventThread()));
						}
					}
				}
			}
			catch(Exception e)
			{
				//
			}

			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException e)
			{
				//
			}
		}

	}

	@NotNull
	public DotNetDebugContext createDebugContext()
	{
		assert myVirtualMachine != null;
		return new DotNetDebugContext(mySession.getProject(), myVirtualMachine, myRunProfile);
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

		XLineBreakpoint<XBreakpointProperties> breakpointAtLine = myDebuggerManager.getBreakpointManager().findBreakpointAtLine
				(DotNetLineBreakpointType.getInstance(), fileByPath, location.lineNumber() - 1); // .net asm - 1 index based, consulo - 0 based
		if(breakpointAtLine == null)
		{
			return null;
		}
		return breakpointAtLine;
	}

	public void normalizeBreakpoints()
	{
		for(XLineBreakpoint<XBreakpointProperties> lineBreakpoint : getOurBreakpoints())
		{
			lineBreakpoint.putUserData(EVENT_REQUEST, null);

			myDebuggerManager.getBreakpointManager().updateBreakpointPresentation(lineBreakpoint, null, null);
		}
	}

	@NotNull
	public Collection<? extends XLineBreakpoint<XBreakpointProperties>> getOurBreakpoints()
	{
		return ApplicationManager.getApplication().runReadAction(new Computable<Collection<? extends XLineBreakpoint<XBreakpointProperties>>>()
		{
			@Override
			public Collection<? extends XLineBreakpoint<XBreakpointProperties>> compute()
			{
				return myDebuggerManager.getBreakpointManager().getBreakpoints(DotNetAbstractBreakpointType.class);
			}
		});
	}

	public void addCommand(Processor<VirtualMachine> processor)
	{
		myQueue.add(processor);
	}

	public boolean isConnected()
	{
		return myVirtualMachine != null;
	}
}
