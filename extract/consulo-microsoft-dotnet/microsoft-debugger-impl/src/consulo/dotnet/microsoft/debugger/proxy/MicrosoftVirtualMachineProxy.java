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

package consulo.dotnet.microsoft.debugger.proxy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.DotNetVirtualMachineProxy;
import consulo.dotnet.debugger.proxy.value.DotNetBooleanValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetCharValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNullValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNumberValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import mssdw.BooleanValueMirror;
import mssdw.CharValueMirror;
import mssdw.NoObjectValueMirror;
import mssdw.NumberValueMirror;
import mssdw.ThreadMirror;
import mssdw.VirtualMachine;
import mssdw.request.EventRequest;
import mssdw.request.EventRequestManager;
import mssdw.request.StepRequest;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftVirtualMachineProxy implements DotNetVirtualMachineProxy
{
	private final Set<StepRequest> myStepRequests = ContainerUtil.newLinkedHashSet();
	private final MultiMap<XBreakpoint, EventRequest> myBreakpointEventRequests = MultiMap.create();
	private final List<String> myLoadedModules = new CopyOnWriteArrayList<String>();

	private final VirtualMachine myVirtualMachine;

	public MicrosoftVirtualMachineProxy(@NotNull VirtualMachine virtualMachine)
	{
		myVirtualMachine = virtualMachine;
	}

	@Nullable
	@Override
	public DotNetTypeProxy findType(@NotNull Project project, @NotNull String vmQName, @NotNull VirtualFile virtualFile)
	{
		return null;
	}

	@NotNull
	@Override
	public List<DotNetThreadProxy> getThreads()
	{
		return ContainerUtil.map(myVirtualMachine.allThreads(), new Function<ThreadMirror, DotNetThreadProxy>()
		{
			@Override
			public DotNetThreadProxy fun(ThreadMirror threadMirror)
			{
				return new MicrosoftThreadProxy(MicrosoftVirtualMachineProxy.this, threadMirror);
			}
		});
	}

	@NotNull
	@Override
	public DotNetStringValueProxy createStringValue(@NotNull String value)
	{
		throw new UnsupportedOperationException();
	}

	@NotNull
	@Override
	public DotNetCharValueProxy createCharValue(char value)
	{
		return MicrosoftValueProxyUtil.wrap(new CharValueMirror(myVirtualMachine, value));
	}

	@NotNull
	@Override
	public DotNetBooleanValueProxy createBooleanValue(boolean value)
	{
		return MicrosoftValueProxyUtil.wrap(new BooleanValueMirror(myVirtualMachine, value));
	}

	@NotNull
	@Override
	public DotNetNumberValueProxy createNumberValue(int tag, @NotNull Number value)
	{
		return MicrosoftValueProxyUtil.wrap(new NumberValueMirror(myVirtualMachine, tag, value));
	}

	@NotNull
	@Override
	public DotNetNullValueProxy createNullValue()
	{
		return MicrosoftValueProxyUtil.wrap(new NoObjectValueMirror(myVirtualMachine));
	}

	public void dispose()
	{
		myStepRequests.clear();
		myVirtualMachine.dispose();
		myBreakpointEventRequests.clear();
	}

	public void addStepRequest(@NotNull StepRequest stepRequest)
	{
		myStepRequests.add(stepRequest);
	}

	public void stopStepRequest(@NotNull StepRequest stepRequest)
	{
		stepRequest.disable();
		myStepRequests.remove(stepRequest);
	}

	public void putRequest(@NotNull XBreakpoint<?> breakpoint, @NotNull EventRequest request)
	{
		myBreakpointEventRequests.putValue(breakpoint, request);
	}

	@Nullable
	public XBreakpoint<?> findBreakpointByRequest(@NotNull EventRequest eventRequest)
	{
		for(Map.Entry<XBreakpoint, Collection<EventRequest>> entry : myBreakpointEventRequests.entrySet())
		{
			if(entry.getValue().contains(eventRequest))
			{
				return entry.getKey();
			}
		}
		return null;
	}

	public void stopBreakpointRequests(XBreakpoint<?> breakpoint)
	{
		Collection<EventRequest> eventRequests = myBreakpointEventRequests.remove(breakpoint);
		if(eventRequests == null)
		{
			return;
		}
		for(EventRequest eventRequest : eventRequests)
		{
			eventRequest.disable();
		}
		myVirtualMachine.eventRequestManager().deleteEventRequests(eventRequests);
	}

	public void stopStepRequests()
	{
		for(StepRequest stepRequest : myStepRequests)
		{
			stepRequest.disable();
		}
		myStepRequests.clear();
	}

	public EventRequestManager eventRequestManager()
	{
		return myVirtualMachine.eventRequestManager();
	}

	@NotNull
	public VirtualMachine getDelegate()
	{
		return myVirtualMachine;
	}

	@NotNull
	private static String getAssemblyName(String name)
	{
		int i = name.indexOf(',');
		if(i == -1)
		{
			return name;
		}
		return name.substring(0, i);
	}

	public void resume()
	{
		myVirtualMachine.resume();
	}

	public void suspend()
	{
		myVirtualMachine.suspend();
	}

	@NotNull
	public List<ThreadMirror> allThreads()
	{
		return myVirtualMachine.allThreads();
	}

	public void addLoadedModule(String path)
	{
		myLoadedModules.add(path);
	}

	public List<String> getLoadedModules()
	{
		return myLoadedModules;
	}
}
