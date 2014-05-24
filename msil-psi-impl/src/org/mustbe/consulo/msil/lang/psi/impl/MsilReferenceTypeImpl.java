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
import org.mustbe.consulo.dotnet.psi.DotNetReferenceExpression;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilReferenceType;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
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
public class MsilReferenceTypeImpl extends MsilStubElementImpl<MsilReferenceTypeStub> implements MsilReferenceType
{
	public MsilReferenceTypeImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilReferenceTypeImpl(@NotNull MsilReferenceTypeStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{

	}

	@NotNull
	@Override
	public DotNetPsiFacade.TypeResoleKind getTypeResoleKind()
	{
		MsilReferenceTypeStub stub = getStub();
		if(stub != null)
		{
			return stub.getTypeResoleKind();
		}
		PsiElement childByType = findChildByType(MsilTokenSets.REFERENCE_TYPE_START);
		if(childByType == null)
		{
			return DotNetPsiFacade.TypeResoleKind.UNKNOWN;
		}
		if(childByType.getNode().getElementType() == MsilTokens.VALUETYPE_KEYWORD)
		{
			return DotNetPsiFacade.TypeResoleKind.STRUCT;
		}
		else if(childByType.getNode().getElementType() == MsilTokens.CLASS_KEYWORD)
		{
			return DotNetPsiFacade.TypeResoleKind.CLASS;
		}
		return DotNetPsiFacade.TypeResoleKind.UNKNOWN;
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
			return childByType.getText();
		}
	}

	@NotNull
	@Override
	public DotNetReferenceExpression getReferenceExpression()
	{
		return findNotNullChildByClass(MsilReferenceExpressionImpl.class);
	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef()
	{
		return new MsilReferenceTypeRefImpl(getProject(), getReferenceText(), getNestedClassName(), getTypeResoleKind());
	}

	@Nullable
	@Override
	public String getNestedClassName()
	{
		MsilReferenceTypeStub stub = getStub();
		if(stub != null)
		{
			return stub.getNestedClassText();
		}
		PsiElement childByType = findChildByType(MsilTokenSets.IDENTIFIERS);
		return childByType == null ? null : StringUtil.unquoteString(childByType.getText());
	}
}
