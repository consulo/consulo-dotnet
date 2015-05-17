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
import mono.debugger.*;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetFieldOrPropertyMirrorNode extends DotNetAbstractVariableMirrorNode
{
	private final FieldOrPropertyMirror myFieldOrPropertyMirror;
	private final ObjectValueMirror myObjectValueMirror;

	public DotNetFieldOrPropertyMirrorNode(
			@NotNull DotNetDebugContext debuggerContext,
			@NotNull FieldOrPropertyMirror fieldOrPropertyMirror,
			@NotNull ThreadMirror threadMirror,
			@Nullable ObjectValueMirror objectValueMirror)
	{
		super(debuggerContext, fieldOrPropertyMirror.name(), threadMirror);
		myFieldOrPropertyMirror = fieldOrPropertyMirror;
		myObjectValueMirror = objectValueMirror;
	}

	@NotNull
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myFieldOrPropertyMirror.type();
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
	public Value<?> getValueOfVariable()
	{
		try
		{
			return myFieldOrPropertyMirror.value(myThreadMirror, myObjectValueMirror);
		}
		catch(ThrowValueException ignored)
		{
		}
		catch(InvalidFieldIdException ignored)
		{
		}
		catch(VMDisconnectedException ignored)
		{
		}
		return null;
	}

	@Override
	public void setValueForVariable(@NotNull Value<?> value)
	{
		try
		{
			myFieldOrPropertyMirror.setValue(myThreadMirror, myObjectValueMirror, value);
		}
		catch(ThrowValueException ignored)
		{
		}
		catch(InvalidFieldIdException ignored)
		{
		}
		catch(VMDisconnectedException ignored)
		{
		}
	}
}
