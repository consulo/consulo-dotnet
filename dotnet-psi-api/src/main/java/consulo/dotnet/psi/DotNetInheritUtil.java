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

import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.resolve.DotNetTypeRef;
import gnu.trove.THashSet;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author VISTALL
 * @since 31.12.13.
 */
public class DotNetInheritUtil
{
	@RequiredReadAction
	public static boolean isStruct(DotNetTypeDeclaration typeDeclaration)
	{
		return CachedValuesManager.getCachedValue(typeDeclaration, () -> CachedValueProvider.Result.create(isInheritor(typeDeclaration, DotNetTypes.System.ValueType, false), PsiModificationTracker
				.MODIFICATION_COUNT));
	}

	@RequiredReadAction
	public static boolean isAttribute(DotNetTypeDeclaration typeDeclaration)
	{
		return CachedValuesManager.getCachedValue(typeDeclaration, () -> CachedValueProvider.Result.create(isInheritor(typeDeclaration, DotNetTypes.System.Attribute, true), PsiModificationTracker
				.MODIFICATION_COUNT));
	}

	@RequiredReadAction
	public static boolean isException(DotNetTypeDeclaration typeDeclaration)
	{
		return CachedValuesManager.getCachedValue(typeDeclaration, () -> CachedValueProvider.Result.create(isInheritor(typeDeclaration, DotNetTypes.System.Exception, true), PsiModificationTracker
				.MODIFICATION_COUNT));
	}

	@RequiredReadAction
	public static boolean isEnum(DotNetTypeDeclaration typeDeclaration)
	{
		return CachedValuesManager.getCachedValue(typeDeclaration, () -> CachedValueProvider.Result.create(isInheritor(typeDeclaration, DotNetTypes.System.Enum, false), PsiModificationTracker
				.MODIFICATION_COUNT));
	}

	@RequiredReadAction
	public static boolean isInheritor(DotNetTypeDeclaration typeDeclaration, @Nonnull String other, boolean deep)
	{
		return isInheritorImpl(typeDeclaration, other, deep, new THashSet<>());
	}

	@RequiredReadAction
	private static boolean isInheritorImpl(@Nonnull DotNetTypeDeclaration typeDeclaration,
										   @Nonnull String otherVmQName,
										   boolean deep,
										   @Nonnull Set<String> alreadyProcessedTypes)
	{
		DotNetTypeRef[] anExtends = typeDeclaration.getExtendTypeRefs();
		if(anExtends.length > 0)
		{
			for(DotNetTypeRef typeRef : anExtends)
			{
				if(typeRef.isEqualToVmQName(otherVmQName))
				{
					return true;
				}

				PsiElement psiElement = typeRef.resolve().getElement();
				if(psiElement instanceof DotNetTypeDeclaration)
				{
					if(psiElement.isEquivalentTo(typeDeclaration))
					{
						return false;
					}

					String vmQName = ((DotNetTypeDeclaration) psiElement).getVmQName();
					if(Comparing.equal(vmQName, otherVmQName))
					{
						return true;
					}

					if(deep)
					{
						if(alreadyProcessedTypes.add(vmQName))
						{
							if(isInheritorImpl((DotNetTypeDeclaration) psiElement, otherVmQName, true, alreadyProcessedTypes))
							{
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@RequiredReadAction
	public static boolean isParentOrSelf(@Nonnull String parentClass, DotNetTypeRef typeRef, PsiElement element, boolean deep)
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
