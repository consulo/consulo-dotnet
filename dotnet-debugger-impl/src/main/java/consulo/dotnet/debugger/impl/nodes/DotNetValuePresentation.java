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

import consulo.dotnet.DotNetTypes;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.impl.DotNetVirtualMachineUtil;
import consulo.dotnet.debugger.impl.nodes.valueRender.DotNetValueTextRendererProxy;
import consulo.dotnet.debugger.proxy.*;
import consulo.dotnet.debugger.proxy.value.*;
import consulo.execution.debug.frame.presentation.XValuePresentation;
import consulo.execution.debug.ui.XValuePresentationUtil;
import consulo.util.lang.Comparing;
import consulo.util.lang.StringUtil;
import consulo.util.lang.ref.SimpleReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DotNetValuePresentation extends XValuePresentation
{
	private final DotNetDebugContext myDebugContext;
	private final DotNetStackFrameProxy myStackFrame;
	private final DotNetValueProxy myValue;

	private String myRenderedTypeText;

	@Nullable
	private DotNetValueTextRendererProxy myProxy;

	public DotNetValuePresentation(DotNetDebugContext debugContext, @Nonnull DotNetStackFrameProxy stackFrame, @Nullable DotNetValueProxy value)
	{
		myDebugContext = debugContext;
		myStackFrame = stackFrame;
		myValue = value;

		myRenderedTypeText = renderType();

		if(myValue != null)
		{
			myProxy = new DotNetValueTextRendererProxy();
			renderValueImpl(myProxy);
		}
	}

	@Nullable
	@Override
	public String getType()
	{
		return myRenderedTypeText;
	}

	@Nullable
	private String renderType()
	{
		if(myValue == null)
		{
			return null;
		}

		final SimpleReference<String> result = SimpleReference.create();

		myValue.accept(new DotNetValueProxyVisitor()
		{
			@Override
			public void visitStructValue(@Nonnull DotNetStructValueProxy proxy)
			{
				result.set(DotNetVirtualMachineUtil.formatNameWithGeneric(proxy.getType()));
			}

			@Override
			public void visitObjectValue(@Nonnull DotNetObjectValueProxy value)
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
			public void visitArrayValue(@Nonnull DotNetArrayValueProxy proxy)
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
	public void renderValue(@Nonnull final XValueTextRenderer renderer)
	{
		if(myProxy != null)
		{
			myProxy.renderBack(renderer);
		}
	}

	private void renderValueImpl(@Nonnull final XValueTextRenderer renderer)
	{
		myValue.accept(new DotNetValueProxyVisitor()
		{
			@Override
			public void visitStringValue(@Nonnull DotNetStringValueProxy proxy)
			{
				renderer.renderStringValue(proxy.getValue());
			}

			@Override
			public void visitEnumValue(@Nonnull DotNetEnumValueProxy mirror)
			{
				Object value = mirror.getValue();
				if(!(value instanceof DotNetNumberValueProxy))
				{
					return;
				}

				DotNetTypeProxy type = mirror.getType();
				assert type != null;
				boolean flags = type.isAnnotatedBy(DotNetTypes.System.FlagsAttribute);

				Set<String> enumFields = new LinkedHashSet<>();
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
			public void visitStructValue(@Nonnull DotNetStructValueProxy proxy)
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

					toStringValue = StringUtil.join(fields.entrySet(), entry ->
					{
						String valueText = XValuePresentationUtil.computeValueText(new DotNetValuePresentation(myDebugContext, myStackFrame, entry.getValue()));
						return entry.getKey().getName() + " = " + valueText;
					}, ", ");
				}

				renderer.renderValue(toStringValue);
			}

			@Override
			public void visitObjectValue(@Nonnull DotNetObjectValueProxy value)
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
			public void visitCharValue(@Nonnull DotNetCharValueProxy proxy)
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
			public void visitBooleanValue(@Nonnull DotNetBooleanValueProxy value)
			{
				renderer.renderValue(String.valueOf(value.getValue()));
			}

			@Override
			public void visitNumberValue(@Nonnull DotNetNumberValueProxy proxy)
			{
				renderer.renderValue(String.valueOf(proxy.getValue()));
			}

			@Override
			public void visitNullValue(@Nonnull DotNetNullValueProxy value)
			{
				renderer.renderValue("null");
			}

			@Override
			public void visitErrorValue(@Nonnull DotNetErrorValueProxy proxy)
			{
				renderer.renderError(proxy.getErrorMessage());
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
