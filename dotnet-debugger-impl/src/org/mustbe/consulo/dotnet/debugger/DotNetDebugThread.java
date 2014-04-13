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
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetAbstractBreakpointType;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import lombok.val;
import mono.debugger.SocketListeningConnector;
import mono.debugger.VirtualMachine;
import mono.debugger.connect.Connector;
import mono.debugger.event.EventQueue;
import mono.debugger.event.EventSet;
import mono.debugger.request.EventRequest;

/**
 * @author VISTALL
 * @since 10.04.14
 */
@Logger
public class DotNetDebugThread extends Thread
{
	private final XDebugSession mySession;
	private final XDebuggerManager myDebuggerManager;
	private final DebugConnectionInfo myDebugConnectionInfo;
	private boolean myStop;

	private VirtualMachine myVirtualMachine;

	private Queue<Processor<VirtualMachine>> myQueue = new ConcurrentLinkedDeque<Processor<VirtualMachine>>();

	public DotNetDebugThread(XDebugSession session, DebugConnectionInfo debugConnectionInfo)
	{
		super("DotNetDebugThread: " + new Random().nextInt());
		mySession = session;
		myDebugConnectionInfo = debugConnectionInfo;
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

		for(val breakpoint : getOurBreakpoints())
		{
			val type = (DotNetAbstractBreakpointType) breakpoint.getType();

			EventRequest eventRequest = type.createEventRequest(mySession.getProject(), myVirtualMachine, breakpoint);
			if(eventRequest == null)
			{
				continue;
			}
			eventRequest.enable();
		}

		while(!myStop)
		{
			Processor<VirtualMachine> processor;
			while((processor = myQueue.poll()) != null)
			{
				processor.process(myVirtualMachine);
			}

			boolean stoppedAlready = false;
			EventQueue eventQueue = myVirtualMachine.eventQueue();
			EventSet eventSet;
			try
			{
				while((eventSet = eventQueue.remove(100)) != null)
				{
					if(!stoppedAlready && eventSet.suspendPolicy() == EventRequest.SUSPEND_ALL)
					{
						stoppedAlready = true;

						mySession.positionReached(new DotNetSuspendContext(myVirtualMachine, mySession.getProject(), eventSet.eventThread()));
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
