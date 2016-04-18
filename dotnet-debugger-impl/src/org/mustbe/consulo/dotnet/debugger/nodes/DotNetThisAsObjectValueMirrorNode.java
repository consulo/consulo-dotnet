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
import consulo.dotnet.debugger.DotNetDebugContext;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.Getter;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueModifier;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XRegularValuePresentation;
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
public class DotNetThisAsObjectValueMirrorNode extends DotNetAbstractVariableMirrorNode
{
	public static void addStaticNode(@NotNull XValueChildrenList list, @NotNull DotNetDebugContext debuggerContext, @NotNull ThreadMirror threadMirror, @NotNull TypeMirror typeMirror)
	{
		boolean result = processFieldOrProperty(typeMirror, null, CommonProcessors.<FieldOrPropertyMirror>alwaysFalse());
		if(result)
		{
			return;
		}
		list.add(new DotNetThisAsObjectValueMirrorNode(debuggerContext, threadMirror, typeMirror, (ObjectValueMirror) null));
	}

	@NotNull
	private final TypeMirror myTypeMirror;
	private final Getter<ObjectValueMirror> myObjectValueMirrorGetter;

	public DotNetThisAsObjectValueMirrorNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull ThreadMirror threadMirror,
			@NotNull TypeMirror typeMirror,
			@Nullable final ObjectValueMirror objectValueMirror)
	{
		this(debuggerContext, threadMirror, typeMirror, objectValueMirror == null ? null : new Getter<ObjectValueMirror>()
		{
			@Nullable
			@Override
			public ObjectValueMirror get()
			{
				return objectValueMirror;
			}
		});
	}

	public DotNetThisAsObjectValueMirrorNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull ThreadMirror threadMirror,
			@NotNull TypeMirror typeMirror,
			@Nullable Getter<ObjectValueMirror> objectValueMirrorGetter)
	{
		super(debuggerContext, objectValueMirrorGetter == null ? "static" : "this", threadMirror);
		myTypeMirror = typeMirror;
		myObjectValueMirrorGetter = objectValueMirrorGetter;
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
		return myObjectValueMirrorGetter == null ? AllIcons.Nodes.Static : AllIcons.Debugger.Value;
	}

	@Nullable
	@Override
	public Value<?> getValueOfVariableImpl()
	{
		return myObjectValueMirrorGetter == null ? null : myObjectValueMirrorGetter.get();
	}

	@Override
	public void setValueForVariableImpl(@NotNull Value<?> value)
	{

	}

	@Override
	public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace)
	{
		if(myObjectValueMirrorGetter == null)
		{
			xValueNode.setPresentation(getIconForVariable(), new XRegularValuePresentation("", null, ""), true);
		}
		else
		{
			super.computePresentation(xValueNode, xValuePlace);
		}
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		final XValueChildrenList childrenList = new XValueChildrenList();

		processFieldOrProperty(myTypeMirror, myObjectValueMirrorGetter, new Processor<FieldOrPropertyMirror>()
		{
			@Override
			public boolean process(FieldOrPropertyMirror fieldOrPropertyMirror)
			{
				childrenList.add(new DotNetFieldOrPropertyMirrorNode(myDebugContext, fieldOrPropertyMirror, myThreadMirror, fieldOrPropertyMirror.isStatic() ? null : myObjectValueMirrorGetter.get()));
				return true;
			}
		});
		node.addChildren(childrenList, true);
	}

	private static boolean processFieldOrProperty(@NotNull TypeMirror typeMirror, @Nullable Getter<ObjectValueMirror> objectValueMirrorGetter, @NotNull Processor<FieldOrPropertyMirror> processor)
	{
		List<FieldOrPropertyMirror> fieldMirrors = typeMirror.fieldAndProperties(true);
		for(FieldOrPropertyMirror fieldMirror : fieldMirrors)
		{
			if(!fieldMirror.isStatic() && objectValueMirrorGetter == null || fieldMirror.isStatic() && objectValueMirrorGetter != null)
			{
				continue;
			}

			if(fieldMirror instanceof PropertyMirror && ((PropertyMirror) fieldMirror).isArrayProperty())
			{
				continue;
			}

			if(DotNetDebuggerCompilerGenerateUtil.needSkipVariableByName(fieldMirror.name()))
			{
				continue;
			}

			if(!processor.process(fieldMirror))
			{
				return false;
			}
		}
		return true;
	}

	@NotNull
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myTypeMirror;
	}
}
