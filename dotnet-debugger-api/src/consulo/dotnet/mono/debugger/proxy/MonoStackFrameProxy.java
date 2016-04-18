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

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetInvalidObjectException;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mono.debugger.InvalidObjectException;
import mono.debugger.StackFrameMirror;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MonoStackFrameProxy implements DotNetStackFrameProxy
{
	private int myIndex;
	private MonoVirtualMachineProxy myVirtualMachineProxy;
	private StackFrameMirror myFrameMirror;

	public MonoStackFrameProxy(int index, MonoVirtualMachineProxy virtualMachineProxy, StackFrameMirror frameMirror)
	{
		myIndex = index;
		myVirtualMachineProxy = virtualMachineProxy;
		myFrameMirror = frameMirror;
	}

	@NotNull
	@Override
	public DotNetValueProxy getThisObject() throws DotNetInvalidObjectException
	{
		try
		{
			myFrameMirror.thisObject();
		}
		catch(InvalidObjectException e)
		{
			throw new DotNetInvalidObjectException(e);
		}
		return null;
	}

	@NotNull
	@Override
	public DotNetThreadProxy getThread()
	{
		return new MonoThreadProxy(myVirtualMachineProxy, myFrameMirror.thread());
	}

	@Override
	public int getIndex()
	{
		return myIndex;
	}

	@NotNull
	@Override
	public Object getEqualityObject()
	{
		return myFrameMirror.location().method().id();
	}

	@Nullable
	@Override
	public DotNetSourceLocation getSourceLocation()
	{
		return new MonoSourceLocation(myFrameMirror.location());
	}

	@NotNull
	private StackFrameMirror getRefreshedFrame()
	{
		try
		{
			List<StackFrameMirror> frames = myFrameMirror.thread().frames();
			return frames.get(myIndex);
		}
		catch(Exception e)
		{
			return myFrameMirror;
		}
	}
}
