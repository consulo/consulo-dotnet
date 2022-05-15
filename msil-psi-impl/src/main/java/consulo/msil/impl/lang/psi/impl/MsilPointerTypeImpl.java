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
import consulo.dotnet.psi.DotNetPointerType;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.msil.impl.lang.psi.MsilStubTokenSets;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilEmptyTypeStub;
import consulo.msil.impl.lang.psi.impl.type.MsilPointerTypeRefImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilPointerTypeImpl extends MsilTypeImpl<MsilEmptyTypeStub> implements DotNetPointerType
{
	public MsilPointerTypeImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilPointerTypeImpl(@Nonnull MsilEmptyTypeStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitPointerType(this);
	}

	@Nullable
	@Override
	public DotNetType getInnerType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@Nonnull
	@Override
	public PsiElement getAsterisk()
	{
		throw new UnsupportedOperationException();
	}

	@RequiredReadAction
	@Nonnull
	@Override
	protected DotNetTypeRef toTypeRefImpl()
	{
		DotNetType innerType = getInnerType();
		if(innerType == null)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}
		return new MsilPointerTypeRefImpl(innerType.toTypeRef());
	}
}
