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

package org.mustbe.consulo.dotnet.debugger.nodes;

import java.util.Map;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueModifier;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.StructValueMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 05.01.16
 */
public class DotNetThisAsStructValueMirrorNode extends DotNetAbstractVariableMirrorNode
{
	@NotNull
	private final TypeMirror myTypeMirror;
	private final StructValueMirror myValue;

	public DotNetThisAsStructValueMirrorNode(@NotNull DotNetDebugContext debuggerContext, @NotNull ThreadMirror threadMirror, @NotNull TypeMirror typeMirror, @NotNull StructValueMirror value)
	{
		super(debuggerContext, "this", threadMirror);
		myTypeMirror = typeMirror;
		myValue = value;
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
		return AllIcons.Debugger.Value;
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

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		final XValueChildrenList childrenList = new XValueChildrenList();

		Map<FieldOrPropertyMirror, Value<?>> map = myValue.map();
		for(Map.Entry<FieldOrPropertyMirror, Value<?>> entry : map.entrySet())
		{
			FieldOrPropertyMirror key = entry.getKey();
			Value<?> value = entry.getValue();

			childrenList.add(new DotNetFieldOrPropertyMirrorNode(myDebugContext, key, myThreadMirror, null, value));
		}

		node.addChildren(childrenList, true);
	}

	@NotNull
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myTypeMirror;
	}
}
