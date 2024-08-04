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
import consulo.dotnet.psi.DotNetExpression;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.IStubElementType;
import consulo.msil.lang.psi.MsilCustomAttribute;
import consulo.msil.lang.psi.MsilFieldEntry;
import consulo.msil.impl.lang.psi.MsilStubElements;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilVariableEntryStub;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilFieldEntryImpl extends MsilQVariableImpl implements MsilFieldEntry
{
	public MsilFieldEntryImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilFieldEntryImpl(@Nonnull MsilVariableEntryStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public MsilCustomAttribute[] getAttributes()
	{
		return getStubOrPsiChildren(MsilStubElements.CUSTOM_ATTRIBUTE, MsilCustomAttribute.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public DotNetExpression getInitializer()
	{
		return getStubOrPsiChild(MsilStubElements.CONSTANT_VALUE);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitFieldEntry(this);
	}
}
