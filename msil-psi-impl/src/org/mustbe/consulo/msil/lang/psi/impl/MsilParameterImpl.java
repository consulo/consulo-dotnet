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
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilParameter;
import org.mustbe.consulo.msil.lang.psi.MsilStubTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilParameterStub;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilParameterImpl extends MsilStubElementImpl<MsilParameterStub> implements MsilParameter
{
	public MsilParameterImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilParameterImpl(@NotNull MsilParameterStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public boolean isConstant()
	{
		return false;
	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromInitializer)
	{
		return getType().toTypeRef();
	}

	@NotNull
	@Override
	public DotNetType getType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@Nullable
	@Override
	public DotNetExpression getInitializer()
	{
		return null;
	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		return getModifierList().hasModifier(modifier);
	}

	@NotNull
	@Override
	public DotNetModifierList getModifierList()
	{
		return getRequiredStubOrPsiChild(MsilStubTokenSets.MODIFIER_LIST);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{

	}

	@Override
	public String getName()
	{
		MsilParameterStub stub = getStub();
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
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetLikeMethodDeclaration getMethod()
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
