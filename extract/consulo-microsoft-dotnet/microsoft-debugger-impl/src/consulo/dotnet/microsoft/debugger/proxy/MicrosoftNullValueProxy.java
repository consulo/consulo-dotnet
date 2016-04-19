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
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNullValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MicrosoftNullValueProxy implements DotNetNullValueProxy
{
	@Nullable
	@Override
	public DotNetTypeProxy getType()
	{
		return null;
	}

	@NotNull
	@Override
	public Object getValue()
	{
		return null;
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitNullValue(this);
	}
}
