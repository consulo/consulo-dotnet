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

import consulo.dotnet.psi.impl.DotNetPsiCountUtil;
import consulo.dotnet.psi.DotNetGenericParameterList;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.IStubElementType;
import consulo.msil.lang.psi.MsilGenericParameter;
import consulo.msil.impl.lang.psi.MsilStubElements;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilGenericParameterListStub;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilGenericParameterListImpl extends MsilStubElementImpl<MsilGenericParameterListStub> implements DotNetGenericParameterList
{
	public MsilGenericParameterListImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilGenericParameterListImpl(@Nonnull MsilGenericParameterListStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{

	}

	@Nonnull
	@Override
	public MsilGenericParameter[] getParameters()
	{
		return getStubOrPsiChildren(MsilStubElements.GENERIC_PARAMETER, MsilGenericParameter.ARRAY_FACTORY);
	}

	@Override
	public int getGenericParametersCount()
	{
		return DotNetPsiCountUtil.countChildrenOfType(this, MsilStubElements.GENERIC_PARAMETER);
	}
}
