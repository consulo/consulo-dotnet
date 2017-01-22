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

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Function;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValuePresentationUtil;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import consulo.dotnet.debugger.proxy.DotNetFieldOrPropertyProxy;
import consulo.dotnet.debugger.proxy.DotNetFieldProxy;
import consulo.dotnet.debugger.proxy.DotNetMethodProxy;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.debugger.proxy.value.*;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DotNetValuePresentation extends XValuePresentation
{
	private static final Logger LOGGER = Logger.getInstance(DotNetValuePresentation.class);

	private final DotNetDebugContext myDebugContext;
	private final DotNetStackFrameProxy myStackFrame;
	private final DotNetValueProxy myValue;

	public DotNetValuePresentation(DotNetDebugContext debugContext, @NotNull DotNetStackFrameProxy stackFrame, @Nullable DotNetValueProxy value)
	{
		myDebugContext = debugContext;
		myStackFrame = stackFrame;
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
			@Override
			public void visitStructValue(@NotNull DotNetStructValueProxy proxy)
			{
				result.set(DotNetVirtualMachineUtil.formatNameWithGeneric(proxy.getType()));
			}

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

			@Override
			public void visitArrayValue(@NotNull DotNetArrayValueProxy proxy)
			{
				StringBuilder builder = new StringBuilder();
				String type = DotNetVirtualMachineUtil.formatNameWithGeneric(proxy.getType());
				builder.append(type.replaceFirst("\\[\\]", "[" + proxy.getLength() + "]"));
				builder.append("@");
				builder.append(proxy.getAddress());
				result.set(builder.toString());
			}
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
				renderer.renderStringValue(proxy.getValue());
			}

			@Override
			public void visitArrayValue(@NotNull DotNetArrayValueProxy proxy)
			{
				// nothing
			}

			@Override
			public void visitEnumValue(@NotNull DotNetEnumValueProxy mirror)
			{
				Object value = mirror.getValue();
				if(!(value instanceof DotNetNumberValueProxy))
				{
					return;
				}

				DotNetTypeProxy type = mirror.getType();
				assert type != null;
				boolean flags = type.isAnnotatedBy(DotNetTypes.System.FlagsAttribute);

				Set<String> enumFields = new LinkedHashSet<String>();
				Number expectedValue = ((DotNetNumberValueProxy) value).getValue();

				DotNetFieldProxy[] fields = type.getFields();

				for(DotNetFieldProxy field : fields)
				{
					if(field.isStatic() && field.isLiteral())
					{
						Number actualValue = field.getEnumConstantValue(myStackFrame);
						if(actualValue != null)
						{
							if(flags)
							{
								long flagsValue = expectedValue.longValue();
								long maskValue = actualValue.longValue();
								if((flagsValue & maskValue) == maskValue)
								{
									enumFields.add(field.getName());
								}
							}
							else
							{
								if(expectedValue.longValue() == actualValue.longValue())
								{
									enumFields.add(field.getName());
									break;
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
			public void visitStructValue(@NotNull DotNetStructValueProxy proxy)
			{
				DotNetTypeProxy type = proxy.getType();
				if(type == null)
				{
					return;
				}

				String toStringValue = null;

				DotNetMethodProxy toString = type.findMethodByName("ToString", false);
				if(toString != null)
				{
					DotNetValueProxy invoke = invokeSafe(toString, myStackFrame, proxy);
					if(invoke instanceof DotNetStringValueProxy)
					{
						toStringValue = ((DotNetStringValueProxy) invoke).getValue();
					}
				}

				if(toStringValue == null)
				{
					Map<DotNetFieldOrPropertyProxy, DotNetValueProxy> fields = proxy.getValues();

					toStringValue = StringUtil.join(fields.entrySet(), new Function<Map.Entry<DotNetFieldOrPropertyProxy, DotNetValueProxy>, String>()
					{
						@Override
						public String fun(Map.Entry<DotNetFieldOrPropertyProxy, DotNetValueProxy> entry)
						{
							String valueText = XValuePresentationUtil.computeValueText(new DotNetValuePresentation(myDebugContext, myStackFrame, entry.getValue()));
							return entry.getKey().getName() + " = " + valueText;
						}
					}, ", ");
				}

				renderer.renderValue(toStringValue);
			}

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
					DotNetValueProxy invoke = invokeSafe(toString, myStackFrame, value);
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

			@Override
			public void visitCharValue(@NotNull DotNetCharValueProxy proxy)
			{
				Character mainValue = proxy.getValue();
				StringBuilder builder = new StringBuilder();
				builder.append('\'');
				builder.append(mainValue);
				builder.append('\'');
				builder.append(' ');
				builder.append((int) mainValue.charValue());
				renderer.renderValue(builder.toString());
			}

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
	private static DotNetValueProxy invokeSafe(DotNetMethodProxy methodMirror, DotNetStackFrameProxy frameProxy, DotNetValueProxy thisObject, DotNetValueProxy... arguments)
	{
		try
		{
			return methodMirror.invoke(frameProxy, thisObject, arguments);
		}
		catch(Exception ignored)
		{
		}
		return null;
	}
}
