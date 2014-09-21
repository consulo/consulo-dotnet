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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachineUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import mono.debugger.*;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DotNetValuePresentation extends XValuePresentation
{
	private ThreadMirror myThreadMirror;
	private TypeMirror myTypeOfValue;
	private Value<?> myValue;

	public DotNetValuePresentation(@NotNull ThreadMirror threadMirror, @Nullable TypeMirror typeOfValue, @Nullable Value<?> value)
	{
		myThreadMirror = threadMirror;
		myTypeOfValue = typeOfValue;
		myValue = value;
	}

	@Nullable
	@Override
	public String getType()
	{
		TypeMirror typeOfVariable = myTypeOfValue;
		if(typeOfVariable == null)
		{
			return null;
		}
		return DotNetVirtualMachineUtil.formatNameWithGeneric(typeOfVariable);
	}

	@Override
	public void renderValue(@NotNull final XValueTextRenderer render)
	{
		if(myValue != null)
		{
			myValue.accept(new ValueVisitor.Adapter()
			{
				@Override
				public void visitStringValue(@NotNull StringValueMirror value, @NotNull String mainValue)
				{
					render.renderStringValue(mainValue);
				}

				@Override
				public void visitObjectValue(@NotNull ObjectValueMirror value)
				{
					if(value.id() == 0)
					{
						render.renderKeywordValue("null");
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
					if(Comparing.equal(qTypeOfValue, DotNetTypes.System.Object))
					{
						toStringValue = "{" + qTypeOfValue + "@" + value.address() + "}";
					}
					else
					{
						Value<?> invoke = toString.invoke(myThreadMirror, InvokeFlags.DISABLE_BREAKPOINTS, value);
						if(invoke instanceof StringValueMirror)
						{
							toStringValue = ((StringValueMirror) invoke).value();
						}
					}
					if(toStringValue != null)
					{
						render.renderValue(toStringValue);
					}
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
					render.renderValue(builder.toString());
				}

				@Override
				public void visitCharValue(@NotNull CharValueMirror valueMirror, @NotNull Character mainValue)
				{
					render.renderCharValue(String.valueOf(mainValue));
				}

				@Override
				public void visitBooleanValue(@NotNull BooleanValueMirror value, @NotNull Boolean mainValue)
				{
					render.renderKeywordValue(String.valueOf(mainValue));
				}

				@Override
				public void visitNumberValue(@NotNull NumberValueMirror value, @NotNull Number mainValue)
				{
					render.renderNumericValue(String.valueOf(mainValue));
				}

				@Override
				public void visitNoObjectValue(@NotNull NoObjectValueMirror value)
				{
					render.renderKeywordValue("null");
				}
			});
		}
	}
}
