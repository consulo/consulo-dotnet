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

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XValueModifier;
import mono.debugger.*;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetFieldOrPropertyMirrorNode extends DotNetAbstractVariableMirrorNode
{
	private final FieldOrPropertyMirror myFieldOrPropertyMirror;
	private final ObjectValueMirror myThisObjectMirror;
	@Nullable
	private DotNetStructValueInfo myFieldValue;

	public DotNetFieldOrPropertyMirrorNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull FieldOrPropertyMirror fieldOrPropertyMirror,
			@NotNull String name,
			@NotNull ThreadMirror threadMirror,
			@Nullable ObjectValueMirror thisObjectMirror)
	{
		super(debuggerContext, name, threadMirror);
		myFieldOrPropertyMirror = fieldOrPropertyMirror;
		myThisObjectMirror = thisObjectMirror;
	}

	public DotNetFieldOrPropertyMirrorNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull FieldOrPropertyMirror fieldOrPropertyMirror,
			@NotNull ThreadMirror threadMirror,
			@Nullable ObjectValueMirror thisObjectMirror)
	{
		this(debuggerContext, fieldOrPropertyMirror, fieldOrPropertyMirror.name(), threadMirror, thisObjectMirror);
	}

	public DotNetFieldOrPropertyMirrorNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull FieldOrPropertyMirror fieldOrPropertyMirror,
			@NotNull ThreadMirror threadMirror,
			@Nullable ObjectValueMirror thisObjectMirror,
			@NotNull DotNetStructValueInfo fieldValue)
	{
		this(debuggerContext, fieldOrPropertyMirror, fieldOrPropertyMirror.name(), threadMirror, thisObjectMirror);
		myFieldValue = fieldValue;
	}

	@NotNull
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myFieldOrPropertyMirror.type();
	}

	@Nullable
	@Override
	public XValueModifier getModifier()
	{
		if(myFieldValue != null)
		{
			return myFieldValue.canSetValue() ? super.getModifier() : null;
		}
		return super.getModifier();
	}

	@NotNull
	@Override
	public Icon getIconForVariable()
	{
		if(myFieldOrPropertyMirror instanceof PropertyMirror)
		{
			return AllIcons.Nodes.Property;
		}
		if(myFieldOrPropertyMirror instanceof FieldMirror && myFieldOrPropertyMirror.isStatic())
		{
			return AllIcons.Nodes.Field;
		}
		return super.getIconForVariable();
	}

	@Nullable
	@Override
	public Value<?> getValueOfVariableImpl() throws ThrowValueException, InvalidFieldIdException, VMDisconnectedException, InvalidStackFrameException
	{
		if(myFieldValue != null)
		{
			return myFieldValue.getValue();
		}
		return myFieldOrPropertyMirror.value(myThreadMirror, myThisObjectMirror);
	}

	@Override
	public void setValueForVariableImpl(@NotNull Value<?> value) throws ThrowValueException, InvalidFieldIdException, VMDisconnectedException,
			InvalidStackFrameException
	{
		if(myFieldValue != null)
		{
			myFieldValue.setValue(value);
		}
		else
		{
			myFieldOrPropertyMirror.setValue(myThreadMirror, myThisObjectMirror, value);
		}
	}
}
