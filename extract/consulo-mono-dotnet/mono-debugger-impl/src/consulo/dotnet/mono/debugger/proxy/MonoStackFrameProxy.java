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
import consulo.dotnet.debugger.proxy.DotNetAbsentInformationException;
import consulo.dotnet.debugger.proxy.DotNetInvalidObjectException;
import consulo.dotnet.debugger.proxy.DotNetInvalidStackFrameException;
import consulo.dotnet.debugger.proxy.DotNetLocalVariableProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodParameterProxy;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import mono.debugger.AbsentInformationException;
import mono.debugger.InvalidObjectException;
import mono.debugger.InvalidStackFrameException;
import mono.debugger.LocalVariableOrParameterMirror;
import mono.debugger.StackFrameMirror;
import mono.debugger.Value;
import mono.debugger.util.ImmutablePair;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MonoStackFrameProxy implements DotNetStackFrameProxy
{
	private final int myIndex;
	private final MonoVirtualMachineProxy myVirtualMachineProxy;
	private final StackFrameMirror myFrameMirror;

	private final int myMethodId;

	public MonoStackFrameProxy(int index, MonoVirtualMachineProxy virtualMachineProxy, StackFrameMirror frameMirror)
	{
		myIndex = index;
		myVirtualMachineProxy = virtualMachineProxy;
		myFrameMirror = frameMirror;

		myMethodId = frameMirror.location().method().id();
	}

	@NotNull
	public StackFrameMirror getFrameMirror()
	{
		return getRefreshedFrame();
	}

	@NotNull
	@Override
	public DotNetValueProxy getThisObject() throws DotNetInvalidObjectException, DotNetInvalidStackFrameException, DotNetAbsentInformationException
	{
		try
		{
			return MonoValueProxyUtil.wrap(getRefreshedFrame().thisObject());
		}
		catch(AbsentInformationException e)
		{
			throw new DotNetAbsentInformationException(e);
		}
		catch(InvalidObjectException e)
		{
			throw new DotNetInvalidObjectException(e);
		}
		catch(InvalidStackFrameException e)
		{
			throw new DotNetInvalidStackFrameException(e);
		}
	}

	@Nullable
	@Override
	public DotNetValueProxy getParameterValue(@NotNull DotNetMethodParameterProxy parameterProxy)
	{
		MonoMethodParameterProxy methodParameterProxy = (MonoMethodParameterProxy) parameterProxy;
		return MonoValueProxyUtil.wrap(getRefreshedFrame().localOrParameterValue(methodParameterProxy.getParameter()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setParameterValue(@NotNull DotNetMethodParameterProxy parameterProxy, @NotNull DotNetValueProxy valueProxy)
	{
		MonoMethodParameterProxy monoMethodParameterProxy = (MonoMethodParameterProxy) parameterProxy;

		Value value = ((MonoValueProxyBase) valueProxy).getMirror();

		getRefreshedFrame().setLocalOrParameterValues(new ImmutablePair<LocalVariableOrParameterMirror, Value<?>>(monoMethodParameterProxy.getParameter(), value));
	}

	@Nullable
	@Override
	public DotNetValueProxy getLocalValue(@NotNull DotNetLocalVariableProxy localVariableProxy)
	{
		MonoLocalVariableProxy methodParameterProxy = (MonoLocalVariableProxy) localVariableProxy;
		return MonoValueProxyUtil.wrap(getRefreshedFrame().localOrParameterValue(methodParameterProxy.getMirror()));

	}

	@Override
	@SuppressWarnings("unchecked")
	public void setLocalValue(@NotNull DotNetLocalVariableProxy localVariableProxy, @NotNull DotNetValueProxy valueProxy)
	{
		MonoLocalVariableProxy monoLocalVariableProxy = (MonoLocalVariableProxy) localVariableProxy;

		Value value = ((MonoValueProxyBase) valueProxy).getMirror();

		getRefreshedFrame().setLocalOrParameterValues(new ImmutablePair<LocalVariableOrParameterMirror, Value<?>>(monoLocalVariableProxy.getMirror(), value));
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
		return myMethodId;
	}

	@Nullable
	@Override
	public DotNetSourceLocation getSourceLocation()
	{
		return new MonoSourceLocation(getRefreshedFrame().location());
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
