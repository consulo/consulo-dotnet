/*
 * Copyright 2013-2014 must-be.org
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

package consulo.dotnet.debugger.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetArrayValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 26.04.14
 */
public class DotNetArrayValueMirrorNode extends DotNetAbstractVariableMirrorNode
{
	@NotNull
	private final DotNetArrayValueProxy myArrayValueMirror;
	private final int myIndex;
	@Nullable
	private final DotNetValueProxy myValue;

	public DotNetArrayValueMirrorNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull String name,
			@NotNull DotNetThreadProxy threadMirror,
			@NotNull DotNetArrayValueProxy valueMirrorNode,
			int index)
	{
		super(debuggerContext, name, threadMirror);
		myArrayValueMirror = valueMirrorNode;
		myIndex = index;
		myValue = valueMirrorNode.get(index);
	}

	@Nullable
	@Override
	public DotNetValueProxy getValueOfVariableImpl()
	{
		return myValue;
	}

	@Override
	public void setValueForVariableImpl(@NotNull DotNetValueProxy value)
	{
		myArrayValueMirror.set(myIndex, value);
	}

	@Nullable
	@Override
	public DotNetTypeProxy getTypeOfVariable()
	{
		return myValue == null ? null : myValue.getType();
	}
}
