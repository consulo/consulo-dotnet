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
import consulo.annotation.access.RequiredWriteAction;
import consulo.dotnet.psi.*;
import consulo.dotnet.psi.resolve.DotNetPsiSearcher;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.util.IncorrectOperationException;
import consulo.msil.impl.lang.psi.MsilStubElements;
import consulo.msil.impl.lang.psi.MsilTokenSets;
import consulo.msil.impl.lang.psi.MsilTokens;
import consulo.msil.lang.psi.*;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilGenericParameterStub;
import consulo.util.collection.ArrayUtil;
import org.jetbrains.annotations.NonNls;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilGenericParameterImpl extends MsilStubElementImpl<MsilGenericParameterStub> implements MsilGenericParameter
{
	public MsilGenericParameterImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilGenericParameterImpl(@Nonnull MsilGenericParameterStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
	}

	@RequiredReadAction
	@Override
	public boolean hasModifier(@Nonnull DotNetModifier modifier)
	{
		MsilGenericParameterStub stub = getGreenStub();
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

	@RequiredReadAction
	@Nullable
	@Override
	public DotNetModifierList getModifierList()
	{
		return null;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return findChildByType(MsilTokenSets.IDENTIFIERS);
	}

	@RequiredReadAction
	@Override
	public String getName()
	{
		MsilGenericParameterStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getName();
		}
		PsiElement nameIdentifier = getNameIdentifier();
		return nameIdentifier == null ? null : nameIdentifier.getText();
	}

	@RequiredWriteAction
	@Override
	public PsiElement setName(@NonNls @Nonnull String s) throws IncorrectOperationException
	{
		return null;
	}

	@RequiredReadAction
	@Nonnull
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

	@RequiredReadAction
	@Nullable
	@Override
	public MsilUserType.Target getTarget()
	{
		MsilGenericParameterStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getTarget();
		}
		PsiElement constraintElement = findChildByType(MsilTokenSets.GENERIC_CONSTRAINT_KEYWORDS);
		if(constraintElement == null)
		{
			return null;
		}
		IElementType elementType = constraintElement.getNode().getElementType();
		if(elementType == MsilTokens.CLASS_KEYWORD)
		{
			return MsilUserType.Target.CLASS;
		}
		else if(elementType == MsilTokens.VALUETYPE_KEYWORD)
		{
			return MsilUserType.Target.STRUCT;
		}
		return null;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetPsiSearcher.TypeResoleKind getTypeKind()
	{
		MsilUserType.Target target = getTarget();
		if(target == null)
		{
			return DotNetPsiSearcher.TypeResoleKind.UNKNOWN;
		}
		switch(target)
		{
			case CLASS:
				return DotNetPsiSearcher.TypeResoleKind.CLASS;
			case STRUCT:
				return DotNetPsiSearcher.TypeResoleKind.STRUCT;
			default:
				throw new IllegalArgumentException();
		}
	}

	@RequiredReadAction
	@Override
	public boolean hasDefaultConstructor()
	{
		MsilGenericParameterStub stub = getGreenStub();
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

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetAttribute[] getAttributes()
	{
		PsiElement parentByStub = getStubOrPsiParentOfType(MsilClassEntry.class);
		if(parentByStub != null)
		{
			String name = getName();
			if(name == null)
			{
				return DotNetAttribute.EMPTY_ARRAY;
			}
			return ((MsilClassEntry) parentByStub).getGenericParameterAttributes(name);
		}
		parentByStub = getStubOrPsiParentOfType(MsilMethodEntry.class);
		if(parentByStub != null)
		{
			String name = getName();
			if(name == null)
			{
				return DotNetAttribute.EMPTY_ARRAY;
			}
			return ((MsilMethodEntry) parentByStub).getGenericParameterAttributes(name);
		}
		return MsilCustomAttribute.EMPTY_ARRAY;
	}
}
