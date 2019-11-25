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

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.lang.psi.impl.DotNetPsiCountUtil;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.DotNetTypeList;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.msil.lang.psi.MsilStubTokenSets;
import consulo.msil.lang.psi.impl.elementType.stub.MsilTypeListStub;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilTypeListImpl extends MsilStubElementImpl<MsilTypeListStub> implements DotNetTypeList
{
	public MsilTypeListImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilTypeListImpl(@Nonnull MsilTypeListStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{

	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetType[] getTypes()
	{
		return getStubOrPsiChildren(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef[] getTypeRefs()
	{
		DotNetType[] types = getTypes();
		if(types.length == 0)
		{
			return DotNetTypeRef.EMPTY_ARRAY;
		}
		DotNetTypeRef[] array = new DotNetTypeRef[types.length];
		for(int i = 0; i < types.length; i++)
		{
			DotNetType type = types[i];
			array[i] = type.toTypeRef();
		}
		return array;
	}

	@RequiredReadAction
	@Override
	public int getTypesCount()
	{
		return DotNetPsiCountUtil.countChildrenOfType(this, MsilStubTokenSets.TYPE_STUBS);
	}
}
