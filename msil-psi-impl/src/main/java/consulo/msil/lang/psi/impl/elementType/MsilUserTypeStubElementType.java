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

package consulo.msil.lang.psi.impl.elementType;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import consulo.annotations.RequiredReadAction;
import consulo.msil.lang.psi.MsilUserType;
import consulo.msil.lang.psi.impl.MsilUserTypeImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilUserTypeStub;

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

	@NotNull
	@Override
	public MsilUserType createElement(@NotNull ASTNode astNode)
	{
		return new MsilUserTypeImpl(astNode);
	}

	@NotNull
	@Override
	public MsilUserType createPsi(@NotNull MsilUserTypeStub msilUserTypeStub)
	{
		return new MsilUserTypeImpl(msilUserTypeStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilUserTypeStub createStub(@NotNull MsilUserType dotNetReferenceType, StubElement stubElement)
	{
		MsilUserType.Target target = dotNetReferenceType.getTarget();
		String referenceText = dotNetReferenceType.getReferenceText();
		return new MsilUserTypeStub(stubElement, this, target, referenceText);
	}

	@Override
	public void serialize(@NotNull MsilUserTypeStub msilUserTypeStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeByte(msilUserTypeStub.getTarget().ordinal());
		stubOutputStream.writeName(msilUserTypeStub.getReferenceText());
	}

	@NotNull
	@Override
	public MsilUserTypeStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		MsilUserType.Target typeResoleKind = MsilUserType.Target.VALUES[inputStream.readByte()];
		StringRef referenceText = inputStream.readName();
		return new MsilUserTypeStub(stubElement, this, typeResoleKind, referenceText);
	}
}
