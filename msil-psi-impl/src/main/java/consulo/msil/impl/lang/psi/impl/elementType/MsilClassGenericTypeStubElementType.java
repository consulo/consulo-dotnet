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
import consulo.msil.lang.psi.MsilClassGenericType;
import consulo.msil.impl.lang.psi.impl.MsilClassGenericTypeImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilClassGenericTypeStub;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilClassGenericTypeStubElementType extends AbstractMsilStubElementType<MsilClassGenericTypeStub, MsilClassGenericType>
{
	public MsilClassGenericTypeStubElementType()
	{
		super("MSIL_CLASS_GENERIC_TYPE");
	}

	@Nonnull
	@Override
	public MsilClassGenericType createElement(@Nonnull ASTNode astNode)
	{
		return new MsilClassGenericTypeImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilClassGenericType createPsi(@Nonnull MsilClassGenericTypeStub msilClassGenericTypeStub)
	{
		return new MsilClassGenericTypeImpl(msilClassGenericTypeStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilClassGenericTypeStub createStub(@Nonnull MsilClassGenericType msilClassGenericType, StubElement stubElement)
	{
		String name = msilClassGenericType.getGenericName();
		return new MsilClassGenericTypeStub(stubElement, this, name);
	}

	@Override
	public void serialize(@Nonnull MsilClassGenericTypeStub msilClassGenericTypeStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilClassGenericTypeStub.getName());
	}

	@Nonnull
	@Override
	public MsilClassGenericTypeStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef name = inputStream.readName();
		return new MsilClassGenericTypeStub(stubElement, this, name);
	}
}
