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
import consulo.msil.lang.psi.MsilEventEntry;
import consulo.msil.lang.psi.impl.MsilEventEntryImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilVariableEntryStub;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilEventStubElementType extends AbstractMsilStubElementType<MsilVariableEntryStub, MsilEventEntry>
{
	public MsilEventStubElementType()
	{
		super("MSIL_EVENT_ENTRY");
	}

	@NotNull
	@Override
	public MsilEventEntry createElement(@NotNull ASTNode astNode)
	{
		return new MsilEventEntryImpl(astNode);
	}

	@NotNull
	@Override
	public MsilEventEntry createPsi(@NotNull MsilVariableEntryStub msilEventEntryStub)
	{
		return new MsilEventEntryImpl(msilEventEntryStub, this);
	}

	@Override
	public MsilVariableEntryStub createStub(@NotNull MsilEventEntry eventEntry, StubElement stubElement)
	{
		String nameFromBytecode = eventEntry.getNameFromBytecode();
		return new MsilVariableEntryStub(stubElement, this, nameFromBytecode);
	}

	@Override
	public void serialize(@NotNull MsilVariableEntryStub msilPropertyEntryStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilPropertyEntryStub.getNameFromBytecode());
	}

	@NotNull
	@Override
	public MsilVariableEntryStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef ref = inputStream.readName();
		return new MsilVariableEntryStub(stubElement, this, ref);
	}}
