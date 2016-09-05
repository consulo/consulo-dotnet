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

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.util.ArrayUtil2;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import mssdw.StackFrameMirror;
import mssdw.ThreadMirror;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftThreadProxy extends DotNetThreadProxy
{
	private MicrosoftVirtualMachineProxy myVirtualMachineProxy;
	private ThreadMirror myThreadMirror;

	public MicrosoftThreadProxy(MicrosoftVirtualMachineProxy virtualMachineProxy, ThreadMirror threadMirror)
	{
		myVirtualMachineProxy = virtualMachineProxy;
		myThreadMirror = threadMirror;
	}

	public ThreadMirror getThreadMirror()
	{
		return myThreadMirror;
	}

	@Override
	public long getId()
	{
		return myThreadMirror.id();
	}

	@Override
	public boolean isRunning()
	{
		return myThreadMirror.isRunning();
	}

	@Override
	public boolean isSuspended()
	{
		return myThreadMirror.isSuspended();
	}

	@Nullable
	@Override
	public String getName()
	{
		return myThreadMirror.name();
	}

	@NotNull
	@Override
	public List<DotNetStackFrameProxy> getFrames()
	{
		List<StackFrameMirror> frames = myThreadMirror.frames();
		List<DotNetStackFrameProxy> proxies = new ArrayList<DotNetStackFrameProxy>(frames.size());
		for(int i = 0; i < frames.size(); i++)
		{
			StackFrameMirror frameMirror = frames.get(i);
			proxies.add(new MicrosoftStackFrameProxy(i, myVirtualMachineProxy, frameMirror));
		}
		return proxies;
	}

	@Nullable
	@Override
	public DotNetStackFrameProxy getFrame(int index)
	{
		List<StackFrameMirror> frames = myThreadMirror.frames();

		StackFrameMirror frameMirror = ArrayUtil2.safeGet(frames, index);
		if(frameMirror != null)
		{
			return new MicrosoftStackFrameProxy(index, myVirtualMachineProxy, frameMirror);
		}
		return null;
	}
}
