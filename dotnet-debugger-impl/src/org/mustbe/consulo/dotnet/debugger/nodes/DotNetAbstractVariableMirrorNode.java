package org.mustbe.consulo.dotnet.debugger.nodes;

import java.util.List;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueModifier;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import lombok.val;
import mono.debugger.*;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public abstract class DotNetAbstractVariableMirrorNode extends AbstractTypedMirrorNode
{
	private XValueModifier myValueModifier = new XValueModifier()
	{
		@Override
		public void setValue(@NotNull String expression, @NotNull XModificationCallback callback)
		{
			TypeMirror typeOfVariable = getTypeOfVariable();
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
					break;
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
	public abstract Icon getIconForVariable();

	@Nullable
	public abstract Value<?> getValueOfVariable();

	public abstract void setValueForVariable(@NotNull Value<?> value);

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
		val typeOfVariable = getTypeOfVariable();
		if(typeOfVariable == null)
		{
			return;
		}

		XValueChildrenList childrenList = new XValueChildrenList();
		val value = getValueOfVariable();
		if(typeOfVariable.isArray())
		{
			ArrayValueMirror arrayValueMirror = (ArrayValueMirror) value;
			if(arrayValueMirror == null)
			{
				return;
			}
			int length = arrayValueMirror.length();
			int min = Math.min(length, 100);
			for(int i = 0; i < min; i++)
			{
				String name = getName() + "[" + i + "]";

				childrenList.add(new DotNetArrayValueMirrorNode(myDebugContext, name, myThreadMirror, arrayValueMirror, i));
			}
		}
		else
		{
			if(!(value instanceof ObjectValueMirror))
			{
				return;
			}

			TypeMirror type = value.type();

			assert type != null;

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
				TypeMirror typeOfVariable = getTypeOfVariable();
				if(typeOfVariable == null)
				{
					return null;
				}
				return DotNetVirtualMachineUtil.formatNameWithGeneric(typeOfVariable);
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
						public void visitArrayValue(@NotNull ArrayValueMirror value)
						{
							StringBuilder builder = new StringBuilder();
							builder.append("{");
							String type = DotNetVirtualMachineUtil.formatNameWithGeneric(value.type());
							builder.append(type.replaceFirst("\\[\\]", "[" + value.length() + "]"));
							builder.append("@");
							builder.append(value.object().address());
							builder.append("}");
							xValueTextRenderer.renderValue(builder.toString());
						}

						@Override
						public void visitCharValue(@NotNull CharValueMirror valueMirror, @NotNull Character mainValue)
						{
							xValueTextRenderer.renderCharValue(String.valueOf(mainValue));
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

						@Override
						public void visitNoObjectValue(@NotNull NoObjectValueMirror value)
						{
							xValueTextRenderer.renderKeywordValue("null");
						}
					});
				}
			}
		}, valueOfVariable instanceof ObjectValueMirror || valueOfVariable instanceof ArrayValueMirror);
	}
}
