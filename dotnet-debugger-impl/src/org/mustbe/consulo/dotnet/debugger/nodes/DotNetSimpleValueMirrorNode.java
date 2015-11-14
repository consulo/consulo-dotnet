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

package org.mustbe.consulo.dotnet.debugger.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import com.intellij.xdebugger.frame.XValueModifier;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DotNetSimpleValueMirrorNode extends DotNetAbstractVariableMirrorNode
{
	@NotNull
	private final Value<?> myValue;

	public DotNetSimpleValueMirrorNode(@NotNull DotNetDebugContext debuggerContext, @NotNull String name, @NotNull ThreadMirror threadMirror,
			@NotNull Value<?> value)
	{
		super(debuggerContext, name, threadMirror);

		myValue = value;
	}

	@Nullable
	@Override
	public XValueModifier getModifier()
	{
		return null;
	}

	@Nullable
	@Override
	public Value<?> getValueOfVariableImpl()
	{
		return myValue;
	}

	@Override
	public void setValueForVariableImpl(@NotNull Value<?> value)
	{
	}

	@Nullable
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myValue.type();
	}
}