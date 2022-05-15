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

package consulo.dotnet.debugger.impl.nodes;

import consulo.application.AllIcons;
import consulo.execution.debug.frame.*;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.impl.DotNetVirtualMachineUtil;
import consulo.dotnet.debugger.nodes.logicView.DotNetLogicValueView;
import consulo.dotnet.debugger.impl.nodes.valueRender.DotNetValueModifier;
import consulo.dotnet.debugger.impl.proxy.DotNetErrorValueProxyImpl;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetThrowValueException;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.*;
import consulo.ui.image.Image;
import consulo.util.dataholder.UserDataHolderBase;
import consulo.util.lang.StringUtil;
import consulo.util.lang.ref.SimpleReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public abstract class DotNetAbstractVariableValueNode extends AbstractTypedValueNode
{
	@Nonnull
	protected final DotNetStackFrameProxy myFrameProxy;
	private final UserDataHolderBase myDataHolder = new UserDataHolderBase();

	private volatile SimpleReference<TypeTag> myTypeTag;

	private final SimpleReference<DotNetValueProxy> myLastValueRef = SimpleReference.create();

	public DotNetAbstractVariableValueNode(@Nonnull DotNetDebugContext debuggerContext, @Nonnull String name, @Nonnull DotNetStackFrameProxy frameProxy)
	{
		super(debuggerContext, name);
		myFrameProxy = frameProxy;
	}

	@Nullable
	public TypeTag typeTag(@Nullable DotNetTypeProxy alreadyCalledType)
	{
		if(myTypeTag != null)
		{
			return myTypeTag.get();
		}

		if(alreadyCalledType != null)
		{
			TypeTag typeTag = TypeTag.byType(alreadyCalledType.getFullName());
			myTypeTag = SimpleReference.create(typeTag);
			return typeTag;
		}
		else
		{
			DotNetVirtualMachineUtil.checkCallForUIThread();

			myTypeTag = SimpleReference.create();

			DotNetTypeProxy typeOfVariable = getTypeOfVariable();
			if(typeOfVariable == null)
			{
				return null;
			}

			TypeTag typeTag = TypeTag.byType(typeOfVariable.getFullName());
			myTypeTag.set(typeTag);
			return typeTag;
		}
	}

	private void setTypeTagIfNotSet()
	{
		if(myTypeTag == null)
		{
			myTypeTag = SimpleReference.create();
		}
	}

	@Nonnull
	public Image getIconForVariable(@Nullable SimpleReference<DotNetValueProxy> alreadyCalledValue)
	{
		DotNetVirtualMachineUtil.checkCallForUIThread();

		DotNetTypeProxy typeOfVariable = getTypeOfVariableOrValue(alreadyCalledValue);
		if(typeOfVariable == null)
		{
			// set type tag to null - do not allow call from typeTag()
			setTypeTagIfNotSet();
			return AllIcons.Debugger.Value;
		}

		if(typeOfVariable.isArray())
		{
			setTypeTagIfNotSet();
			return AllIcons.Debugger.Db_array;
		}

		TypeTag typeTag = typeTag(typeOfVariable);
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
	protected abstract DotNetValueProxy getValueOfVariableImpl();

	@Nullable
	public DotNetValueProxy getValueOfVariable()
	{
		DotNetVirtualMachineUtil.checkCallForUIThread();

		try
		{
			return getValueOfVariableImpl();
		}
		catch(Throwable e)
		{
			DotNetValueProxy proxy = null;
			if(e instanceof DotNetThrowValueException)
			{
				proxy = ((DotNetThrowValueException) e).getThrowExceptionValue();
			}
			return new DotNetErrorValueProxyImpl(e, proxy);
		}
	}

	/**
	 * @param alreadyCalledValue null if value not fetched. null inside ref mean null from vm
	 */
	@Nullable
	public DotNetTypeProxy getTypeOfVariableOrValue(@Nullable SimpleReference<DotNetValueProxy> alreadyCalledValue)
	{
		DotNetVirtualMachineUtil.checkCallForUIThread();

		DotNetValueProxy valueOfVariable = alreadyCalledValue != null ? alreadyCalledValue.get() : getValueOfVariable();
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

	protected abstract void setValueForVariableImpl(@Nonnull DotNetValueProxy value);

	public void setValueForVariable(@Nonnull DotNetValueProxy value)
	{
		DotNetVirtualMachineUtil.checkCallForUIThread();

		setValueForVariableImpl(value);
	}

	@Nullable
	@Override
	public XValueModifier getModifier()
	{
		TypeTag typeTag = typeTag(null);
		if(typeTag != null)
		{
			return new DotNetValueModifier(this::setValueForVariable, myLastValueRef, myDebugContext, typeTag);
		}
		return null;
	}

	@Override
	public void computeChildren(@Nonnull XCompositeNode node)
	{
		myDebugContext.invoke(() ->
		{
			DotNetValueProxy value = getValueOfVariable();
			if(value instanceof DotNetErrorValueProxy)
			{
				DotNetValueProxy throwObject = ((DotNetErrorValueProxy) value).getThrowObject();

				if(throwObject == null)
				{
					node.setErrorMessage("No children for error value");
					return;
				}
				else
				{
					XValueChildrenList list = new XValueChildrenList(1);
					list.add(new DotNetThrowValueNode(myDebugContext, myFrameProxy, throwObject));
					node.addChildren(list, true);
					return;
				}
			}

			DotNetTypeProxy typeOfVariable = getTypeOfVariableOrValue(SimpleReference.create(value));

			if(typeOfVariable == null)
			{
				node.setErrorMessage("Variable type is not resolved");
				return;
			}

			DotNetLogicValueView valueView = null;
			for(DotNetLogicValueView temp : myDebugContext.getLogicValueViews())
			{
				if(temp.canHandle(myDebugContext, typeOfVariable))
				{
					valueView = temp;
					break;
				}
			}

			assert valueView != null : "Required default implementation";

			valueView.computeChildren(myDataHolder, myDebugContext, this, myFrameProxy, value, node);
		});
	}

	private boolean canHaveChildren(@Nullable DotNetValueProxy valueOfVariable)
	{
		DotNetVirtualMachineUtil.checkCallForUIThread();

		return valueOfVariable instanceof DotNetObjectValueProxy ||
				valueOfVariable instanceof DotNetErrorValueProxy && ((DotNetErrorValueProxy) valueOfVariable).getThrowObject() != null ||
				valueOfVariable instanceof DotNetArrayValueProxy && ((DotNetArrayValueProxy) valueOfVariable).getLength() != 0 ||
				valueOfVariable instanceof DotNetStringValueProxy && !StringUtil.isEmpty(((String) valueOfVariable.getValue())) ||
				valueOfVariable instanceof DotNetStructValueProxy && !((DotNetStructValueProxy) valueOfVariable).getValues().isEmpty();
	}

	protected void postInitialize(@Nullable DotNetValueProxy valueOfVariable)
	{
	}

	@Override
	public void computePresentation(@Nonnull XValueNode xValueNode, @Nonnull XValuePlace xValuePlace)
	{
		myDebugContext.invoke(() -> computePresentationImpl(xValueNode, xValuePlace));
	}

	protected void computePresentationImpl(@Nonnull XValueNode node, @Nonnull XValuePlace xValuePlace)
	{
		DotNetValueProxy valueOfVariable = getValueOfVariable();

		myLastValueRef.set(valueOfVariable);

		postInitialize(valueOfVariable);

		node.setPresentation(getIconForVariable(SimpleReference.create(valueOfVariable)), new DotNetValuePresentation(myDebugContext, myFrameProxy, valueOfVariable), canHaveChildren
				(valueOfVariable));
	}
}
