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

package consulo.msil.impl.lang.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetElement;
import consulo.dotnet.util.ArrayUtil2;
import consulo.language.ast.ASTNode;
import consulo.language.ast.TokenSet;
import consulo.language.impl.psi.stub.StubBasedPsiElementBase;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiNameIdentifierOwner;
import consulo.language.psi.StubBasedPsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubElement;
import consulo.navigation.ItemPresentation;
import consulo.navigation.ItemPresentationProvider;
import consulo.util.collection.ArrayFactory;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public abstract class MsilStubElementImpl<T extends StubElement> extends StubBasedPsiElementBase<T> implements StubBasedPsiElement<T>, DotNetElement
{
	protected MsilStubElementImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	protected MsilStubElementImpl(@Nonnull T stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public ItemPresentation getPresentation()
	{
		return ItemPresentationProvider.getItemPresentation(this);
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
	public void accept(@Nonnull PsiElementVisitor visitor)
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

	public abstract void accept(MsilVisitor visitor);

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "(" + getNode().getElementType() + ")";
	}
}
