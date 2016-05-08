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
import consulo.dotnet.microsoft.debugger.MicrosoftDebuggerClient;
import consulo.dotnet.microsoft.debugger.protocol.TypeRef;
import consulo.dotnet.microsoft.debugger.protocol.serverMessage.GetFramesRequestResult;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
@Deprecated
public class MicrosoftSourceLocationOld implements DotNetSourceLocation
{
	private GetFramesRequestResult.FrameInfo.SourcePosition myPosition;

	private final DotNetMethodProxy myMethodProxy;

	public MicrosoftSourceLocationOld(MicrosoftDebuggerClient context, GetFramesRequestResult.FrameInfo.SourcePosition position, TypeRef typeRef, int functionToken)
	{
		myPosition = position;
		myMethodProxy = new MicrosoftMethodProxyOld(context, typeRef, functionToken);
	}

	@Nullable
	@Override
	public String getFilePath()
	{
		return myPosition.FilePath;
	}

	@Override
	public int getLineZeroBased()
	{
		return myPosition.Line - 1;
	}

	@Override
	public int getLineOneBased()
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
		return myMethodProxy;
	}
}
