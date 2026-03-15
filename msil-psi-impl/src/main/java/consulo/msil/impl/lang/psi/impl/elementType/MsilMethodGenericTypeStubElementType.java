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
import consulo.msil.lang.psi.MsilMethodGenericType;
import consulo.msil.impl.lang.psi.impl.MsilMethodGenericTypeImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilMethodGenericTypeStub;

import java.io.IOException;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilMethodGenericTypeStubElementType extends AbstractMsilStubElementType<MsilMethodGenericTypeStub, MsilMethodGenericType>
{
	public MsilMethodGenericTypeStubElementType()
	{
		super("MSIL_METHOD_GENERIC_TYPE");
	}

	@Override
	public MsilMethodGenericType createElement(ASTNode astNode)
	{
		return new MsilMethodGenericTypeImpl(astNode);
	}

	@Override
	public MsilMethodGenericType createPsi(MsilMethodGenericTypeStub msilMethodGenericTypeStub)
	{
		return new MsilMethodGenericTypeImpl(msilMethodGenericTypeStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilMethodGenericTypeStub createStub(MsilMethodGenericType msilMethodGenericType, StubElement stubElement)
	{
		int index = msilMethodGenericType.getIndex();
		return new MsilMethodGenericTypeStub(stubElement, this, index);
	}

	@Override
	public void serialize(MsilMethodGenericTypeStub stub, StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeInt(stub.getIndex());
	}

	@Override
	public MsilMethodGenericTypeStub deserialize(StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		int index = inputStream.readInt();
		return new MsilMethodGenericTypeStub(stubElement, this, index);
	}
}
