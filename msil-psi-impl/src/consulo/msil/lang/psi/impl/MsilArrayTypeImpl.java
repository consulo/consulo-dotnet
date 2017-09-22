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

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.ArrayUtil;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.msil.lang.psi.MsilArrayDimension;
import consulo.msil.lang.psi.MsilArrayType;
import consulo.msil.lang.psi.MsilStubElements;
import consulo.msil.lang.psi.MsilStubTokenSets;
import consulo.msil.lang.psi.impl.elementType.stub.MsilEmptyTypeStub;
import consulo.msil.lang.psi.impl.type.MsilArrayTypRefImpl;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilArrayTypeImpl extends MsilTypeImpl<MsilEmptyTypeStub> implements MsilArrayType
{
	public MsilArrayTypeImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilArrayTypeImpl(@NotNull MsilEmptyTypeStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	@RequiredReadAction
	@NotNull
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
	@NotNull
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

		return new MsilArrayTypRefImpl(getProject(), getInnerType().toTypeRef(), lowerValues);
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetType getInnerType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}
}
