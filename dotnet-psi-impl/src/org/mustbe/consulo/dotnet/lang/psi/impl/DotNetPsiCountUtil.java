/*
 * Copyright 2013-2015 must-be.org
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

package org.mustbe.consulo.dotnet.lang.psi.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 23.01.15
 */
public class DotNetPsiCountUtil
{
	public static int countChildrenOfType(@NotNull StubBasedPsiElement<?> psiElement, @NotNull Class<? extends PsiElement> classOfElement)
	{
		StubElement stub = psiElement.getStub();
		if(stub != null)
		{
			return countChildrenOfType(stub, classOfElement);
		}
		return countChildrenOfType(psiElement.getNode(), classOfElement);
	}

	public static int countChildrenOfType(@NotNull StubBasedPsiElement<?> psiElement, @NotNull IElementType elementType)
	{
		StubElement stub = psiElement.getStub();
		if(stub != null)
		{
			return countChildrenOfType(stub, elementType);
		}
		return countChildrenOfType(psiElement.getNode(), elementType);
	}

	public static int countChildrenOfType(@NotNull StubBasedPsiElement<?> psiElement, @NotNull TokenSet tokenSet)
	{
		StubElement stub = psiElement.getStub();
		if(stub != null)
		{
			return countChildrenOfType(stub, tokenSet);
		}
		return countChildrenOfType(psiElement.getNode(), tokenSet);
	}

	public static int countChildrenOfType(@NotNull ASTNode node, @NotNull IElementType elementType)
	{
		int count = 0;
		for(ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext())
		{
			if(child.getElementType() == elementType)
			{
				count++;
			}
		}

		return count;
	}

	public static int countChildrenOfType(@NotNull ASTNode node, @NotNull TokenSet tokenSet)
	{
		int count = 0;
		for(ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext())
		{
			if(tokenSet.contains(child.getElementType()))
			{
				count++;
			}
		}

		return count;
	}

	public static int countChildrenOfType(@NotNull StubElement<?> stubElement, final IElementType elementType)
	{
		int count = 0;
		List<StubElement> childrenStubs = stubElement.getChildrenStubs();
		//noinspection ForLoopReplaceableByForEach
		for(int i = 0, childrenStubsSize = childrenStubs.size(); i < childrenStubsSize; i++)
		{
			StubElement childStub = childrenStubs.get(i);
			if(elementType == childStub.getStubType())
			{
				count++;
			}
		}

		return count;
	}

	public static int countChildrenOfType(@NotNull StubElement<?> stubElement, final TokenSet types)
	{
		int count = 0;
		List<StubElement> childrenStubs = stubElement.getChildrenStubs();
		//noinspection ForLoopReplaceableByForEach
		for(int i = 0, childrenStubsSize = childrenStubs.size(); i < childrenStubsSize; i++)
		{
			StubElement childStub = childrenStubs.get(i);
			if(types.contains(childStub.getStubType()))
			{
				count++;
			}
		}

		return count;
	}

	public static int countChildrenOfType(@NotNull ASTNode node, @NotNull Class<? extends PsiElement> classOfElement)
	{
		int count = 0;
		for(ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext())
		{
			if(classOfElement.isInstance(child.getPsi()))
			{
				count++;
			}
		}

		return count;
	}

	public static int countChildrenOfType(@NotNull StubElement<?> stubElement, final Class<? extends PsiElement> classOfElement)
	{
		int count = 0;
		List<StubElement> childrenStubs = stubElement.getChildrenStubs();
		//noinspection ForLoopReplaceableByForEach
		for(int i = 0, childrenStubsSize = childrenStubs.size(); i < childrenStubsSize; i++)
		{
			StubElement childStub = childrenStubs.get(i);
			if(classOfElement.isInstance(childStub.getPsi()))
			{
				count++;
			}
		}

		return count;
	}
}
