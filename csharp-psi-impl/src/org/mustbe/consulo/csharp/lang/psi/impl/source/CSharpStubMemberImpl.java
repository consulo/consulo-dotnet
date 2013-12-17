/*
 * Copyright 2013 must-be.org
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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetModifierListOwner;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStub;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public abstract class CSharpStubMemberImpl<S extends NamedStub<?>> extends CSharpStubElementImpl<S> implements PsiNameIdentifierOwner,
		DotNetModifierListOwner
{
	public CSharpStubMemberImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpStubMemberImpl(@NotNull S stub, @NotNull IStubElementType<? extends S, ?> nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	@Nullable
	public DotNetModifierList getModifierList()
	{
		return findChildByClass(DotNetModifierList.class);
	}

	@Override
	public boolean hasModifier(@NotNull IElementType modifier)
	{
		DotNetModifierList modifierList = getModifierList();
		return modifierList != null && modifierList.hasModifier(modifier);
	}

	@Override
	@Nullable
	public PsiElement getNameIdentifier()
	{
		return findChildByType(CSharpTokens.IDENTIFIER);
	}

	@Override
	public int getTextOffset()
	{
		PsiElement nameIdentifier = getNameIdentifier();
		if(nameIdentifier != null)
		{
			return nameIdentifier.getTextOffset();
		}
		return super.getTextOffset();
	}

	@Override
	public String getName()
	{
		S stub = getStub();
		if(stub != null)
		{
			return stub.getName();
		}
		PsiElement nameIdentifier = getNameIdentifier();
		return nameIdentifier == null ? null : nameIdentifier.getText();
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}
}