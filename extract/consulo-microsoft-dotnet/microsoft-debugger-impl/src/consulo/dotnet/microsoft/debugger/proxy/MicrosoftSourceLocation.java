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
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClientContext;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetFramesRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftSourceLocation implements DotNetSourceLocation
{
	private MicrosoftDebuggerClientContext myContext;
	private GetFramesRequestResult.FrameInfo.SourcePosition myPosition;
	private int myModuleToken;
	private int myClassToken;
	private int myFunctionToken;

	public MicrosoftSourceLocation(MicrosoftDebuggerClientContext context, GetFramesRequestResult.FrameInfo.SourcePosition position, int moduleToken, int classToken, int functionToken)
	{
		myContext = context;
		myPosition = position;
		myModuleToken = moduleToken;
		myClassToken = classToken;
		myFunctionToken = functionToken;
	}

	@Nullable
	@Override
	public String getFilePath()
	{
		return myPosition.FilePath;
	}

	@Override
	public int getLine()
	{
		return myPosition.Line;
	}

	@Override
	public int getColumn()
	{
		return myPosition.Column;
	}

	@NotNull
	@Override
	public DotNetMethodProxy getMethod()
	{
		return new MicrosoftMethodProxy(myContext, myModuleToken, myClassToken, myFunctionToken);
	}
}
