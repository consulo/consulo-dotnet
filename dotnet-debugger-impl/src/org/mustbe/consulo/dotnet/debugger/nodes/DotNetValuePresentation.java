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

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.BitUtil;
import com.intellij.util.Function;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValuePresentationUtil;
import edu.arizona.cs.mbel.signature.FieldAttributes;
import mono.debugger.*;

/**
 * @author VISTALL
 * @since 20.09.14
 */
@Logger
public class DotNetValuePresentation extends XValuePresentation
{
	private ThreadMirror myThreadMirror;
	private Value<?> myValue;

	public DotNetValuePresentation(@NotNull ThreadMirror threadMirror, @Nullable Value<?> value)
	{
		myThreadMirror = threadMirror;
		myValue = value;
	}

	@Nullable
	@Override
	public String getType()
	{
		if(myValue != null)
		{
			final Ref<String> result = Ref.create();

			myValue.accept(new ValueVisitor.Adapter()
			{
				@Override
				public void visitStructValue(@NotNull StructValueMirror mirror)
				{
					result.set(DotNetVirtualMachineUtil.formatNameWithGeneric(mirror.type()));
				}

				@Override
				public void visitObjectValue(@NotNull ObjectValueMirror value)
				{
					if(value.id() == 0)
					{
						return;
					}

					TypeMirror type = null;
					try
					{
						type = value.type();
					}
					catch(InvalidObjectException ignored)
					{
					}

					if(type == null)
					{
						return;
					}
					MethodMirror toString = type.findMethodByName("ToString", true);
					if(toString == null)
					{
						return;
					}

					result.set(DotNetVirtualMachineUtil.formatNameWithGeneric(type) + "@" + value.address());
				}

				@Override
				public void visitArrayValue(@NotNull ArrayValueMirror value)
				{
					StringBuilder builder = new StringBuilder();
					String type = DotNetVirtualMachineUtil.formatNameWithGeneric(value.type());
					builder.append(type.replaceFirst("\\[\\]", "[" + value.length() + "]"));
					builder.append("@");
					builder.append(value.object().address());
					result.set(builder.toString());
				}
			});
			return result.get();
		}
		return null;
	}

	@Override
	public void renderValue(@NotNull final XValueTextRenderer renderer)
	{
		if(myValue != null)
		{
			myValue.accept(new ValueVisitor.Adapter()
			{
				@Override
				public void visitStringValue(@NotNull StringValueMirror value, @NotNull String mainValue)
				{
					renderer.renderStringValue(mainValue);
				}

				@Override
				public void visitEnumValue(@NotNull EnumValueMirror mirror)
				{
					Value<?> value = mirror.value();
					if(!(value instanceof NumberValueMirror))
					{
						return;
					}

					TypeMirror type = mirror.type();
					CustomAttributeMirror[] customAttributeMirrors = type.customAttributes();

					boolean flags = false;
					for(CustomAttributeMirror customAttributeMirror : customAttributeMirrors)
					{
						MethodMirror constructorMirror = customAttributeMirror.getConstructorMirror();
						TypeMirror typeMirror = constructorMirror.declaringType();
						if(DotNetTypes.System.FlagsAttribute.equals(typeMirror.qualifiedName()))
						{
							flags = true;
							break;
						}
					}

					Set<String> enumFields = new LinkedHashSet<String>();
					Number expectedValue = ((NumberValueMirror) value).value();

					FieldMirror[] fields = mirror.type().fields();

					for(FieldMirror field : fields)
					{
						if(field.isStatic() && BitUtil.isSet(field.attributes(), FieldAttributes.Literal))
						{
							Value<?> fieldValue = field.value(myThreadMirror, null);
							if(fieldValue instanceof EnumValueMirror)
							{
								Value<?> enumValue = ((EnumValueMirror) fieldValue).value();
								if(enumValue instanceof NumberValueMirror)
								{
									Number actualValue = ((NumberValueMirror) enumValue).value();

									if(flags)
									{
										if(BitUtil.isSet(expectedValue.longValue(), actualValue.longValue()))
										{
											enumFields.add(field.name());
										}
									}
									else
									{
										if(expectedValue.equals(actualValue))
										{
											enumFields.add(field.name());
											break;
										}
									}
								}
							}
						}
					}

					if(!enumFields.isEmpty())
					{
						renderer.renderValue(StringUtil.join(enumFields, " | "));
					}
					else
					{
						renderer.renderValue(expectedValue.toString());
					}
				}

				@Override
				public void visitStructValue(@NotNull StructValueMirror mirror)
				{
					TypeMirror type = mirror.type();

					String toStringValue = null;

					MethodMirror toString = type.findMethodByName("ToString", false);
					if(toString != null)
					{
						Value<?> invoke = invokeSafe(toString, myThreadMirror, mirror);
						if(invoke instanceof StringValueMirror)
						{
							toStringValue = ((StringValueMirror) invoke).value();
						}
					}

					if(toStringValue == null)
					{
						Map<FieldOrPropertyMirror, Value<?>> fields = mirror.map();

						toStringValue = StringUtil.join(fields.entrySet(), new Function<Map.Entry<FieldOrPropertyMirror, Value<?>>, String>()
						{
							@Override
							public String fun(Map.Entry<FieldOrPropertyMirror, Value<?>> entry)
							{
								String valueText = XValuePresentationUtil.computeValueText(new DotNetValuePresentation(myThreadMirror, entry.getValue()));
								return entry.getKey().name() + " = " + valueText;
							}
						}, ", ");
					}

					renderer.renderValue(toStringValue);
				}

				@Override
				public void visitObjectValue(@NotNull ObjectValueMirror value)
				{
					if(value.id() == 0)
					{
						renderer.renderValue("null");
						return;
					}

					TypeMirror type = null;
					try
					{
						type = value.type();
					}
					catch(InvalidObjectException ignored)
					{
					}

					if(type == null)
					{
						return;
					}
					MethodMirror toString = type.findMethodByName("ToString", true);
					if(toString == null)
					{
						return;
					}

					String toStringValue = null;
					String qTypeOfValue = toString.declaringType().qualifiedName();
					if(!Comparing.equal(qTypeOfValue, DotNetTypes.System.Object))
					{
						Value<?> invoke = invokeSafe(toString, myThreadMirror, value);
						if(invoke instanceof StringValueMirror)
						{
							toStringValue = ((StringValueMirror) invoke).value();
						}
					}

					if(toStringValue != null)
					{
						renderer.renderValue(toStringValue);
					}
				}

				@Override
				public void visitCharValue(@NotNull CharValueMirror valueMirror, @NotNull Character mainValue)
				{
					StringBuilder builder = new StringBuilder();
					builder.append('\'');
					builder.append(mainValue);
					builder.append('\'');
					builder.append(' ');
					builder.append((int) mainValue.charValue());
					renderer.renderValue(builder.toString());
				}

				@Override
				public void visitBooleanValue(@NotNull BooleanValueMirror value, @NotNull Boolean mainValue)
				{
					renderer.renderValue(String.valueOf(mainValue));
				}

				@Override
				public void visitNumberValue(@NotNull NumberValueMirror value, @NotNull Number mainValue)
				{
					renderer.renderValue(String.valueOf(mainValue));
				}

				@Override
				public void visitNoObjectValue(@NotNull NoObjectValueMirror value)
				{
					renderer.renderValue("null");
				}
			});
		}
	}

	@Nullable
	private static Value<?> invokeSafe(MethodMirror methodMirror, ThreadMirror threadMirror, Value<?> thisObject, Value... arguments)
	{
		try
		{
			return methodMirror.invoke(threadMirror, InvokeFlags.DISABLE_BREAKPOINTS, thisObject, arguments);
		}
		catch(IllegalArgumentException ignored)
		{
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
}
