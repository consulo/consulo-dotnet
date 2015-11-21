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

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.DotNetLogicValueView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.NullableFunction;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueModifier;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import mono.debugger.*;

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
			TypeMirror typeOfVariable = getTypeOfVariable();
			assert typeOfVariable != null;
			VirtualMachine virtualMachine = typeOfVariable.virtualMachine();

			TypeTag typeTag = typeTag();
			assert typeTag != null;

			AppDomainMirror appDomainMirror = virtualMachine.rootAppDomain();

			Value<?> setValue = null;
			switch(typeTag)
			{
				case String:
					if(expression.equals("null"))
					{
						setValue = new NoObjectValueMirror(virtualMachine);
					}
					else
					{
						expression = StringUtil.unquoteString(expression);

						setValue = appDomainMirror.createString(expression);
					}
					break;
				case Char:
					String chars = StringUtil.unquoteString(expression);
					if(chars.length() == 1)
					{
						setValue = new CharValueMirror(virtualMachine, chars.charAt(0));
					}
					break;
				case Boolean:
					setValue = new BooleanValueMirror(virtualMachine, Boolean.valueOf(expression));
					break;
				default:
					setValue = new NumberValueMirror(virtualMachine, typeTag.getTag(), Double.parseDouble(expression));
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
			Value<?> valueOfVariable = getValueOfVariableSafe();
			if(valueOfVariable == null)
			{
				return null;
			}

			if(valueOfVariable instanceof NoObjectValueMirror)
			{
				return "null";
			}

			String valueOfString = String.valueOf(valueOfVariable.value());
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
	protected final ThreadMirror myThreadMirror;
	private final UserDataHolderBase myDataHolder = new UserDataHolderBase();

	public DotNetAbstractVariableMirrorNode(@NotNull DotNetDebugContext debuggerContext, @NotNull String name, @NotNull ThreadMirror threadMirror)
	{
		super(debuggerContext, name);
		myThreadMirror = threadMirror;
	}

	@Nullable
	public TypeTag typeTag()
	{
		TypeMirror typeOfVariable = getTypeOfVariable();
		if(typeOfVariable == null)
		{
			return null;
		}
		return TypeTag.byType(typeOfVariable.qualifiedName());
	}

	@NotNull
	public Icon getIconForVariable()
	{
		TypeMirror typeOfVariable = getTypeOfVariableForChildren();
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

		return AllIcons.Debugger.Value;
	}

	@Nullable
	public abstract Value<?> getValueOfVariableImpl() throws ThrowValueException, InvalidFieldIdException, VMDisconnectedException, InvalidStackFrameException;

	public abstract void setValueForVariableImpl(@NotNull Value<?> value) throws ThrowValueException, InvalidFieldIdException, VMDisconnectedException, InvalidStackFrameException;

	@Nullable
	public Value<?> getValueOfVariableSafe()
	{
		return invoke(new NullableFunction<Void, Value<?>>()
		{
			@Nullable
			@Override
			public Value<?> fun(Void o)
			{
				return getValueOfVariableImpl();
			}
		}, null);
	}

	@Nullable
	public TypeMirror getTypeOfVariableForChildren()
	{
		Value<?> valueOfVariable = getValueOfVariableSafe();
		if(valueOfVariable == null)
		{
			return getTypeOfVariable();
		}
		try
		{
			return valueOfVariable.type();
		}
		catch(Exception e)
		{
			return getTypeOfVariable();
		}
	}

	public void setValueForVariable(@NotNull Value<?> value)
	{
		invoke(new NullableFunction<Value<?>, Void>()
		{
			@Nullable
			@Override
			public Void fun(Value<?> o)
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
		catch(ThrowValueException ignored)
		{
		}
		catch(InvalidFieldIdException ignored)
		{
		}
		catch(VMDisconnectedException ignored)
		{
		}
		catch(InvalidStackFrameException ignored)
		{
		}
		catch(Exception e)
		{
			LOGGER.error(e);
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
		TypeMirror typeOfVariable = getTypeOfVariableForChildren();
		if(typeOfVariable == null)
		{
			node.setErrorMessage("No type");
			return;
		}

		Value<?> value = getValueOfVariableSafe();

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

		valueView.computeChildren(myDataHolder, myDebugContext, myThreadMirror, value, node);
	}

	public boolean canHaveChildren()
	{
		final Value<?> valueOfVariable = getValueOfVariableSafe();
		return valueOfVariable instanceof ObjectValueMirror || valueOfVariable instanceof ArrayValueMirror || valueOfVariable instanceof StringValueMirror;
	}

	@Override
	public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace)
	{
		final Value<?> valueOfVariable = getValueOfVariableSafe();

		xValueNode.setPresentation(getIconForVariable(), new DotNetValuePresentation(myThreadMirror, valueOfVariable), canHaveChildren());
	}
}
