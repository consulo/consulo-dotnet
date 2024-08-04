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
import consulo.index.io.StringRef;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.lang.psi.MsilParameter;
import consulo.msil.impl.lang.psi.impl.MsilParameterImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilParameterStub;

import jakarta.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilParameterStubElementType extends AbstractMsilStubElementType<MsilParameterStub, MsilParameter>
{
	public MsilParameterStubElementType()
	{
		super("MSIL_PARAMETER");
	}

	@Nonnull
	@Override
	public MsilParameter createElement(@Nonnull ASTNode astNode)
	{
		return new MsilParameterImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilParameter createPsi(@Nonnull MsilParameterStub msilParameterStub)
	{
		return new MsilParameterImpl(msilParameterStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilParameterStub createStub(@Nonnull MsilParameter msilParameter, StubElement stubElement)
	{
		String name = msilParameter.getName();
		return new MsilParameterStub(stubElement, this, name);
	}

	@Override
	public void serialize(@Nonnull MsilParameterStub msilParameterStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilParameterStub.getName());
	}

	@Nonnull
	@Override
	public MsilParameterStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef name = inputStream.readName();
		return new MsilParameterStub(stubElement, this, StringRef.toString(name));
	}
}
