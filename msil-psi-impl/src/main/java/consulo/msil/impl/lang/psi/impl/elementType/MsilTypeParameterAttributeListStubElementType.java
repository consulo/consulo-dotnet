/*
 * Copyright 2013-2015 must-be.org
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
import consulo.index.io.StringRef;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.lang.psi.MsilTypeParameterAttributeList;
import consulo.msil.impl.lang.psi.impl.MsilTypeParameterAttributeListImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilTypeParameterAttributeListStub;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 12.06.2015
 */
public class MsilTypeParameterAttributeListStubElementType extends AbstractMsilStubElementType<MsilTypeParameterAttributeListStub, MsilTypeParameterAttributeList>
{
	public MsilTypeParameterAttributeListStubElementType()
	{
		super("MSIL_TYPE_PARAMETER_ATTRIBUTE_LIST");
	}

	@Nonnull
	@Override
	public MsilTypeParameterAttributeList createElement(@Nonnull ASTNode astNode)
	{
		return new MsilTypeParameterAttributeListImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilTypeParameterAttributeList createPsi(@Nonnull MsilTypeParameterAttributeListStub msilParameterAttributeListStub)
	{
		return new MsilTypeParameterAttributeListImpl(msilParameterAttributeListStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilTypeParameterAttributeListStub createStub(@Nonnull MsilTypeParameterAttributeList list, StubElement stubElement)
	{
		String name = list.getGenericParameterName();
		return new MsilTypeParameterAttributeListStub(stubElement, this, name);
	}

	@Override
	public void serialize(@Nonnull MsilTypeParameterAttributeListStub list, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(list.getGenericParameterName());
	}

	@Nonnull
	@Override
	public MsilTypeParameterAttributeListStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef name = inputStream.readName();
		return new MsilTypeParameterAttributeListStub(stubElement, this, name);
	}
}
