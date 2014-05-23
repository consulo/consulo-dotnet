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
	public MsilCustomAttribute createPsi(@NotNull ASTNode astNode)
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
		return new MsilCustomAttributeStub(stubElement, this, (StringRef)null);
	}

	@Override
	public void serialize(
			@NotNull MsilCustomAttributeStub msilCustomAttributeStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@NotNull
	@Override
	public MsilCustomAttributeStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilCustomAttributeStub(stubElement, this, (StringRef)null);
	}
}
