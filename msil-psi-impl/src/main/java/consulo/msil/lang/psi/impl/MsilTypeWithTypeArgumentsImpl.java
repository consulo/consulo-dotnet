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
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.DotNetTypeList;
import consulo.dotnet.psi.DotNetTypeWithTypeArguments;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.msil.lang.psi.MsilStubTokenSets;
import consulo.msil.lang.psi.impl.elementType.stub.MsilEmptyTypeStub;
import consulo.msil.lang.psi.impl.type.MsilTypeWithTypeArgumentsRefImpl;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilTypeWithTypeArgumentsImpl extends MsilTypeImpl<MsilEmptyTypeStub> implements DotNetTypeWithTypeArguments
{
	public MsilTypeWithTypeArgumentsImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilTypeWithTypeArgumentsImpl(@Nonnull MsilEmptyTypeStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitTypeWithTypeArguments(this);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetType getInnerType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeList getArgumentsList()
	{
		return getRequiredStubOrPsiChild(MsilStubTokenSets.TYPE_ARGUMENTS_TYPE_LIST);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetType[] getArguments()
	{
		return getArgumentsList().getTypes();
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef toTypeRefImpl()
	{
		DotNetType innerType = getInnerType();
		DotNetType[] arguments = getArguments();
		if(arguments.length == 0)
		{
			return innerType.toTypeRef();
		}

		DotNetTypeRef[] rArguments = new DotNetTypeRef[arguments.length];
		for(int i = 0; i < arguments.length; i++)
		{
			DotNetType argument = arguments[i];
			rArguments[i] = argument.toTypeRef();
		}

		return new MsilTypeWithTypeArgumentsRefImpl(innerType.toTypeRef(), rArguments);
	}
}
