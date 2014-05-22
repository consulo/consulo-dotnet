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
import org.mustbe.consulo.msil.lang.psi.MsilEventEntry;
import org.mustbe.consulo.msil.lang.psi.impl.MsilEventEntryImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilEventEntryStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilEventStubElementType extends AbstractMsilStubElementType<MsilEventEntryStub, MsilEventEntry>
{
	public MsilEventStubElementType()
	{
		super("MSIL_EVENT_ENTRY");
	}

	@NotNull
	@Override
	public MsilEventEntry createPsi(@NotNull ASTNode astNode)
	{
		return new MsilEventEntryImpl(astNode);
	}

	@Override
	public MsilEventEntry createPsi(@NotNull MsilEventEntryStub msilEventEntryStub)
	{
		return new MsilEventEntryImpl(msilEventEntryStub, this);
	}

	@Override
	public MsilEventEntryStub createStub(
			@NotNull MsilEventEntry msilEventEntry, StubElement stubElement)
	{
		return new MsilEventEntryStub(stubElement, this);
	}

	@Override
	public void serialize(
			@NotNull MsilEventEntryStub msilEventEntryStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@NotNull
	@Override
	public MsilEventEntryStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilEventEntryStub(stubElement, this);
	}
}
