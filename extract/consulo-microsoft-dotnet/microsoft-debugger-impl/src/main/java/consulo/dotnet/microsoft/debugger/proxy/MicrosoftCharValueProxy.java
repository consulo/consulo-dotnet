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
import consulo.dotnet.debugger.proxy.value.DotNetCharValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import mssdw.CharValueMirror;

/**
 * @author VISTALL
 * @since 5/9/2016
 */
public class MicrosoftCharValueProxy extends MicrosoftValueProxyBase<CharValueMirror> implements DotNetCharValueProxy
{
	public MicrosoftCharValueProxy(CharValueMirror value)
	{
		super(value);
	}

	@NotNull
	@Override
	public Character getValue()
	{
		return (Character) super.getValue();
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitCharValue(this);
	}
}

