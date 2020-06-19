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

import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XValueModifier;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.*;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.ui.image.Image;
import consulo.ui.image.ImageEffects;
import consulo.util.lang.ref.SimpleReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetFieldOrPropertyValueNode extends DotNetAbstractVariableValueNode
{
	@Nonnull
	private final DotNetFieldOrPropertyProxy myFieldOrPropertyMirror;
	private final DotNetObjectValueProxy myThisObjectMirror;
	@Nullable
	private DotNetStructValueInfo myFieldValue;

	public DotNetFieldOrPropertyValueNode(@Nonnull DotNetDebugContext debuggerContext,
			@Nonnull DotNetFieldOrPropertyProxy fieldOrPropertyMirror,
			@Nonnull String name,
			@Nonnull DotNetStackFrameProxy stackFrame,
			@Nullable DotNetObjectValueProxy thisObjectMirror)
	{
		super(debuggerContext, name, stackFrame);
		myFieldOrPropertyMirror = fieldOrPropertyMirror;
		myThisObjectMirror = thisObjectMirror;
	}

	public DotNetFieldOrPropertyValueNode(@Nonnull DotNetDebugContext debuggerContext,
			@Nonnull DotNetFieldOrPropertyProxy fieldOrPropertyMirror,
			@Nonnull DotNetStackFrameProxy threadMirror,
			@Nullable DotNetObjectValueProxy thisObjectMirror)
	{
		this(debuggerContext, fieldOrPropertyMirror, fieldOrPropertyMirror.getName(), threadMirror, thisObjectMirror);
	}

	public DotNetFieldOrPropertyValueNode(@Nonnull DotNetDebugContext debuggerContext,
			@Nonnull DotNetFieldOrPropertyProxy fieldOrPropertyMirror,
			@Nonnull DotNetStackFrameProxy stackFrame,
			@Nullable DotNetObjectValueProxy thisObjectMirror,
			@Nonnull DotNetStructValueInfo fieldValue)
	{
		this(debuggerContext, fieldOrPropertyMirror, fieldOrPropertyMirror.getName(), stackFrame, thisObjectMirror);
		myFieldValue = fieldValue;
	}

	@Override
	protected void postInitialize(DotNetValueProxy valueOfVariable)
	{
		typeTag(null);
	}

	@Nullable
	@Override
	public DotNetTypeProxy getTypeOfVariableImpl()
	{
		return myFieldOrPropertyMirror.getType();
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

	@Nonnull
	@Override
	public Image getIconForVariable(@Nullable SimpleReference<DotNetValueProxy> alreadyCalledValue)
	{
		boolean isStatic = myFieldOrPropertyMirror.isStatic();

		Image baseIcon = null;
		if(myFieldOrPropertyMirror instanceof DotNetPropertyProxy)
		{
			DotNetValueProxy valueOfVariableSafe = alreadyCalledValue != null ? alreadyCalledValue.get() : getValueOfVariable();
			if(valueOfVariableSafe != null && myThisObjectMirror != null && valueOfVariableSafe.isEqualTo(myThisObjectMirror))
			{
				baseIcon = AllIcons.Debugger.Selfreference;
			}

			if(baseIcon == null)
			{
				baseIcon = AllIcons.Nodes.Property;
			}
		}

		if(myFieldOrPropertyMirror instanceof DotNetFieldProxy)
		{
			DotNetValueProxy valueOfVariableSafe = alreadyCalledValue != null ? alreadyCalledValue.get() : getValueOfVariable();
			if(valueOfVariableSafe != null && myThisObjectMirror != null && valueOfVariableSafe.isEqualTo(myThisObjectMirror))
			{
				baseIcon = AllIcons.Debugger.Selfreference;
			}

			if(baseIcon == null)
			{
				baseIcon = AllIcons.Nodes.Field;
			}
		}

		assert baseIcon != null;
		if(isStatic)
		{
			return ImageEffects.layered(baseIcon, AllIcons.Nodes.StaticMark);
		}
		return baseIcon;
	}

	@Nullable
	@Override
	public DotNetValueProxy getValueOfVariableImpl()
	{
		if(myFieldValue != null)
		{
			return myFieldValue.getValue();
		}
		return myFieldOrPropertyMirror.getValue(myFrameProxy, myThisObjectMirror);
	}

	@Override
	public void setValueForVariableImpl(@Nonnull DotNetValueProxy value)
	{
		if(myFieldValue != null)
		{
			myFieldValue.setValue(value);
		}
		else
		{
			myFieldOrPropertyMirror.setValue(myFrameProxy, myThisObjectMirror, value);
		}
	}
}
