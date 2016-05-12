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
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceExpression;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.MsilUserType;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilReferenceTypeStub;
import org.mustbe.consulo.msil.lang.psi.impl.type.MsilReferenceTypeRefImpl;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilUserTypeImpl extends MsilTypeImpl<MsilReferenceTypeStub> implements MsilUserType
{
	public MsilUserTypeImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilUserTypeImpl(@NotNull MsilReferenceTypeStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@NotNull
	@Override
	public DotNetPsiSearcher.TypeResoleKind getTypeResoleKind()
	{
		MsilReferenceTypeStub stub = getStub();
		if(stub != null)
		{
			return stub.getTypeResoleKind();
		}
		PsiElement childByType = findChildByType(MsilTokenSets.REFERENCE_TYPE_START);
		if(childByType == null)
		{
			return DotNetPsiSearcher.TypeResoleKind.UNKNOWN;
		}
		if(childByType.getNode().getElementType() == MsilTokens.VALUETYPE_KEYWORD)
		{
			return DotNetPsiSearcher.TypeResoleKind.STRUCT;
		}
		else if(childByType.getNode().getElementType() == MsilTokens.CLASS_KEYWORD)
		{
			return DotNetPsiSearcher.TypeResoleKind.CLASS;
		}
		return DotNetPsiSearcher.TypeResoleKind.UNKNOWN;
	}

	@NotNull
	@Override
	public String getReferenceText()
	{
		MsilReferenceTypeStub stub = getStub();
		if(stub != null)
		{
			return stub.getReferenceText();
		}

		PsiElement childByType = findChildByClass(MsilReferenceExpressionImpl.class);
		if(childByType == null)
		{
			return "";
		}
		else
		{
			return StringUtil.unquoteString(childByType.getText());
		}
	}

	@NotNull
	@Override
	public DotNetReferenceExpression getReferenceExpression()
	{
		return findNotNullChildByClass(MsilReferenceExpressionImpl.class);
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetTypeRef toTypeRefImpl()
	{
		return new MsilReferenceTypeRefImpl(this, getReferenceText(), getTypeResoleKind());
	}
}
