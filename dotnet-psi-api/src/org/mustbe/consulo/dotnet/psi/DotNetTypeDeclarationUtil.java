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

package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;

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
	public static String getVmQName(@NotNull DotNetTypeDeclaration typeDeclaration)
	{
		String presentableQName;

		PsiElement parent = typeDeclaration.getParent();
		if(parent instanceof DotNetTypeDeclaration)
		{
			String q = ((DotNetTypeDeclaration) parent).getPresentableQName();

			presentableQName = StringUtil.isEmpty(q) ? typeDeclaration.getName() : q + NESTED_SEPARATOR_IN_NAME + typeDeclaration.getName();
		}
		else if(parent instanceof DotNetQualifiedElement)
		{
			String q = ((DotNetQualifiedElement) parent).getPresentableQName();

			presentableQName = StringUtil.isEmpty(q) ? typeDeclaration.getName() : q + NORMAL_SEPARATOR_IN_NAME + typeDeclaration.getName();
		}
		else
		{
			presentableQName = typeDeclaration.getName();
		}

		int genericParametersCount = typeDeclaration.getGenericParametersCount();
		if(genericParametersCount == 0)
		{
			return presentableQName;
		}
		return presentableQName + GENERIC_MARKER_IN_NAME + genericParametersCount;
	}

	@Nullable
	public static String getVmName(@NotNull DotNetTypeDeclaration typeDeclaration)
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
