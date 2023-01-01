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
import consulo.application.util.CachedValue;
import consulo.application.util.CachedValueProvider;
import consulo.application.util.CachedValuesManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiModificationTracker;
import consulo.language.psi.StubBasedPsiElement;
import consulo.project.DumbService;
import consulo.util.dataholder.Key;
import consulo.util.lang.Comparing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author VISTALL
 * @since 13.02.14
 */
public class DotNetAttributeUtil
{
	private static Key<ConcurrentMap<String, CachedValue<Boolean>>> ourAttributesKey = Key.create("ourAttributesKey");

	@Nullable
	@RequiredReadAction
	public static DotNetAttribute findAttribute(@Nonnull PsiElement owner, @Nonnull String qName)
	{
		if(!owner.isValid())
		{
			return null;
		}
		if(DumbService.isDumb(owner.getProject()))
		{
			return null;
		}

		DotNetAttribute[] attributes;
		if(owner instanceof DotNetAttributeListOwner)
		{
			attributes = ((DotNetAttributeListOwner) owner).getAttributes();
		}
		else
		{
			if(!(owner instanceof DotNetModifierListOwner))
			{
				return null;
			}
			DotNetModifierList modifierList = ((DotNetModifierListOwner) owner).getModifierList();
			if(modifierList == null)
			{
				return null;
			}
			 attributes = modifierList.getAttributes();
		}

		for(DotNetAttribute attribute : attributes)
		{
			DotNetTypeDeclaration typeDeclaration = attribute.resolveToType();
			if(typeDeclaration != null && Comparing.equal(typeDeclaration.getVmQName(), qName))
			{
				return attribute;
			}
		}
		return null;
	}

	@RequiredReadAction
	public static boolean hasAttribute(@Nonnull final PsiElement owner, @Nonnull final String qName)
	{
		ConcurrentMap<String, CachedValue<Boolean>> map = owner.getUserData(ourAttributesKey);
		if(map == null)
		{
			owner.putUserData(ourAttributesKey, map = new ConcurrentHashMap<String, CachedValue<Boolean>>());
		}

		CachedValue<Boolean> provider = map.get(qName);
		if(provider != null)
		{
			return provider.getValue();
		}
		else
		{
			provider = CachedValuesManager.getManager(owner.getProject()).createCachedValue(new CachedValueProvider<Boolean>()
			{
				@Nullable
				@Override
				@RequiredReadAction
				public Result<Boolean> compute()
				{
					Key key =  PsiModificationTracker.MODIFICATION_COUNT;
					if(owner instanceof StubBasedPsiElement)
					{
						key = PsiModificationTracker.OUT_OF_CODE_BLOCK_MODIFICATION_COUNT;
					}
					return Result.create(findAttribute(owner, qName) != null, key);
				}
			}, false);
			map.putIfAbsent(qName, provider);
			return provider.getValue();
		}
	}
}
