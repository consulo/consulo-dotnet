package org.mustbe.consulo.dotnet.debugger.nodes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueModifier;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import edu.arizona.cs.mbel.signature.SignatureConstants;
import mono.debugger.*;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public abstract class DotNetAbstractVariableMirrorNode extends AbstractTypedMirrorNode
{
	public static final Map<String, Byte> PRIMITIVE_TYPES = new HashMap<String, Byte>()
	{
		{
			put(DotNetTypes.System_Int32, SignatureConstants.ELEMENT_TYPE_I4);
		}
	};

	private XValueModifier myValueModifier = new XValueModifier()
	{
		@Override
		public void setValue(@NotNull String expression, @NotNull XModificationCallback callback)
		{
			TypeMirror typeOfVariable = getTypeOfVariable();

			AppDomainMirror appDomainMirror = typeOfVariable.virtualMachine().rootAppDomain();

			Value<?> setValue = null;
			if(isString())
			{
				if(expression.equals("null"))
				{
					setValue = new NoObjectValueMirror(typeOfVariable.virtualMachine());
				}
				else
				{
					expression = StringUtil.unquoteString(expression);

					setValue = appDomainMirror.createString(expression);
				}
			}
			else if(isBoolean())
			{
				setValue = new BooleanValueMirror(typeOfVariable.virtualMachine(), Boolean.valueOf(expression));
			}
			else
			{
				Byte tag = PRIMITIVE_TYPES.get(typeOfVariable.qualifiedName());
				if(tag != null)
				{
					setValue = new NumberValueMirror(typeOfVariable.virtualMachine(), tag, Double.parseDouble(expression));
				}
			}

			if(setValue != null)
			{

				try
				{
					setValueForVariable(setValue);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			callback.valueModified();
		}

		@Override
		@Nullable
		public String getInitialValueEditorText()
		{
			Value<?> valueOfVariable = getValueOfVariable();
			if(valueOfVariable == null)
			{
				return null;
			}
			String valueOfString = String.valueOf(valueOfVariable.value());
			if(isString())
			{
				return StringUtil.QUOTER.fun(valueOfString);
			}
			if(valueOfVariable instanceof NoObjectValueMirror)
			{
				return "null";
			}
			return valueOfString;
		}
	};

	@NotNull
	protected final ThreadMirror myThreadMirror;

	public DotNetAbstractVariableMirrorNode(@NotNull DotNetDebugContext debuggerContext, @NotNull String name, @NotNull ThreadMirror threadMirror)
	{
		super(debuggerContext, name);
		myThreadMirror = threadMirror;
	}

	public boolean isString()
	{
		TypeMirror typeOfVariable = getTypeOfVariable();
		return Comparing.equal(typeOfVariable.qualifiedName(), DotNetTypes.System_String);
	}

	public boolean isBoolean()
	{
		TypeMirror typeOfVariable = getTypeOfVariable();
		return Comparing.equal(typeOfVariable.qualifiedName(), DotNetTypes.System_Boolean);
	}

	@NotNull
	public abstract Icon getIconForVariable();

	@Nullable
	public abstract Value<?> getValueOfVariable();

	public abstract void setValueForVariable(@NotNull Value<?> value);

	@Nullable
	@Override
	public XValueModifier getModifier()
	{
		if(isString() || PRIMITIVE_TYPES.containsKey(getTypeOfVariable().qualifiedName()) || isBoolean())
		{
			return myValueModifier;
		}
		return null;
	}

	@Override
	public void computeChildren(@NotNull XCompositeNode node)
	{
		final Value<?> value = getValueOfVariable();
		if(!(value instanceof ObjectValueMirror))
		{
			return;
		}

		TypeMirror type = value.type();

		assert type != null;

		XValueChildrenList childrenList = new XValueChildrenList();

		childrenList.add(new DotNetObjectValueMirrorNode(myDebugContext, myThreadMirror, type, null));

		List<FieldOrPropertyMirror> fieldMirrors = type.fieldAndProperties(true);
		for(FieldOrPropertyMirror fieldMirror : fieldMirrors)
		{
			if(fieldMirror.isStatic())
			{
				continue;
			}

			if(fieldMirror instanceof PropertyMirror && ((PropertyMirror) fieldMirror).isArrayProperty())
			{
				continue;
			}
			childrenList.add(new DotNetFieldOrPropertyMirrorNode(myDebugContext, fieldMirror, myThreadMirror, (ObjectValueMirror) value));
		}
		node.addChildren(childrenList, true);
	}

	@Override
	public void computePresentation(@NotNull XValueNode xValueNode, @NotNull XValuePlace xValuePlace)
	{
		final Value<?> valueOfVariable = getValueOfVariable();

		xValueNode.setPresentation(getIconForVariable(), new XValuePresentation()
		{
			@Nullable
			@Override
			public String getType()
			{
				return DotNetVirtualMachineUtil.formatNameWithGeneric(getTypeOfVariable());
			}

			@Override
			public void renderValue(@NotNull final XValueTextRenderer xValueTextRenderer)
			{
				if(valueOfVariable == null)
				{
					xValueTextRenderer.renderKeywordValue("null?");
				}
				else
				{
					valueOfVariable.accept(new ValueVisitor.Adapter()
					{
						@Override
						public void visitStringValue(@NotNull StringValueMirror value, @NotNull String mainValue)
						{
							xValueTextRenderer.renderStringValue(mainValue);
						}

						@Override
						public void visitBooleanValue(@NotNull BooleanValueMirror value, @NotNull Boolean mainValue)
						{
							xValueTextRenderer.renderKeywordValue(String.valueOf(mainValue));
						}

						@Override
						public void visitNumberValue(@NotNull NumberValueMirror value, @NotNull Number mainValue)
						{
							xValueTextRenderer.renderNumericValue(String.valueOf(mainValue));
						}
					});
				}
			}
		}, valueOfVariable instanceof ObjectValueMirror);
	}
}
