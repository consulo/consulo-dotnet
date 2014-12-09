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

package org.mustbe.consulo.dotnet.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.DeprecationInfo;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class DotNetTypeRefUtil
{
	@Deprecated
	@DeprecationInfo(value = "Use #isVmQNameEqual due getQualifiedText() dont return qualified name of element inside type")
	public static boolean isObject(@NotNull DotNetTypeRef typeRef)
	{
		return DotNetTypes.System.Object.equals(typeRef.getQualifiedText());
	}

	@Deprecated
	@DeprecationInfo(value = "Use #isVmQNameEqual due getQualifiedText() dont return qualified name of element inside type")

	public static boolean isVoid(@NotNull DotNetTypeRef typeRef)
	{
		return DotNetTypes.System.Void.equals(typeRef.getQualifiedText());
	}

	@Deprecated
	@DeprecationInfo(value = "Use #isVmQNameEqual due getQualifiedText() dont return qualified name of element inside type")
	public static boolean isInt32(@NotNull DotNetTypeRef typeRef)
	{
		return DotNetTypes.System.Int32.equals(typeRef.getQualifiedText());
	}

	@Deprecated
	@DeprecationInfo(value = "Use #isVmQNameEqual due getQualifiedText() dont return qualified name of element inside type")
	public static boolean isUInt32(@NotNull DotNetTypeRef typeRef)
	{
		return DotNetTypes.System.UInt32.equals(typeRef.getQualifiedText());
	}

	@Deprecated
	@DeprecationInfo(value = "Use #isVmQNameEqual due getQualifiedText() dont return qualified name of element inside type")
	public static boolean isInt64(@NotNull DotNetTypeRef typeRef)
	{
		return DotNetTypes.System.Int64.equals(typeRef.getQualifiedText());
	}

	@Deprecated
	@DeprecationInfo(value = "Use #isVmQNameEqual due getQualifiedText() dont return qualified name of element inside type")
	public static boolean isUInt64(@NotNull DotNetTypeRef typeRef)
	{
		return DotNetTypes.System.UInt64.equals(typeRef.getQualifiedText());
	}

	@Deprecated
	@DeprecationInfo(value = "Use #isVmQNameEqual due getQualifiedText() dont return qualified name of element inside type")
	public static boolean isBool(@NotNull DotNetTypeRef typeRef)
	{
		return DotNetTypes.System.Boolean.equals(typeRef.getQualifiedText());
	}

	@Nullable
	public static PsiElement resolve(@Nullable DotNetType type)
	{
		if(type == null)
		{
			return null;
		}

		DotNetTypeRef typeRef = type.toTypeRef();
		return typeRef.resolve(type).getElement();
	}

	public static boolean isVmQNameEqual(@NotNull DotNetTypeRef typeRef, @NotNull PsiElement element, @NotNull String expectedVmQName)
	{
		DotNetTypeResolveResult typeResolveResult = typeRef.resolve(element);
		PsiElement typeResolveResultElement = typeResolveResult.getElement();
		if(typeResolveResultElement instanceof DotNetTypeDeclaration)
		{
			String vmQName = ((DotNetTypeDeclaration) typeResolveResultElement).getVmQName();
			return vmQName != null && Comparing.equal(vmQName, expectedVmQName);
		}
		return false;
	}

	@NotNull
	public static DotNetTypeRef[] toArray(@NotNull DotNetType[] arguments)
	{
		if(arguments.length == 0)
		{
			return DotNetTypeRef.EMPTY_ARRAY;
		}

		DotNetTypeRef[] rArguments = new DotNetTypeRef[arguments.length];
		for(int i = 0; i < arguments.length; i++)
		{
			DotNetType argument = arguments[i];
			rArguments[i] = argument.toTypeRef();
		}
		return rArguments;
	}
}
