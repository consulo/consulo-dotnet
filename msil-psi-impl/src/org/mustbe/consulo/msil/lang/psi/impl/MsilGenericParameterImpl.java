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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetTypeList;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilGenericParameter;
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilGenericParameterStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilGenericParameterImpl extends MsilStubElementImpl<MsilGenericParameterStub> implements MsilGenericParameter
{
	public MsilGenericParameterImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilGenericParameterImpl(@NotNull MsilGenericParameterStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{

	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		MsilGenericParameterStub stub = getStub();
		if(stub != null)
		{
			return stub.hasModifier(modifier);
		}

		if(modifier == DotNetModifier.CONTRAVARIANT)
		{
			return findChildByType(MsilTokens.MINUS) != null;
		}
		else if(modifier == DotNetModifier.COVARIANT)
		{
			return findChildByType(MsilTokens.PLUS) != null;
		}
		return false;
	}

	@Nullable
	@Override
	public DotNetModifierList getModifierList()
	{
		return null;
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return findChildByType(MsilTokenSets.IDENTIFIERS);
	}

	@Override
	public String getName()
	{
		MsilGenericParameterStub stub = getStub();
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

	@NotNull
	@Override
	public DotNetTypeRef[] getExtendTypeRefs()
	{
		DotNetTypeList typeList = getStubOrPsiChild(MsilStubElements.GENERIC_PARAM_EXTENDS_LIST);
		if(typeList == null)
		{
			return DotNetTypeRef.EMPTY_ARRAY;
		}
		return typeList.getTypeRefs();
	}

	@NotNull
	@Override
	public DotNetPsiSearcher.TypeResoleKind getTypeKind()
	{
		MsilGenericParameterStub stub = getStub();
		if(stub != null)
		{
			return stub.getTypeKind();
		}

		PsiElement constraintElement = findChildByType(MsilTokenSets.GENERIC_CONSTRAINT_KEYWORDS);
		if(constraintElement == null)
		{
			return DotNetPsiSearcher.TypeResoleKind.UNKNOWN;
		}
		IElementType elementType = constraintElement.getNode().getElementType();
		if(elementType == MsilTokens.CLASS_KEYWORD)
		{
			return DotNetPsiSearcher.TypeResoleKind.CLASS;
		}
		else if(elementType == MsilTokens.VALUETYPE_KEYWORD)
		{
			return DotNetPsiSearcher.TypeResoleKind.STRUCT;
		}
		return DotNetPsiSearcher.TypeResoleKind.UNKNOWN;
	}

	@Override
	public boolean hasDefaultConstructor()
	{
		MsilGenericParameterStub stub = getStub();
		if(stub != null)
		{
			return stub.hasDefaultConstructor();
		}
		return findChildByType(MsilTokens._CTOR_KEYWORD) != null;
	}

	@Override
	public int getIndex()
	{
		PsiElement parentByStub = getParentByStub();
		if(parentByStub instanceof DotNetGenericParameterList)
		{
			return ArrayUtil.find(((DotNetGenericParameterList) parentByStub).getParameters(), this);
		}
		return -1;
	}
}
