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
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.internal.DotNetInheritCache;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.psi.PsiElement;
import consulo.language.psi.util.LanguageCachedValueUtil;
import consulo.util.lang.Comparing;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 31.12.13.
 */
public class DotNetInheritUtil
{
	@RequiredReadAction
	public static boolean isStruct(DotNetTypeDeclaration typeDeclaration)
	{
		return LanguageCachedValueUtil.getProjectPsiDependentCache(typeDeclaration, (t) -> isInheritor(t, DotNetTypes.System.ValueType, false));
	}

	@RequiredReadAction
	public static boolean isAttribute(DotNetTypeDeclaration typeDeclaration)
	{
		return LanguageCachedValueUtil.getProjectPsiDependentCache(typeDeclaration, (t) -> isInheritor(t, DotNetTypes.System.Attribute, true));
	}

	@RequiredReadAction
	public static boolean isException(DotNetTypeDeclaration typeDeclaration)
	{
		return LanguageCachedValueUtil.getProjectPsiDependentCache(typeDeclaration, (t) -> isParentOrSelf(DotNetTypes.System.Exception, t, true));
	}

	@RequiredReadAction
	public static boolean isEnum(DotNetTypeDeclaration typeDeclaration)
	{
		return LanguageCachedValueUtil.getProjectPsiDependentCache(typeDeclaration, (t) -> isInheritor(t, DotNetTypes.System.Enum, false));
	}

	@RequiredReadAction
	public static boolean isInheritor(DotNetTypeDeclaration typeDeclaration, @Nonnull String otherVmQName, boolean deep)
	{
		return DotNetInheritCache.getInstance(typeDeclaration.getProject()).calcResult(typeDeclaration, otherVmQName, deep);
	}

	@RequiredReadAction
	public static boolean isParentOrSelf(@Nonnull String parentClass, DotNetTypeRef typeRef, boolean deep)
	{
		PsiElement resolve = typeRef.resolve().getElement();
		if(!(resolve instanceof DotNetTypeDeclaration))
		{
			return false;
		}
		DotNetTypeDeclaration typeDeclaration = (DotNetTypeDeclaration) resolve;
		return isParentOrSelf(parentClass, typeDeclaration, deep);
	}

	@RequiredReadAction
	public static boolean isParentOrSelf(@Nonnull String parentClass, DotNetTypeDeclaration typeDeclaration, boolean deep)
	{
		if(Comparing.equal(parentClass, typeDeclaration.getVmQName()))
		{
			return true;
		}
		return isParent(parentClass, typeDeclaration, deep);
	}

	@RequiredReadAction
	public static boolean isParent(@Nonnull String parentClass, DotNetTypeDeclaration typeDeclaration, boolean deep)
	{
		return typeDeclaration.isInheritor(parentClass, deep);
	}
}
