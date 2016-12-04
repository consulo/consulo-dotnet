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

package consulo.dotnet.lang;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiLanguageInjectionHost;
import consulo.annotations.DeprecationInfo;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetAttribute;
import consulo.dotnet.psi.DotNetExpression;

/**
 * @author VISTALL
 * @since 02.09.14
 */
public interface MultiHostInjectorByAttributeHelper
{
	ExtensionPointName<MultiHostInjectorByAttributeHelper> EP_NAME = ExtensionPointName.create("consulo.dotnet.injectionByAttributeHelper");

	@Nullable
	@RequiredReadAction
	String getLanguageId(@NotNull DotNetAttribute attribute);

	@RequiredReadAction
	default void fillExpressionsForInject(@NotNull DotNetExpression expression, @NotNull Consumer<Pair<PsiLanguageInjectionHost, TextRange>> list)
	{
		if(expression instanceof PsiLanguageInjectionHost)
		{
			TextRange textRangeForInject = getTextRangeForInject(expression);
			if(textRangeForInject == null)
			{
				return;
			}
			list.accept(Pair.create((PsiLanguageInjectionHost) expression, textRangeForInject));
		}
	}

	@Deprecated
	@DeprecationInfo("see #fillExpressionsForInject(DotNetExpression)")
	@Nullable
	@RequiredReadAction
	default TextRange getTextRangeForInject(@NotNull DotNetExpression expression)
	{
		return null;
	}
}
