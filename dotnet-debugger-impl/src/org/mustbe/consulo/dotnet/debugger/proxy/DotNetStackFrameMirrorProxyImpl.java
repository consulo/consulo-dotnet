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

package org.mustbe.consulo.dotnet.debugger.proxy;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import mono.debugger.LocalVariableOrParameterMirror;
import mono.debugger.Location;
import mono.debugger.StackFrameMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.Value;
import mono.debugger.util.ImmutablePair;

/**
 * @author VISTALL
 * @since 26.03.2016
 */
public class DotNetStackFrameMirrorProxyImpl implements DotNetStackFrameMirrorProxy
{
	private StackFrameMirror myStackFrameMirror;
	private int myIndex;

	public DotNetStackFrameMirrorProxyImpl(StackFrameMirror stackFrameMirror, int index)
	{
		myStackFrameMirror = stackFrameMirror;
		myIndex = index;
	}

	@Override
	@NotNull
	public Value thisObject()
	{
		return getRefreshedFrame().thisObject();
	}

	@Override
	@NotNull
	public ThreadMirror thread()
	{
		return myStackFrameMirror.thread();
	}

	@Override
	@NotNull
	public Location location()
	{
		return myStackFrameMirror.location();
	}

	@Override
	@Nullable
	public Value<?> localOrParameterValue(LocalVariableOrParameterMirror mirror)
	{
		return getRefreshedFrame().localOrParameterValue(mirror);
	}

	@Override
	public void setLocalOrParameterValues(@NotNull ImmutablePair<LocalVariableOrParameterMirror, Value<?>>... pairs)
	{
		getRefreshedFrame().setLocalOrParameterValues(pairs);
	}

	@NotNull
	private StackFrameMirror getRefreshedFrame()
	{
		try
		{
			List<StackFrameMirror> frames = myStackFrameMirror.thread().frames();
			return frames.get(myIndex);
		}
		catch(Exception e)
		{
			return myStackFrameMirror;
		}
	}
}
