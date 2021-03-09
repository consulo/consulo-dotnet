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

package consulo.msil.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.*;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.msil.lang.psi.MsilMethodEntry;
import consulo.msil.lang.psi.MsilParameter;
import consulo.msil.lang.psi.MsilStubTokenSets;
import consulo.msil.lang.psi.MsilTokenSets;
import consulo.msil.lang.psi.impl.elementType.stub.MsilParameterStub;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilParameterImpl extends MsilStubElementImpl<MsilParameterStub> implements MsilParameter
{
	public MsilParameterImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilParameterImpl(@Nonnull MsilParameterStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@RequiredReadAction
	@Override
	public boolean isConstant()
	{
		return false;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement getConstantKeywordElement()
	{
		return null;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromInitializer)
	{
		return getType().toTypeRef();
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetType getType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public DotNetExpression getInitializer()
	{
		int index = getIndex();
		if(index == -1)
		{
			return null;
		}
		DotNetParameterListOwner owner = getOwner();
		if(owner instanceof MsilMethodEntry)
		{
			return ((MsilMethodEntry) owner).getConstantValue(index);
		}
		return null;
	}

	@RequiredReadAction
	@Override
	public boolean hasModifier(@Nonnull DotNetModifier modifier)
	{
		return getModifierList().hasModifier(modifier);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetModifierList getModifierList()
	{
		return getRequiredStubOrPsiChild(MsilStubTokenSets.MODIFIER_LIST);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitParameter(this);
	}

	@Override
	public String getName()
	{
		MsilParameterStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getName();
		}
		PsiElement nameIdentifier = getNameIdentifier();
		return nameIdentifier == null ? null : StringUtil.unquoteString(nameIdentifier.getText());
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return findChildByType(MsilTokenSets.IDENTIFIERS);
	}

	@Override
	public PsiElement setName(@Nonnull String s) throws IncorrectOperationException
	{
		return null;
	}

	@Nullable
	@Override
	public DotNetParameterListOwner getOwner()
	{
		return getStubOrPsiParentOfType(DotNetLikeMethodDeclaration.class);
	}

	@Override
	public int getIndex()
	{
		DotNetParameterList parameterList = getStubOrPsiParentOfType(DotNetParameterList.class);
		assert parameterList != null;
		return ArrayUtil.indexOf(parameterList.getParameters(), this);
	}
}
