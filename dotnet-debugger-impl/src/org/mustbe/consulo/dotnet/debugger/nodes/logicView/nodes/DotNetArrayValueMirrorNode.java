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

package org.mustbe.consulo.dotnet.debugger.nodes.logicView.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetAbstractVariableMirrorNode;
import mono.debugger.ArrayValueMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 26.04.14
 */
public class DotNetArrayValueMirrorNode extends DotNetAbstractVariableMirrorNode
{
	@NotNull
	private final ArrayValueMirror myArrayValueMirror;
	private final int myIndex;
	@NotNull
	private final Value<?> myValue;

	public DotNetArrayValueMirrorNode(
			@NotNull DotNetDebugContext debuggerContext,
			@NotNull String name,
			@NotNull ThreadMirror threadMirror,
			@NotNull ArrayValueMirror arrayValueMirror,
			int index)
	{
		super(debuggerContext, name, threadMirror);
		myArrayValueMirror = arrayValueMirror;
		myIndex = index;
		myValue = arrayValueMirror.get(index);
	}

	@Nullable
	@Override
	public Value<?> getValueOfVariable()
	{
		return myValue;
	}

	@Override
	public void setValueForVariable(@NotNull Value<?> value)
	{
		myArrayValueMirror.set(myIndex, value);
	}

	@Nullable
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myValue.type();
	}
}
