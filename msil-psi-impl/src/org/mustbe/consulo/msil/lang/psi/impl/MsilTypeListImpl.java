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
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeList;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilTypeListStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilTypeListImpl extends MsilStubElementImpl<MsilTypeListStub> implements DotNetTypeList
{
	public MsilTypeListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilTypeListImpl(@NotNull MsilTypeListStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{

	}

	@NotNull
	@Override
	public DotNetType[] getTypes()
	{
		return new DotNetType[0];
	}

	@NotNull
	@Override
	public DotNetTypeRef[] getTypeRefs()
	{
		return new DotNetTypeRef[0];
	}

	@NotNull
	@Override
	public String[] getTypeTexts()
	{
		return new String[0];
	}
}
