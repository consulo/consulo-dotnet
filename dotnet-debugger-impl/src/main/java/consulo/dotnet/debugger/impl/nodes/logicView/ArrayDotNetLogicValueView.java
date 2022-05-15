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

package consulo.dotnet.debugger.impl.nodes.logicView;

import consulo.execution.debug.frame.XNamedValue;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.impl.nodes.DotNetArrayValueNode;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetArrayValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class ArrayDotNetLogicValueView extends LimitableDotNetLogicValueView<DotNetArrayValueProxy>
{
	@Override
	public boolean canHandle(@Nonnull DotNetDebugContext debugContext, @Nonnull DotNetTypeProxy typeMirror)
	{
		return typeMirror.isArray();
	}

	@Override
	public int getSize(@Nonnull DotNetArrayValueProxy value)
	{
		return value.getLength();
	}

	@Override
	public boolean isMyValue(@Nonnull DotNetValueProxy value)
	{
		return value instanceof DotNetArrayValueProxy;
	}

	@Nonnull
	@Override
	public XNamedValue createChildValue(int index, @Nonnull DotNetDebugContext context, @Nonnull DotNetStackFrameProxy frameProxy, @Nonnull DotNetArrayValueProxy value)
	{
		return new DotNetArrayValueNode(context, "[" + index + "]", frameProxy, value, index);
	}
}
