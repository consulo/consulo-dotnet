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

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 31.12.13.
 */
@Logger
public class DotNetInheritUtil
{
	@RequiredReadAction
	public static boolean isStruct(DotNetTypeDeclaration typeDeclaration)
	{
		return isInheritor(typeDeclaration, DotNetTypes.System.ValueType, true);
	}

	@RequiredReadAction
	public static boolean isAttribute(DotNetTypeDeclaration typeDeclaration)
	{
		return isInheritor(typeDeclaration, DotNetTypes.System.Attribute, true);
	}

	@RequiredReadAction
	public static boolean isException(DotNetTypeDeclaration typeDeclaration)
	{
		return isParentOrSelf(DotNetTypes.System.Exception, typeDeclaration, true);
	}

	@RequiredReadAction
	public static boolean isEnum(DotNetTypeDeclaration typeDeclaration)
	{
		return isInheritor(typeDeclaration, DotNetTypes.System.Enum, true);
	}

	@RequiredReadAction
	public static boolean isInheritor(DotNetTypeDeclaration typeDeclaration, @NotNull String other, boolean deep)
	{
		DotNetTypeRef[] anExtends = typeDeclaration.getExtendTypeRefs();
		if(anExtends.length > 0)
		{
			for(DotNetTypeRef dotNetType : anExtends)
			{
				PsiElement psiElement = dotNetType.resolve(typeDeclaration).getElement();
				if(psiElement instanceof DotNetTypeDeclaration)
				{
					if(psiElement.isEquivalentTo(typeDeclaration))
					{
						return false;
					}

					if(Comparing.equal(((DotNetTypeDeclaration) psiElement).getVmQName(), other))
					{
						return true;
					}

					if(deep)
					{
						if(isInheritor((DotNetTypeDeclaration) psiElement, other, true))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@RequiredReadAction
	public static boolean isParentOrSelf(@NotNull String parentClass, DotNetTypeRef typeRef, PsiElement element, boolean deep)
	{
		PsiElement resolve = typeRef.resolve(element).getElement();
		if(!(resolve instanceof DotNetTypeDeclaration))
		{
			return false;
		}
		DotNetTypeDeclaration typeDeclaration = (DotNetTypeDeclaration) resolve;
		return isParentOrSelf(parentClass, typeDeclaration, deep);
	}

	@RequiredReadAction
	public static boolean isParentOrSelf(@NotNull String parentClass, DotNetTypeDeclaration typeDeclaration, boolean deep)
	{
		if(Comparing.equal(parentClass, typeDeclaration.getVmQName()))
		{
			return true;
		}
		return isParent(parentClass, typeDeclaration, deep);
	}

	@RequiredReadAction
	public static boolean isParent(@NotNull String parentClass, DotNetTypeDeclaration typeDeclaration, boolean deep)
	{
		return typeDeclaration.isInheritor(parentClass, deep);
	}

	@Deprecated
	public static boolean isInheritor(DotNetTypeDeclaration typeDeclaration, DotNetTypeDeclaration other, boolean deep)
	{
		if(typeDeclaration.isEquivalentTo(other))
		{
			return true;
		}
		DotNetTypeRef[] anExtends = typeDeclaration.getExtendTypeRefs();
		if(anExtends.length > 0)
		{
			for(DotNetTypeRef dotNetType : anExtends)
			{
				PsiElement psiElement = dotNetType.resolve(typeDeclaration).getElement();
				if(psiElement instanceof DotNetTypeDeclaration)
				{
					if(psiElement.isEquivalentTo(typeDeclaration))
					{
						return false;
					}

					if(psiElement.isEquivalentTo(other))
					{
						return true;
					}

					if(deep)
					{
						if(isInheritor((DotNetTypeDeclaration) psiElement, other, true))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
