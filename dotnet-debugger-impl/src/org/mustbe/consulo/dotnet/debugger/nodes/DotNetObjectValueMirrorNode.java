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

import java.util.List;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import mono.debugger.FieldOrPropertyMirror;
import mono.debugger.ObjectValueMirror;
import mono.debugger.PropertyMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetObjectValueMirrorNode extends DotNetAbstractVariableMirrorNode
{
	@NotNull
	private final TypeMirror myTypeMirror;
	private final ObjectValueMirror myObjectValueMirror;

	public DotNetObjectValueMirrorNode(
			@NotNull DotNetDebugContext debuggerContext,
			@NotNull ThreadMirror threadMirror,
			@NotNull TypeMirror typeMirror,
			@Nullable ObjectValueMirror objectValueMirror)
	{
		super(debuggerContext, objectValueMirror == null ? "static" : "this", threadMirror);
		myTypeMirror = typeMirror;
		myObjectValueMirror = objectValueMirror;
	}

	@NotNull
	@Override
	public Icon getIconForVariable()
	{
		return myObjectValueMirror == null ? AllIcons.Nodes.Static : AllIcons.Debugger.Value;
	}

	@Nullable
	@Override
	public Value<?> getValueOfVariable()
	{
		return myObjectValueMirror;
	}

	@Override
	public void setValueForVariable(@NotNull Value<?> value)
	{

	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		XValueChildrenList childrenList = new XValueChildrenList();

		List<FieldOrPropertyMirror> fieldMirrors = myTypeMirror.fieldAndProperties(true);
		for(FieldOrPropertyMirror fieldMirror : fieldMirrors)
		{
			if(!fieldMirror.isStatic() && myObjectValueMirror == null)
			{
				continue;
			}

			if(fieldMirror instanceof PropertyMirror && ((PropertyMirror) fieldMirror).isArrayProperty())
			{
				continue;
			}
			childrenList.add(new DotNetFieldOrPropertyMirrorNode(myDebugContext, fieldMirror, myThreadMirror,
					fieldMirror.isStatic() ? null : myObjectValueMirror));
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
