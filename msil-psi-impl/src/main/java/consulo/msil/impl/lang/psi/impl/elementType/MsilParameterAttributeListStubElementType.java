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
import consulo.msil.lang.psi.MsilParameterAttributeList;
import consulo.msil.impl.lang.psi.impl.MsilParameterAttributeListImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilParameterAttributeListStub;

import jakarta.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilParameterAttributeListStubElementType extends AbstractMsilStubElementType<MsilParameterAttributeListStub, MsilParameterAttributeList>
{
	public MsilParameterAttributeListStubElementType()
	{
		super("MSIL_PARAMETER_ATTRIBUTE_LIST");
	}

	@Nonnull
	@Override
	public MsilParameterAttributeList createElement(@Nonnull ASTNode astNode)
	{
		return new MsilParameterAttributeListImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilParameterAttributeList createPsi(@Nonnull MsilParameterAttributeListStub msilParameterAttributeListStub)
	{
		return new MsilParameterAttributeListImpl(msilParameterAttributeListStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilParameterAttributeListStub createStub(@Nonnull MsilParameterAttributeList list, StubElement stubElement)
	{
		int index = list.getIndex();
		return new MsilParameterAttributeListStub(stubElement, this, index);
	}

	@Override
	public void serialize(@Nonnull MsilParameterAttributeListStub list, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeVarInt(list.getIndex());
	}

	@Nonnull
	@Override
	public MsilParameterAttributeListStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		int index = inputStream.readVarInt();
		return new MsilParameterAttributeListStub(stubElement, this, index);
	}
}
