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
import consulo.annotations.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.MsilStubTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilVariableEntryStub;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 24.05.14
 */
public abstract class MsilQVariableImpl extends MsilStubElementImpl<MsilVariableEntryStub> implements DotNetVariable, DotNetQualifiedElement
{
	public MsilQVariableImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilQVariableImpl(@NotNull MsilVariableEntryStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement getConstantKeywordElement()
	{
		return null;
	}

	@RequiredReadAction
	@Override
	public boolean isConstant()
	{
		return false;
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromInitializer)
	{
		return getType().toTypeRef();
	}

	@RequiredReadAction
	@NotNull
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
		return null;
	}

	@RequiredReadAction
	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		return getModifierList().hasModifier(modifier);
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetModifierList getModifierList()
	{
		return getRequiredStubOrPsiChild(MsilStubElements.MODIFIER_LIST);
	}

	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		return StringUtil.getPackageName(getNameFromBytecode());
	}

	@Nullable
	@Override
	public String getPresentableQName()
	{
		return getNameFromBytecode();
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
		return StringUtil.getShortName(getNameFromBytecode());
	}

	@NotNull
	public String getNameFromBytecode()
	{
		MsilVariableEntryStub stub = getStub();
		if(stub != null)
		{
			return stub.getNameFromBytecode();
		}
		PsiElement element = getNameIdentifier();
		return element == null ? "" : StringUtil.unquoteString(element.getText());
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}
}
