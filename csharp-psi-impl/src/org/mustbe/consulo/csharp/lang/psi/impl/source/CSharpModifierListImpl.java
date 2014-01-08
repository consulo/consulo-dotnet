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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpModifierListImpl extends CSharpElementImpl implements DotNetModifierList
{
	private static final Map<DotNetModifier, IElementType> ourModifiers = new HashMap<DotNetModifier, IElementType>()
	{
		{
			put(DotNetModifier.STATIC, CSharpTokens.STATIC_KEYWORD);
			put(DotNetModifier.PUBLIC, CSharpTokens.PUBLIC_KEYWORD);
			put(DotNetModifier.PROTECTED, CSharpTokens.PROTECTED_KEYWORD);
			put(DotNetModifier.PRIVATE, CSharpTokens.PRIVATE_KEYWORD);
			put(DotNetModifier.SEALED, CSharpTokens.SEALED_KEYWORD);
			put(DotNetModifier.READONLY, CSharpTokens.READONLY_KEYWORD);
			put(DotNetModifier.ABSTRACT, CSharpTokens.ABSTRACT_KEYWORD);
			put(DotNetModifier.STATIC, CSharpTokens.STATIC_KEYWORD);
			put(DotNetModifier.UNSAFE, CSharpTokens.UNSAFE_KEYWORD);
		}
	};
	public CSharpModifierListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitModifierList(this);
	}

	@NotNull
	@Override
	public DotNetModifier[] getModifiers()
	{
		List<DotNetModifier> list = new ArrayList<DotNetModifier>();
		for(Map.Entry<DotNetModifier, IElementType> entry : ourModifiers.entrySet())
		{
			if(findChildByType(entry.getValue()) != null)
			{
				list.add(entry.getKey());
			}
		}
		return list.toArray(new DotNetModifier[list.size()]);
	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		IElementType iElementType = ourModifiers.get(modifier);
		return iElementType != null && findChildByType(iElementType) != null;
	}

	@Nullable
	@Override
	public PsiElement getModifier(IElementType elementType)
	{
		return findChildByType(elementType);
	}
}
