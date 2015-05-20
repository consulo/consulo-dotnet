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
import org.mustbe.consulo.dotnet.lang.psi.impl.source.resolve.type.SimpleGenericWrapperTypeRef;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeList;
import org.mustbe.consulo.dotnet.psi.DotNetTypeWithTypeArguments;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilStubTokenSets;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilEmptyTypeStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilTypeWithTypeArgumentsImpl extends MsilStubElementImpl<MsilEmptyTypeStub> implements DotNetTypeWithTypeArguments
{
	public MsilTypeWithTypeArgumentsImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilTypeWithTypeArgumentsImpl(@NotNull MsilEmptyTypeStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitTypeWithTypeArguments(this);
	}

	@NotNull
	@Override
	public DotNetType getInnerType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@NotNull
	@Override
	public DotNetTypeList getArgumentsList()
	{
		return getRequiredStubOrPsiChild(MsilStubTokenSets.TYPE_ARGUMENTS_TYPE_LIST);
	}

	@NotNull
	@Override
	public DotNetType[] getArguments()
	{
		return getArgumentsList().getTypes();
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetTypeRef toTypeRef()
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

		return new SimpleGenericWrapperTypeRef(innerType.toTypeRef(), rArguments);
	}
}
