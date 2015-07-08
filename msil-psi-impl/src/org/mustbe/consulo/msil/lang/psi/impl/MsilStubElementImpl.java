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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetElement;
import org.mustbe.consulo.dotnet.util.ArrayUtil2;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProviders;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ArrayFactory;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public abstract class MsilStubElementImpl<T extends StubElement> extends StubBasedPsiElementBase<T> implements StubBasedPsiElement<T>, DotNetElement
{
	protected MsilStubElementImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	protected MsilStubElementImpl(@NotNull T stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public ItemPresentation getPresentation()
	{
		return ItemPresentationProviders.getItemPresentation(this);
	}

	@RequiredReadAction
	@Override
	public int getTextOffset()
	{
		if(this instanceof PsiNameIdentifierOwner)
		{
			PsiElement nameIdentifier = (((PsiNameIdentifierOwner) this).getNameIdentifier());
			return nameIdentifier != null ? nameIdentifier.getTextOffset() : super.getTextOffset();
		}
		return super.getTextOffset();
	}

	@Override
	public void accept(@NotNull PsiElementVisitor visitor)
	{
		if(visitor instanceof MsilVisitor)
		{
			accept((MsilVisitor) visitor);
		}
		else
		{
			visitor.visitElement(this);
		}
	}

	@Nullable
	public <T extends PsiElement> T getFirstStubOrPsiChild(TokenSet tokenSet, ArrayFactory<T> arrayFactory)
	{
		T[] stubOrPsiChildren = getStubOrPsiChildren(tokenSet, arrayFactory);
		return ArrayUtil2.safeGet(stubOrPsiChildren, 0);
	}

	@Nullable
	public <T extends PsiElement> T getStubOrPsiChildByIndex(TokenSet tokenSet, ArrayFactory<T> arrayFactory, int index)
	{
		T[] stubOrPsiChildren = getStubOrPsiChildren(tokenSet, arrayFactory);
		return ArrayUtil2.safeGet(stubOrPsiChildren, index);
	}

	@Override
	public PsiElement getParent()
	{
		return getParentByStub();
	}

	public abstract void accept(MsilVisitor visitor);

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "(" + getNode().getElementType() + ")";
	}
}
