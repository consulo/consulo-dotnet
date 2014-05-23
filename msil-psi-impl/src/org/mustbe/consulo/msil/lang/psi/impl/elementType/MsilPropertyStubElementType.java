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
import org.mustbe.consulo.msil.lang.psi.MsilPropertyEntry;
import org.mustbe.consulo.msil.lang.psi.impl.MsilPropertyEntryImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilPropertyEntryStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilPropertyStubElementType extends AbstractMsilStubElementType<MsilPropertyEntryStub, MsilPropertyEntry>
{
	public MsilPropertyStubElementType()
	{
		super("MSIL_PROPERTY_ENTRY");
	}

	@NotNull
	@Override
	public MsilPropertyEntry createPsi(@NotNull ASTNode astNode)
	{
		return new MsilPropertyEntryImpl(astNode);
	}

	@NotNull
	@Override
	public MsilPropertyEntry createPsi(@NotNull MsilPropertyEntryStub msilPropertyEntryStub)
	{
		return new MsilPropertyEntryImpl(msilPropertyEntryStub, this);
	}

	@Override
	public MsilPropertyEntryStub createStub(@NotNull MsilPropertyEntry msilPropertyEntry, StubElement stubElement)
	{
		return new MsilPropertyEntryStub(stubElement, this);
	}

	@Override
	public void serialize(@NotNull MsilPropertyEntryStub msilPropertyEntryStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@NotNull
	@Override
	public MsilPropertyEntryStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilPropertyEntryStub(stubElement, this);
	}
}
