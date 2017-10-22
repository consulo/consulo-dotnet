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

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.frame.XValueModifier;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.ide.IconDescriptor;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetFieldOrPropertyValueNode extends DotNetAbstractVariableValueNode
{
	@NotNull
	private final DotNetFieldOrPropertyProxy myFieldOrPropertyMirror;
	private final DotNetObjectValueProxy myThisObjectMirror;
	@Nullable
	private DotNetStructValueInfo myFieldValue;

	public DotNetFieldOrPropertyValueNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull DotNetFieldOrPropertyProxy fieldOrPropertyMirror,
			@NotNull String name,
			@NotNull DotNetStackFrameProxy stackFrame,
			@Nullable DotNetObjectValueProxy thisObjectMirror)
	{
		super(debuggerContext, name, stackFrame);
		myFieldOrPropertyMirror = fieldOrPropertyMirror;
		myThisObjectMirror = thisObjectMirror;
	}

	public DotNetFieldOrPropertyValueNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull DotNetFieldOrPropertyProxy fieldOrPropertyMirror,
			@NotNull DotNetStackFrameProxy threadMirror,
			@Nullable DotNetObjectValueProxy thisObjectMirror)
	{
		this(debuggerContext, fieldOrPropertyMirror, fieldOrPropertyMirror.getName(), threadMirror, thisObjectMirror);
	}

	public DotNetFieldOrPropertyValueNode(@NotNull DotNetDebugContext debuggerContext,
			@NotNull DotNetFieldOrPropertyProxy fieldOrPropertyMirror,
			@NotNull DotNetStackFrameProxy stackFrame,
			@Nullable DotNetObjectValueProxy thisObjectMirror,
			@NotNull DotNetStructValueInfo fieldValue)
	{
		this(debuggerContext, fieldOrPropertyMirror, fieldOrPropertyMirror.getName(), stackFrame, thisObjectMirror);
		myFieldValue = fieldValue;
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

	@NotNull
	@Override
	public Icon getIconForVariable()
	{
		boolean isStatic = myFieldOrPropertyMirror.isStatic();

		Icon baseIcon = null;
		if(myFieldOrPropertyMirror instanceof DotNetPropertyProxy)
		{
			DotNetValueProxy valueOfVariableSafe = getValueOfVariableSafe();
			if(valueOfVariableSafe != null && myThisObjectMirror != null && valueOfVariableSafe.isEqualTo(myThisObjectMirror))
			{
				baseIcon = AllIcons.Debugger.Selfreference;
			}
			baseIcon = AllIcons.Nodes.Property;
		}
		if(myFieldOrPropertyMirror instanceof DotNetFieldProxy)
		{
			baseIcon = AllIcons.Nodes.Field;
		}
		assert baseIcon != null;
		if(isStatic)
		{
			return new IconDescriptor(baseIcon).addLayerIcon(AllIcons.Nodes.StaticMark).toIcon();
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
	public void setValueForVariableImpl(@NotNull DotNetValueProxy value)
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
