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

package org.mustbe.consulo.msil.lang.psi.impl.elementType;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.msil.lang.psi.MsilCustomAttribute;
import org.mustbe.consulo.msil.lang.psi.impl.MsilCustomAttributeImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilCustomAttributeStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilCustomAttributeStubElementType extends AbstractMsilStubElementType<MsilCustomAttributeStub, MsilCustomAttribute>
{
	public MsilCustomAttributeStubElementType()
	{
		super("MSIL_CUSTOM_ATTRIBTE");
	}

	@NotNull
	@Override
	public MsilCustomAttribute createElement(@NotNull ASTNode astNode)
	{
		return new MsilCustomAttributeImpl(astNode);
	}

	@NotNull
	@Override
	public MsilCustomAttribute createPsi(@NotNull MsilCustomAttributeStub msilCustomAttributeStub)
	{
		return new MsilCustomAttributeImpl(msilCustomAttributeStub, this);
	}

	@Override
	public MsilCustomAttributeStub createStub(
			@NotNull MsilCustomAttribute msilCustomAttribute, StubElement stubElement)
	{
		DotNetType type = msilCustomAttribute.getType();
		String ref = type == null ? null : type.getText();
		return new MsilCustomAttributeStub(stubElement, this, ref);
	}

	@Override
	public void serialize(
			@NotNull MsilCustomAttributeStub msilCustomAttributeStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilCustomAttributeStub.getTypeRef());
	}

	@NotNull
	@Override
	public MsilCustomAttributeStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef ref = inputStream.readName();
		return new MsilCustomAttributeStub(stubElement, this, ref);
	}
}
