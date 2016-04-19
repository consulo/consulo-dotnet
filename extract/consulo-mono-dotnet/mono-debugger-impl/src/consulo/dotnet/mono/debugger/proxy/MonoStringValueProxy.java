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

import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import mono.debugger.StringValueMirror;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public class MonoStringValueProxy extends MonoValueProxyBase<StringValueMirror> implements DotNetStringValueProxy
{
	public MonoStringValueProxy(StringValueMirror value)
	{
		super(value);
	}

	@Nullable
	@Override
	public String getValue()
	{
		return (String) super.getValue();
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitStringValue(this);
	}
}
