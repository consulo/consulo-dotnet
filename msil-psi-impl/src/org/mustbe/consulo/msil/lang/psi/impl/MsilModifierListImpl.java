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

package org.mustbe.consulo.msil.lang.psi.impl;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetAttribute;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.msil.lang.psi.ModifierElementType;
import org.mustbe.consulo.msil.lang.psi.MsilModifierList;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilModifierListStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import lombok.val;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilModifierListImpl extends MsilStubElementImpl<MsilModifierListStub> implements MsilModifierList
{
	public MsilModifierListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilModifierListImpl(@NotNull MsilModifierListStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitModifierList(this);
	}

	@NotNull
	@Override
	public DotNetModifier[] getModifiers()
	{
		val modifiers = new ArrayList<DotNetModifier>();
		for(ModifierElementType modifierElementType : MsilTokenSets.MODIFIERS_AS_ARRAY)
		{
			if(hasModifier(modifierElementType))
			{
				modifiers.add(modifierElementType);
			}
		}
		return modifiers.toArray(new DotNetModifier[modifiers.size()]);
	}

	@NotNull
	@Override
	public DotNetAttribute[] getAttributes()
	{
		return new DotNetAttribute[0];
	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		DotNetModifier elementType = modifier;
		if(modifier == DotNetModifier.STATIC)
		{
			elementType = MsilTokens.STATIC_KEYWORD;
		}

		assert elementType instanceof ModifierElementType;
		MsilModifierListStub stub = getStub();
		if(stub != null)
		{
			return stub.hasModififer((ModifierElementType)elementType);
		}
		return hasModifierInTree(elementType);
	}

	@Override
	public boolean hasModifierInTree(@NotNull DotNetModifier modifier)
	{
		DotNetModifier elementType = modifier;
		if(modifier == DotNetModifier.STATIC)
		{
			elementType = MsilTokens.STATIC_KEYWORD;
		}
		assert elementType instanceof ModifierElementType;
		return findChildByType((IElementType) elementType) != null;
	}

	@Nullable
	@Override
	public PsiElement getModifier(IElementType elementType)
	{
		return findChildByType(elementType);
	}
}
