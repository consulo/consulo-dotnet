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

package consulo.dotnet.ide;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.dotnet.psi.DotNetGenericParameterListOwner;
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.language.psi.PsiNamedElement;
import consulo.util.lang.StringUtil;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class DotNetElementPresentationUtil
{
	@Nonnull
	@RequiredReadAction
	public static <T extends DotNetGenericParameterListOwner & DotNetNamedElement> String formatTypeWithGenericParameters(@Nonnull T el)
	{
		DotNetGenericParameter[] genericParameters = el.getGenericParameters();
		String name = el.getName();
		if(genericParameters.length == 0)
		{
			return name == null ? "<null>" : MsilHelper.cutGenericMarker(name);
		}

		StringBuilder builder = new StringBuilder();
		builder.append(name == null ? "<null>" : MsilHelper.cutGenericMarker(name));
		formatTypeGenericParameters(genericParameters, builder);
		return builder.toString();
	}

	@RequiredReadAction
	public static void formatTypeGenericParameters(@Nonnull DotNetGenericParameter[] parameters, @Nonnull StringBuilder builder)
	{
		if(parameters.length > 0)
		{
			builder.append("<");
			builder.append(StringUtil.join(parameters, PsiNamedElement::getName, ", "));
			builder.append(">");
		}
	}

	@Nonnull
	@RequiredReadAction
	public static String formatGenericParameters(@Nonnull DotNetGenericParameterListOwner owner)
	{
		DotNetGenericParameter[] genericParameters = owner.getGenericParameters();
		if(genericParameters.length == 0)
		{
			return "";
		}
		return "<" + StringUtil.join(genericParameters, PsiNamedElement::getName, ", ") + ">";
	}
}
