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

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetThreadProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.DotNetArrayValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetBooleanValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNullValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNumberValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetObjectValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetStringValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxyVisitor;

/**
 * @author VISTALL
 * @since 20.09.14
 */
@Logger
public class DotNetValuePresentation extends XValuePresentation
{
	private DotNetThreadProxy myThreadProxy;
	private DotNetValueProxy myValue;

	public DotNetValuePresentation(@NotNull DotNetThreadProxy threadProxy, @Nullable DotNetValueProxy value)
	{
		myThreadProxy = threadProxy;
		myValue = value;
	}

	@Nullable
	@Override
	public String getType()
	{
		if(myValue == null)
		{
			return null;
		}

		final Ref<String> result = Ref.create();

		myValue.accept(new DotNetValueProxyVisitor.Adaptor()
		{
			/*@Override
			public void visitStructValue(@NotNull StructValueMirror mirror)
			{
				result.set(DotNetVirtualMachineUtil.formatNameWithGeneric(mirror.type()));
			}  */

			@Override
			public void visitObjectValue(@NotNull DotNetObjectValueProxy value)
			{
				DotNetTypeProxy type = value.getType();

				if(type == null)
				{
					return;
				}
				DotNetMethodProxy toString = type.findMethodByName("ToString", true);
				if(toString == null)
				{
					return;
				}

				result.set(DotNetVirtualMachineUtil.formatNameWithGeneric(type) + "@" + value.getAddress());
			}

			/*@Override
			public void visitArrayValue(@NotNull ArrayValueMirror value)
			{
				StringBuilder builder = new StringBuilder();
				String type = DotNetVirtualMachineUtil.formatNameWithGeneric(value.type());
				builder.append(type.replaceFirst("\\[\\]", "[" + value.length() + "]"));
				builder.append("@");
				builder.append(value.object().address());
				result.set(builder.toString());
			}*/
		});
		return result.get();
	}

	@Override
	public void renderValue(@NotNull final XValueTextRenderer renderer)
	{
		if(myValue == null)
		{
			return;
		}

		myValue.accept(new DotNetValueProxyVisitor()
		{
			@Override
			public void visitStringValue(@NotNull DotNetStringValueProxy proxy)
			{
				renderer.renderStringValue((String) proxy.getValue());
			}

			@Override
			public void visitArrayValue(@NotNull DotNetArrayValueProxy proxy)
			{
				// nothing
			}

			/*@Override
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
					if(field.isStatic() && (field.attributes() & FieldAttributes.Literal) == FieldAttributes.Literal)
					{
						Value<?> fieldValue = field.value(myThreadProxy, null);
						if(fieldValue instanceof EnumValueMirror)
						{
							Value<?> enumValue = ((EnumValueMirror) fieldValue).value();
							if(enumValue instanceof NumberValueMirror)
							{
								Number actualValue = ((NumberValueMirror) enumValue).value();

								if(flags)
								{
									long flagsValue = expectedValue.longValue();
									long maskValue = actualValue.longValue();
									if((flagsValue & maskValue) == maskValue)
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
			} */

			/*@Override
			public void visitStructValue(@NotNull StructValueMirror mirror)
			{
				TypeMirror type = mirror.type();

				String toStringValue = null;

				MethodMirror toString = type.findMethodByName("ToString", false);
				if(toString != null)
				{
					Value<?> invoke = invokeSafe(toString, myThreadProxy, mirror);
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
							String valueText = XValuePresentationUtil.computeValueText(new DotNetValuePresentation(myThreadProxy, entry.getValue()));
							return entry.getKey().name() + " = " + valueText;
						}
					}, ", ");
				}

				renderer.renderValue(toStringValue);
			}  */

			@Override
			public void visitObjectValue(@NotNull DotNetObjectValueProxy value)
			{
				DotNetTypeProxy type = value.getType();

				if(type == null)
				{
					return;
				}
				DotNetMethodProxy toString = type.findMethodByName("ToString", true);
				if(toString == null)
				{
					return;
				}

				String toStringValue = null;
				String qTypeOfValue = toString.getDeclarationType().getFullName();
				if(!Comparing.equal(qTypeOfValue, DotNetTypes.System.Object))
				{
					DotNetValueProxy invoke = invokeSafe(toString, myThreadProxy, value);
					if(invoke instanceof DotNetStringValueProxy)
					{
						toStringValue = (String) invoke.getValue();
					}
				}

				if(toStringValue != null)
				{
					renderer.renderValue(toStringValue);
				}
			}

			/*@Override
			public void visitCharValue(@NotNull CharValueMirror valueMirror, @NotNull Character mainValue)
			{
				StringBuilder builder = new StringBuilder();
				builder.append('\'');
				builder.append(mainValue);
				builder.append('\'');
				builder.append(' ');
				builder.append((int) mainValue.charValue());
				renderer.renderValue(builder.toString());
			} */

			@Override
			public void visitBooleanValue(@NotNull DotNetBooleanValueProxy value)
			{
				renderer.renderValue(String.valueOf(value.getValue()));
			}

			@Override
			public void visitNumberValue(@NotNull DotNetNumberValueProxy proxy)
			{
				renderer.renderValue(String.valueOf(proxy.getValue()));
			}

			@Override
			public void visitNullValue(@NotNull DotNetNullValueProxy value)
			{
				renderer.renderValue("null");
			}
		});
	}

	@Nullable
	private static DotNetValueProxy invokeSafe(DotNetMethodProxy methodMirror, DotNetThreadProxy threadMirror, DotNetValueProxy thisObject, DotNetValueProxy... arguments)
	{
		try
		{
			return methodMirror.invoke(threadMirror, thisObject, arguments);
		}
		catch(Exception ignored)
		{
		}
		return null;
	}
}
