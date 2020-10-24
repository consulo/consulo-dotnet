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
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetReferenceExpression;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.msil.lang.psi.MsilTokenSets;
import consulo.msil.lang.psi.MsilTokens;
import consulo.msil.lang.psi.MsilUserType;
import consulo.msil.lang.psi.impl.elementType.stub.MsilUserTypeStub;
import consulo.msil.lang.psi.impl.type.MsilReferenceTypeRefImpl;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilUserTypeImpl extends MsilTypeImpl<MsilUserTypeStub> implements MsilUserType
{
	public MsilUserTypeImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilUserTypeImpl(@Nonnull MsilUserTypeStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public Target getTarget()
	{
		MsilUserTypeStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getTarget();
		}
		PsiElement childByType = findChildByType(MsilTokenSets.REFERENCE_TYPE_START);
		if(childByType == null)
		{
			return Target.CLASS;
		}
		if(childByType.getNode().getElementType() == MsilTokens.VALUETYPE_KEYWORD)
		{
			return Target.STRUCT;
		}
		else if(childByType.getNode().getElementType() == MsilTokens.CLASS_KEYWORD)
		{
			return Target.CLASS;
		}
		return Target.CLASS;
	}

	@Nonnull
	@Override
	public String getReferenceText()
	{
		MsilUserTypeStub stub = getGreenStub();
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

	@Nonnull
	@Override
	public DotNetReferenceExpression getReferenceExpression()
	{
		return findNotNullChildByClass(MsilReferenceExpressionImpl.class);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef toTypeRefImpl()
	{
		return new MsilReferenceTypeRefImpl(getProject(), getResolveScope(), getReferenceText());
	}
}
