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
import consulo.dotnet.debugger.proxy.value.DotNetArrayValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;
import mssdw.ArrayValueMirror;

/**
 * @author VISTALL
 * @since 5/9/2016
 */
public class MicrosoftArrayValueProxy extends MicrosoftValueProxyBase<ArrayValueMirror> implements DotNetArrayValueProxy
{
	public MicrosoftArrayValueProxy(ArrayValueMirror value)
	{
		super(value);
	}

	@Override
	public void accept(DotNetValueProxyVisitor visitor)
	{
		visitor.visitArrayValue(this);
	}

	@Override
	public long getAddress()
	{
		return myValue.object().address();
	}

	@Override
	public int getLength()
	{
		return myValue.length();
	}

	@Nullable
	@Override
	public DotNetValueProxy get(int index)
	{
		return MicrosoftValueProxyUtil.wrap(myValue.get(index));
	}

	@Override
	public void set(int index, @NotNull DotNetValueProxy proxy)
	{
		//MonoValueProxyBase<?> valueProxyBase = (MonoValueProxyBase<?>) proxy;
		//myValue.set(index, valueProxyBase.getMirror());
	}
}
