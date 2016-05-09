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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetSourceLocation;
import mssdw.MethodMirror;
import mssdw.StackFrameMirror;

/**
 * @author VISTALL
 * @since 5/8/2016
 */
public class MicrosoftSourceLocation implements DotNetSourceLocation
{
	private StackFrameMirror myFrameMirror;

	public MicrosoftSourceLocation(StackFrameMirror frameMirror)
	{
		myFrameMirror = frameMirror;
	}

	@Nullable
	@Override
	public String getFilePath()
	{
		return myFrameMirror.getFilePath();
	}

	@Override
	public int getLineZeroBased()
	{
		return myFrameMirror.getLine() - 1;
	}

	@Override
	public int getLineOneBased()
	{
		return myFrameMirror.getLine();
	}

	@Override
	public int getColumn()
	{
		return myFrameMirror.getColumn();
	}

	@NotNull
	@Override
	public DotNetMethodProxy getMethod()
	{
		return new MicrosoftMethodProxy(new MethodMirror(myFrameMirror.virtualMachine(), myFrameMirror.getTypeRef(), myFrameMirror.getFunctionId()));
	}
}
