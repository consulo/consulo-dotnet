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
import consulo.msil.lang.psi.MsilUserType;
import consulo.msil.impl.lang.psi.impl.MsilUserTypeImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilUserTypeStub;

import jakarta.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilUserTypeStubElementType extends AbstractMsilStubElementType<MsilUserTypeStub, MsilUserType>
{
	public MsilUserTypeStubElementType()
	{
		super("MSIL_USER_TYPE");
	}

	@Nonnull
	@Override
	public MsilUserType createElement(@Nonnull ASTNode astNode)
	{
		return new MsilUserTypeImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilUserType createPsi(@Nonnull MsilUserTypeStub msilUserTypeStub)
	{
		return new MsilUserTypeImpl(msilUserTypeStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilUserTypeStub createStub(@Nonnull MsilUserType dotNetReferenceType, StubElement stubElement)
	{
		MsilUserType.Target target = dotNetReferenceType.getTarget();
		String referenceText = dotNetReferenceType.getReferenceText();
		return new MsilUserTypeStub(stubElement, this, target, referenceText);
	}

	@Override
	public void serialize(@Nonnull MsilUserTypeStub msilUserTypeStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeByte(msilUserTypeStub.getTarget().ordinal());
		stubOutputStream.writeName(msilUserTypeStub.getReferenceText());
	}

	@Nonnull
	@Override
	public MsilUserTypeStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		MsilUserType.Target typeResoleKind = MsilUserType.Target.VALUES[inputStream.readByte()];
		StringRef referenceText = inputStream.readName();
		return new MsilUserTypeStub(stubElement, this, typeResoleKind, referenceText);
	}
}
