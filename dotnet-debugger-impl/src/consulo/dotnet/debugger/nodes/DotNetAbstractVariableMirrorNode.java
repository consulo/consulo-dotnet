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

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.NullableFunction;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueModifier;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.nodes.logicView.DotNetLogicValueView;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.DotNetVirtualMachineProxy;
import consulo.dotnet.debugger.proxy.value.DotNetArrayValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNullValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStructValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 11.04.14
 */
@Logger
public abstract class DotNetAbstractVariableMirrorNode extends AbstractTypedMirrorNode
{
	private XValueModifier myValueModifier = new XValueModifier()
	{
		@Override
		public void setValue(@NotNull String expression, @NotNull XModificationCallback callback)
		{
			DotNetTypeProxy typeOfVariable = getTypeOfVariable();
			assert typeOfVariable != null;
			DotNetVirtualMachineProxy virtualMachine = myDebugContext.getVirtualMachine();

			TypeTag typeTag = typeTag();
			assert typeTag != null;

			DotNetValueProxy setValue = null;
			switch(typeTag)
			{
				case String:
					if(expression.equals("null"))
					{
						setValue = virtualMachine.createNullValue();
					}
					else
					{
						expression = StringUtil.unquoteString(expression);

						setValue = virtualMachine.createStringValue(expression);
					}
					break;
				case Char:
					String chars = StringUtil.unquoteString(expression);
					if(chars.length() == 1)
					{
						setValue = virtualMachine.createCharValue(chars.charAt(0));
					}
					break;
				case Boolean:
					setValue = virtualMachine.createBooleanValue(Boolean.valueOf(expression));
					break;
				default:
					setValue = virtualMachine.createNumberValue(typeTag.getTag(), Double.parseDouble(expression));
					break;
			}

			if(setValue != null)
			{
				setValueForVariable(setValue);
			}

			callback.valueModified();
		}

		@Override
		@Nullable
		public String getInitialValueEditorText()
		{
			DotNetValueProxy valueOfVariable = getValueOfVariableSafe();
			if(valueOfVariable == null)
			{
				return null;
			}

			if(valueOfVariable instanceof DotNetNullValueProxy)
			{
				return "null";
			}

			String valueOfString = String.valueOf(valueOfVariable.getValue());
			TypeTag typeTag = typeTag();
			assert typeTag != null;

			if(typeTag == TypeTag.String)
			{
				return StringUtil.QUOTER.fun(valueOfString);
			}
			else if(typeTag == TypeTag.Char)
			{
				return StringUtil.SINGLE_QUOTER.fun(valueOfString);
			}
			return valueOfString;
		}
	};

	@NotNull
	protected final DotNetThreadProxy myThreadProxy;
	private final UserDataHolderBase myDataHolder = new UserDataHolderBase();

	public DotNetAbstractVariableMirrorNode(@NotNull DotNetDebugContext debuggerContext, @NotNull String name, @NotNull DotNetThreadProxy threadProxy)
	{
		super(debuggerContext, name);
		myThreadProxy = threadProxy;
	}

	@Nullable
	public TypeTag typeTag()
	{
		DotNetTypeProxy typeOfVariable = getTypeOfVariable();
		if(typeOfVariable == null)
		{
			return null;
		}
		return TypeTag.byType(typeOfVariable.getFullName());
	}

	@NotNull
	public Icon getIconForVariable()
	{
		DotNetTypeProxy typeOfVariable = getTypeOfVariableForChildren();
		if(typeOfVariable == null)
		{
			return AllIcons.Debugger.Value;
		}

		if(typeOfVariable.isArray())
		{
			return AllIcons.Debugger.Db_array;
		}

		TypeTag typeTag = typeTag();
		if(typeTag != null && typeTag != TypeTag.String)
		{
			return AllIcons.Debugger.Db_primitive;
		}

		DotNetTypeProxy baseType = typeOfVariable.getBaseType();
		if(baseType != null && DotNetTypes.System.Enum.equals(baseType.getFullName()))
		{
			return AllIcons.Nodes.Enum;
		}

		return AllIcons.Debugger.Value;
	}

	@Nullable
	public abstract DotNetValueProxy getValueOfVariableImpl();

	public abstract void setValueForVariableImpl(@NotNull DotNetValueProxy value);

	@Nullable
	public DotNetValueProxy getValueOfVariableSafe()
	{
		return invoke(new NullableFunction<Void, DotNetValueProxy>()
		{
			@Nullable
			@Override
			public DotNetValueProxy fun(Void o)
			{
				return getValueOfVariableImpl();
			}
		}, null);
	}

	@Nullable
	public DotNetTypeProxy getTypeOfVariableForChildren()
	{
		DotNetValueProxy valueOfVariable = getValueOfVariableSafe();
		if(valueOfVariable == null)
		{
			return getTypeOfVariable();
		}
		try
		{
			return valueOfVariable.getType();
		}
		catch(Exception e)
		{
			return getTypeOfVariable();
		}
	}

	public void setValueForVariable(@NotNull DotNetValueProxy value)
	{
		invoke(new NullableFunction<DotNetValueProxy, Void>()
		{
			@Nullable
			@Override
			public Void fun(DotNetValueProxy o)
			{
				setValueForVariableImpl(o);
				return null;
			}
		}, value);
	}

	@Nullable
	protected <P, R> R invoke(NullableFunction<P, R> func, P param)
	{
		try
		{
			return func.fun(param);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			// ignore all
		}
		return null;
	}

	@Nullable
	@Override
	public XValueModifier getModifier()
	{
		if(typeTag() != null)
		{
			return myValueModifier;
		}
		return null;
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		DotNetTypeProxy typeOfVariable = getTypeOfVariableForChildren();
		if(typeOfVariable == null)
		{
			node.setErrorMessage("No type");
			return;
		}

		DotNetValueProxy value = getValueOfVariableSafe();

		DotNetLogicValueView valueView = null;
		for(DotNetLogicValueView temp : DotNetLogicValueView.IMPL)
		{
			if(temp.canHandle(myDebugContext, typeOfVariable))
			{
				valueView = temp;
				break;
			}
		}

		assert valueView != null : "Required default implementation";

		valueView.computeChildren(myDataHolder, myDebugContext, this, myThreadProxy, value, node);
	}

	public boolean canHaveChildren()
	{
		final DotNetValueProxy valueOfVariable = getValueOfVariableSafe();
		return valueOfVariable instanceof DotNetObjectValueProxy ||
				valueOfVariable instanceof DotNetArrayValueProxy && ((DotNetArrayValueProxy) valueOfVariable).getLength() != 0 ||
				valueOfVariable instanceof DotNetStringValueProxy && !StringUtil.isEmpty(((String) valueOfVariable.getValue())) ||
				valueOfVariable instanceof DotNetStructValueProxy && !((DotNetStructValueProxy) valueOfVariable).getValues().isEmpty();
	}

	@Override
	public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace)
	{
		final DotNetValueProxy valueOfVariable = getValueOfVariableSafe();

		xValueNode.setPresentation(getIconForVariable(), new DotNetValuePresentation(myDebugContext, myThreadProxy, valueOfVariable), canHaveChildren());
	}
}
