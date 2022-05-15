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

package consulo.msil.impl.lang.psi.impl.elementType;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetGenericParameterList;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.impl.lang.psi.impl.MsilGenericParameterListImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilGenericParameterListStub;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilGenericParameterListStubElementType extends AbstractMsilStubElementType<MsilGenericParameterListStub, DotNetGenericParameterList>
{
	public MsilGenericParameterListStubElementType()
	{
		super("MSIL_GENERIC_PARAMETER_LIST");
	}

	@Nonnull
	@Override
	public DotNetGenericParameterList createElement(@Nonnull ASTNode astNode)
	{
		return new MsilGenericParameterListImpl(astNode);
	}

	@Nonnull
	@Override
	public DotNetGenericParameterList createPsi(@Nonnull MsilGenericParameterListStub msilGenericParameterListStub)
	{
		return new MsilGenericParameterListImpl(msilGenericParameterListStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilGenericParameterListStub createStub(@Nonnull DotNetGenericParameterList dotNetGenericParameterList, StubElement stubElement)
	{
		return new MsilGenericParameterListStub(stubElement, this);
	}

	@Override
	public void serialize(@Nonnull MsilGenericParameterListStub msilGenericParameterListStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@Nonnull
	@Override
	public MsilGenericParameterListStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilGenericParameterListStub(stubElement, this);
	}
}
