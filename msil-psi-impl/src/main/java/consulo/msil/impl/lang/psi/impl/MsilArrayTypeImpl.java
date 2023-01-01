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
import consulo.msil.lang.psi.MsilArrayDimension;
import consulo.msil.lang.psi.MsilArrayType;
import consulo.msil.impl.lang.psi.MsilStubElements;
import consulo.msil.impl.lang.psi.MsilStubTokenSets;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilEmptyTypeStub;
import consulo.msil.impl.lang.psi.impl.type.MsilArrayTypRefImpl;
import consulo.util.collection.ArrayUtil;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilArrayTypeImpl extends MsilTypeImpl<MsilEmptyTypeStub> implements MsilArrayType
{
	public MsilArrayTypeImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilArrayTypeImpl(@Nonnull MsilEmptyTypeStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	@RequiredReadAction
	@Nonnull
	public MsilArrayDimension[] getDimensions()
	{
		return getStubOrPsiChildren(MsilStubElements.ARRAY_DIMENSION, MsilArrayDimension.ARRAY_FACTORY);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitArrayType(this);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	protected DotNetTypeRef toTypeRefImpl()
	{
		int[] lowerValues = ArrayUtil.EMPTY_INT_ARRAY;
		MsilArrayDimension[] dimensions = getDimensions();
		if(dimensions.length > 0)
		{
			lowerValues = new int[dimensions.length];
			for(int i = 0; i < dimensions.length; i++)
			{
				MsilArrayDimension dimension = dimensions[i];
				lowerValues[i] = dimension.getLowerValue();
			}
		}

		return new MsilArrayTypRefImpl(getInnerType().toTypeRef(), lowerValues);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetType getInnerType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}
}
