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
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetAbstractBreakpointType;
import org.mustbe.consulo.dotnet.debugger.linebreakType.DotNetLineBreakpointType;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ArrayUtil;
import com.intellij.util.EventDispatcher;
import com.intellij.util.Processor;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import lombok.val;
import mono.debugger.EventKind;
import mono.debugger.Location;
import mono.debugger.SocketAttachingConnector;
import mono.debugger.SocketListeningConnector;
import mono.debugger.SuspendPolicy;
import mono.debugger.TypeMirror;
import mono.debugger.VirtualMachine;
import mono.debugger.connect.Connector;
import mono.debugger.event.Event;
import mono.debugger.event.EventQueue;
import mono.debugger.event.EventSet;
import mono.debugger.event.TypeLoadEvent;
import mono.debugger.event.VMDeathEvent;
import mono.debugger.request.BreakpointRequest;
import mono.debugger.request.EventRequest;
import mono.debugger.request.StepRequest;
import mono.debugger.request.TypeLoadRequest;

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

	private Queue<Processor<VirtualMachine>> myQueue = new ConcurrentLinkedQueue<Processor<VirtualMachine>>();

	private VirtualMachine myVirtualMachine;

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

		myVirtualMachine = virtualMachine;

		virtualMachine.enableEvents(EventKind.ASSEMBLY_LOAD, EventKind.THREAD_START, EventKind.THREAD_DEATH, EventKind.ASSEMBLY_UNLOAD,
				EventKind.USER_BREAK, EventKind.USER_LOG);

		TypeLoadRequest typeLoad = virtualMachine.eventRequestManager().createTypeLoad();
		if(virtualMachine.isAtLeastVersion(2, 9))
		{
			Set<String> files = new HashSet<String>();
			Collection<? extends XLineBreakpoint<XBreakpointProperties>> ourBreakpoints = getOurBreakpoints();
			for(final XLineBreakpoint<XBreakpointProperties> ourBreakpoint : ourBreakpoints)
			{
				files.add(ourBreakpoint.getPresentableFilePath());
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
			processCommands(virtualMachine);

			boolean stoppedAlready = false;
			EventQueue eventQueue = virtualMachine.eventQueue();
			EventSet eventSet;
			try
			{
				l:
				while((eventSet = eventQueue.remove(50)) != null)
				{
					Location location = null;

					for(final Event event : eventSet)
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

						if(event instanceof TypeLoadEvent)
						{
							insertBreakpoints(virtualMachine, ((TypeLoadEvent) event).typeMirror());
							continue l;
						}

						if(event instanceof VMDeathEvent)
						{
							connectionStopped();
							return;
						}
					}

					if(!stoppedAlready && eventSet.suspendPolicy() == SuspendPolicy.ALL)
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
				Thread.sleep(50);
			}
			catch(InterruptedException e)
			{
				//
			}
		}
	}

	private void insertBreakpoints(final VirtualMachine virtualMachine, final TypeMirror typeMirror)
	{
		ApplicationManager.getApplication().runReadAction(new Runnable()
		{
			@Override
			public void run()
			{
				DotNetDebugContext debugContext = createDebugContext();

				DotNetTypeDeclaration[] qualifiedNameImpl = DotNetVirtualMachineUtil.findTypesByQualifiedName(typeMirror, debugContext);
				if(qualifiedNameImpl.length != 0)
				{
					Collection<? extends XLineBreakpoint<XBreakpointProperties>> breakpoints = getOurBreakpoints();
					for(DotNetTypeDeclaration dotNetTypeDeclaration : qualifiedNameImpl)
					{
						VirtualFile typeVirtualFile = dotNetTypeDeclaration.getContainingFile().getVirtualFile();

						for(final XLineBreakpoint<XBreakpointProperties> breakpoint : breakpoints)
						{
							VirtualFile lineBreakpoint = VirtualFileManager.getInstance().findFileByUrl(breakpoint.getFileUrl());
							if(!Comparing.equal(typeVirtualFile, lineBreakpoint))
							{
								continue;
							}

							val type = (DotNetLineBreakpointType) breakpoint.getType();

							type.createRequest(mySession.getProject(), virtualMachine, breakpoint, typeMirror);
						}
					}
				}

				virtualMachine.resume();
			}
		});
	}

	private void processCommands(VirtualMachine virtualMachine)
	{
		Processor<VirtualMachine> processor;
		while((processor = myQueue.poll()) != null)
		{
			if(processor.process(virtualMachine))
			{
				virtualMachine.resume();
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

	public void processAnyway(Processor<VirtualMachine> processor)
	{
		if(myVirtualMachine == null)
		{
			return;
		}
		processor.process(myVirtualMachine);
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
