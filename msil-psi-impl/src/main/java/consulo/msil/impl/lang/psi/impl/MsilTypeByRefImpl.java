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
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.IStubElementType;
import consulo.msil.impl.lang.psi.MsilStubTokenSets;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilEmptyTypeStub;
import consulo.msil.impl.lang.psi.impl.type.MsilRefTypeRefImpl;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilTypeByRefImpl extends MsilTypeImpl<MsilEmptyTypeStub> implements DotNetType
{
	public MsilTypeByRefImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilTypeByRefImpl(@Nonnull MsilEmptyTypeStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	public DotNetType getInnerType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	protected DotNetTypeRef toTypeRefImpl()
	{
		return new MsilRefTypeRefImpl(getInnerType().toTypeRef());
	}
}
