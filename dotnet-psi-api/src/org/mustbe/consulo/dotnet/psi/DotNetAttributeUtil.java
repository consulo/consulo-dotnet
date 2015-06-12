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
import org.mustbe.consulo.RequiredReadAction;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 13.02.14
 */
public class DotNetAttributeUtil
{
	@Nullable
	@RequiredReadAction
	public static DotNetAttribute findAttribute(@NotNull PsiElement owner, @NotNull String qName)
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
	public static boolean hasAttribute(@NotNull PsiElement owner, @NotNull String qName)
	{
		return findAttribute(owner, qName) != null;
	}
}
