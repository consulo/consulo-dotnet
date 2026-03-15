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
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.lang.psi.MsilParameterList;
import consulo.msil.impl.lang.psi.impl.MsilParameterListImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilParameterListStub;

import java.io.IOException;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilParameterListStubElementType extends AbstractMsilStubElementType<MsilParameterListStub, MsilParameterList>
{
	public MsilParameterListStubElementType()
	{
		super("MSIL_PARAMETER_LIST");
	}

	@Override
	public MsilParameterList createElement(ASTNode astNode)
	{
		return new MsilParameterListImpl(astNode);
	}

	@Override
	public MsilParameterList createPsi(MsilParameterListStub msilParameterListStub)
	{
		return new MsilParameterListImpl(msilParameterListStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilParameterListStub createStub(MsilParameterList msilParameterList, StubElement stubElement)
	{
		return new MsilParameterListStub(stubElement, this);
	}

	@Override
	public void serialize(MsilParameterListStub msilParameterListStub, StubOutputStream stubOutputStream) throws IOException
	{
	}

	@Override
	public MsilParameterListStub deserialize(StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilParameterListStub(stubElement, this);
	}
}
