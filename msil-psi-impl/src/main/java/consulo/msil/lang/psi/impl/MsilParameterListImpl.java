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
import com.intellij.psi.stubs.IStubElementType;
import consulo.dotnet.lang.psi.impl.DotNetPsiCountUtil;
import consulo.dotnet.psi.DotNetParameter;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.msil.lang.psi.MsilParameter;
import consulo.msil.lang.psi.MsilParameterList;
import consulo.msil.lang.psi.MsilStubElements;
import consulo.msil.lang.psi.impl.elementType.stub.MsilParameterListStub;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilParameterListImpl extends MsilStubElementImpl<MsilParameterListStub> implements MsilParameterList
{
	public MsilParameterListImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilParameterListImpl(@Nonnull MsilParameterListStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public int getParametersCount()
	{
		return DotNetPsiCountUtil.countChildrenOfType(this, MsilStubElements.PARAMETER);
	}

	@Nonnull
	@Override
	public MsilParameter[] getParameters()
	{
		return getStubOrPsiChildren(MsilStubElements.PARAMETER, MsilParameter.ARRAY_FACTORY);
	}

	@Nonnull
	@Override
	public DotNetTypeRef[] getParameterTypeRefs()
	{
		DotNetParameter[] parameters = getParameters();
		if(parameters.length == 0)
		{
			return DotNetTypeRef.EMPTY_ARRAY;
		}
		DotNetTypeRef[] dotNetTypeRefs = new DotNetTypeRef[parameters.length];
		for(int i = 0; i < dotNetTypeRefs.length; i++)
		{
			dotNetTypeRefs[i] = parameters[i].toTypeRef(true);
		}
		return dotNetTypeRefs;
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitElement(this);
	}
}
