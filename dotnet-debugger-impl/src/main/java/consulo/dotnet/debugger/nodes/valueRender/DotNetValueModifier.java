/*
 * Copyright 2013-2017 consulo.io
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

package consulo.dotnet.debugger.nodes.valueRender;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.xdebugger.frame.XValueModifier;
import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.nodes.TypeTag;
import consulo.dotnet.debugger.proxy.DotNetVirtualMachineProxy;
import consulo.dotnet.debugger.proxy.value.DotNetNullValueProxy;
import consulo.dotnet.debugger.proxy.value.DotNetValueProxy;

/**
 * @author VISTALL
 * @since 22-Oct-17
 */
public class DotNetValueModifier extends XValueModifier
{
	private final Consumer<DotNetValueProxy> myValueSetFunction;
	private final Ref<DotNetValueProxy> myValueProxyRef;
	private final DotNetDebugContext myDebugContext;
	private final TypeTag myTypeTag;

	public DotNetValueModifier(@NotNull Consumer<DotNetValueProxy> valueSetFunction, @NotNull Ref<DotNetValueProxy> valueProxyRef, @NotNull DotNetDebugContext debugContext, @NotNull TypeTag typeTag)
	{
		myValueSetFunction = valueSetFunction;
		myValueProxyRef = valueProxyRef;
		myDebugContext = debugContext;
		myTypeTag = typeTag;
	}

	@Override
	public void setValue(@NotNull String expression, @NotNull XModificationCallback callback)
	{
		DotNetVirtualMachineProxy virtualMachine = myDebugContext.getVirtualMachine();

		virtualMachine.invoke(() ->
		{
			TypeTag typeTag = myTypeTag;

			DotNetValueProxy setValue;
			try
			{
				setValue = null;
				switch(typeTag)
				{
					case String:
						if(expression.equals("null"))
						{
							setValue = virtualMachine.createNullValue();
						}
						else
						{
							setValue = virtualMachine.createStringValue(StringUtil.unquoteString(expression));
						}
						break;
					case Char:
						String chars = StringUtil.unquoteString(expression);
						if(chars.length() == 1)
						{
							setValue = virtualMachine.createCharValue(chars.charAt(0));
						}
						break;
					case Boolean:
						setValue = virtualMachine.createBooleanValue(Boolean.valueOf(expression));
						break;
					default:
						setValue = virtualMachine.createNumberValue(typeTag.getTag(), Double.parseDouble(expression));
						break;
				}

				if(setValue != null)
				{
					myValueSetFunction.accept(setValue);
				}
			}
			finally
			{
				callback.valueModified();
			}
		});
	}

	@Override
	@Nullable
	public String getInitialValueEditorText()
	{
		DotNetValueProxy valueOfVariable = myValueProxyRef.get();
		if(valueOfVariable == null)
		{
			return null;
		}

		if(valueOfVariable instanceof DotNetNullValueProxy)
		{
			return "null";
		}

		String valueOfString = String.valueOf(valueOfVariable.getValue());
		TypeTag typeTag = myTypeTag;

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
}
