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

package consulo.dotnet.psi;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiElement;
import consulo.util.lang.StringUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 06.07.14
 */
public class DotNetTypeDeclarationUtil
{
	public static final char GENERIC_MARKER_IN_NAME = '`';
	public static final char NESTED_SEPARATOR_IN_NAME = '/';
	public static final char NORMAL_SEPARATOR_IN_NAME = '.';


	@Nullable
	@RequiredReadAction
	public static String getVmQName(@Nonnull DotNetTypeDeclaration typeDeclaration)
	{
		String vmQName;

		PsiElement parent = typeDeclaration.getParent();
		if(parent instanceof DotNetTypeDeclaration)
		{
			String q = ((DotNetTypeDeclaration) parent).getVmQName();

			vmQName = StringUtil.isEmpty(q) ? typeDeclaration.getName() : q + NESTED_SEPARATOR_IN_NAME + typeDeclaration.getName();
		}
		else if(parent instanceof DotNetQualifiedElement)
		{
			String q = ((DotNetQualifiedElement) parent).getPresentableQName();

			vmQName = StringUtil.isEmpty(q) ? typeDeclaration.getName() : q + NORMAL_SEPARATOR_IN_NAME + typeDeclaration.getName();
		}
		else
		{
			vmQName = typeDeclaration.getName();
		}

		int genericParametersCount = typeDeclaration.getGenericParametersCount();
		if(genericParametersCount == 0)
		{
			return vmQName;
		}
		return vmQName + GENERIC_MARKER_IN_NAME + genericParametersCount;
	}

	@Nullable
	public static String getVmName(@Nonnull DotNetTypeDeclaration typeDeclaration)
	{
		String name = typeDeclaration.getName();

		int genericParametersCount = typeDeclaration.getGenericParametersCount();
		if(genericParametersCount == 0)
		{
			return name;
		}
		return name + GENERIC_MARKER_IN_NAME + genericParametersCount;
	}
}
