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
import com.intellij.openapi.util.Ref;
import com.intellij.xdebugger.frame.presentation.XValuePresentation;
import mono.debugger.*;

/**
 * @author VISTALL
 * @since 20.09.14
 */
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
						render.renderValue("null");
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
				public void visitCharValue(@NotNull CharValueMirror valueMirror, @NotNull Character mainValue)
				{
					StringBuilder builder = new StringBuilder();
					builder.append('\'');
					builder.append(mainValue);
					builder.append('\'');
					builder.append(' ');
					builder.append((int)mainValue.charValue());
					render.renderValue(builder.toString());
				}

				@Override
				public void visitBooleanValue(@NotNull BooleanValueMirror value, @NotNull Boolean mainValue)
				{
					render.renderValue(String.valueOf(mainValue));
				}

				@Override
				public void visitNumberValue(@NotNull NumberValueMirror value, @NotNull Number mainValue)
				{
					render.renderValue(String.valueOf(mainValue));
				}

				@Override
				public void visitNoObjectValue(@NotNull NoObjectValueMirror value)
				{
					render.renderValue("null");
				}
			});
		}
	}
}
