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
import consulo.dotnet.psi.DotNetNamedElement;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.DotNetXAccessor;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.IStubElementType;
import consulo.msil.lang.psi.MsilEventEntry;
import consulo.msil.impl.lang.psi.MsilStubElements;
import consulo.msil.lang.psi.MsilXAcessor;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilVariableEntryStub;

import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilEventEntryImpl extends MsilQVariableImpl implements MsilEventEntry
{
	public MsilEventEntryImpl(ASTNode node)
	{
		super(node);
	}

	public MsilEventEntryImpl(MsilVariableEntryStub stub, IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitEventEntry(this);
	}

	@Override
	public DotNetXAccessor[] getAccessors()
	{
		return getStubOrPsiChildren(MsilStubElements.XACCESSOR, MsilXAcessor.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@Override
	public DotNetNamedElement[] getMembers()
	{
		return getAccessors();
	}

	@Nullable
	@Override
	public DotNetType getTypeForImplement()
	{
		return null;
	}

	@Override
	public DotNetTypeRef getTypeRefForImplement()
	{
		return DotNetTypeRef.ERROR_TYPE;
	}
}
