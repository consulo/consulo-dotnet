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

package consulo.dotnet.mono.debugger.proxy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.util.BitUtil;
import consulo.dotnet.debugger.proxy.DotNetNotSuspendedException;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.util.ArrayUtil2;
import mono.debugger.NotSuspendedException;
import mono.debugger.StackFrameMirror;
import mono.debugger.ThreadMirror;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MonoThreadProxy extends DotNetThreadProxy
{
	private MonoVirtualMachineProxy myVirtualMachineProxy;
	private ThreadMirror myThreadMirror;

	private final NotNullLazyValue<Long> myIdValue = NotNullLazyValue.createValue(() -> getIdFromThread(myVirtualMachineProxy, myThreadMirror));
	private final NotNullLazyValue<String> myNameValue = NotNullLazyValue.createValue(() -> myThreadMirror.name());

	public MonoThreadProxy(MonoVirtualMachineProxy virtualMachineProxy, ThreadMirror threadMirror)
	{
		myVirtualMachineProxy = virtualMachineProxy;
		myThreadMirror = threadMirror;
	}

	public ThreadMirror getThreadMirror()
	{
		return myThreadMirror;
	}

	public static long getIdFromThread(MonoVirtualMachineProxy proxy, ThreadMirror mirror)
	{
		if(proxy.isSupportSystemThreadId())
		{
			return mirror.systemThreadId();
		}
		else
		{
			return mirror.id();
		}
	}

	@Override
	public long getId()
	{
		return myIdValue.getValue();
	}

	@Override
	public boolean isRunning()
	{
		int state = myThreadMirror.state();
		return BitUtil.isSet(state, ThreadMirror.ThreadState.Running);
	}

	@Override
	public boolean isSuspended()
	{
		int state = myThreadMirror.state();
		return BitUtil.isSet(state, ThreadMirror.ThreadState.Suspended);
	}

	@Nullable
	@Override
	public String getName()
	{
		return myNameValue.getValue();
	}

	@Nonnull
	@Override
	public List<DotNetStackFrameProxy> getFrames() throws DotNetNotSuspendedException
	{
		try
		{
			List<StackFrameMirror> frames = myThreadMirror.frames();
			List<DotNetStackFrameProxy> proxies = new ArrayList<>(frames.size());
			for(int i = 0; i < frames.size(); i++)
			{
				StackFrameMirror frameMirror = frames.get(i);
				proxies.add(new MonoStackFrameProxy(i, myVirtualMachineProxy, frameMirror));
			}
			return proxies;
		}
		catch(NotSuspendedException e)
		{
			throw new DotNetNotSuspendedException(e);
		}
	}

	@Nullable
	@Override
	public DotNetStackFrameProxy getFrame(int index) throws DotNetNotSuspendedException
	{
		try
		{
			List<StackFrameMirror> frames = myThreadMirror.frames();

			StackFrameMirror frameMirror = ArrayUtil2.safeGet(frames, index);
			if(frameMirror != null)
			{
				return new MonoStackFrameProxy(index, myVirtualMachineProxy, frameMirror);
			}
			return null;
		}
		catch(NotSuspendedException e)
		{
			throw new DotNetNotSuspendedException(e);
		}
	}
}
