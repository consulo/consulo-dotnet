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
import org.jetbrains.annotations.Nullable;
import consulo.annotations.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetXXXAccessor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilCustomAttribute;
import org.mustbe.consulo.msil.lang.psi.MsilPropertyEntry;
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.MsilXXXAcessor;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilVariableEntryStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilPropertyEntryImpl extends MsilQVariableImpl implements MsilPropertyEntry
{
	public MsilPropertyEntryImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilPropertyEntryImpl(@NotNull MsilVariableEntryStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@RequiredReadAction
	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		for(DotNetXXXAccessor dotNetXXXAccessor : getAccessors())
		{
			if(dotNetXXXAccessor.hasModifier(modifier))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitPropertyEntry(this);
	}

	@NotNull
	@Override
	public DotNetXXXAccessor[] getAccessors()
	{
		return getStubOrPsiChildren(MsilStubElements.XXX_ACCESSOR, MsilXXXAcessor.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@NotNull
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

	@NotNull
	@Override
	public DotNetTypeRef getTypeRefForImplement()
	{
		return DotNetTypeRef.ERROR_TYPE;
	}

	@RequiredReadAction
	@NotNull
	@Override
	public MsilCustomAttribute[] getAttributes()
	{
		return getStubOrPsiChildren(MsilStubElements.CUSTOM_ATTRIBUTE, MsilCustomAttribute.ARRAY_FACTORY);
	}
}
