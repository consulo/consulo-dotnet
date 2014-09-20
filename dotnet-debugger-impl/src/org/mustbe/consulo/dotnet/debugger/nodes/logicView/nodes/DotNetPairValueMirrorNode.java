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

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetAbstractVariableMirrorNode;
import org.mustbe.consulo.dotnet.debugger.nodes.DotNetValuePresentation;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XValueModifier;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValuePresentationUtil;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DotNetPairValueMirrorNode extends DotNetAbstractVariableMirrorNode
{
	@NotNull
	private final Value<?> myValue;

	public DotNetPairValueMirrorNode(@NotNull DotNetDebugContext debuggerContext, @NotNull String name, @NotNull ThreadMirror threadMirror,
			@NotNull Value<?> first, @NotNull Value<?> value)
	{
		super(debuggerContext, getName(threadMirror, first), threadMirror);

		myValue = value;
	}

	private static String getName(@NotNull ThreadMirror threadMirror, Value<?> value)
	{
		return XValuePresentationUtil.computeValueText(new DotNetValuePresentation(threadMirror, null, value));
	}

	@Nullable
	@Override
	public XValueModifier getModifier()
	{
		return null;
	}

	@NotNull
	@Override
	public Icon getIconForVariable()
	{
		return AllIcons.Debugger.AutoVariablesMode;
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

	}

	@Nullable
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myValue.type();
	}
}