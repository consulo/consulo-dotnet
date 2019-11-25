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

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import consulo.annotation.access.RequiredReadAction;
import consulo.msil.lang.psi.MsilFieldEntry;
import consulo.msil.lang.psi.impl.MsilFieldEntryImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilVariableEntryStub;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilFieldStubElementType extends AbstractMsilStubElementType<MsilVariableEntryStub, MsilFieldEntry>
{
	public MsilFieldStubElementType()
	{
		super("MSIL_FIELD_ENTRY");
	}

	@Nonnull
	@Override
	public MsilFieldEntry createElement(@Nonnull ASTNode astNode)
	{
		return new MsilFieldEntryImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilFieldEntry createPsi(@Nonnull MsilVariableEntryStub msilVariableEntryStub)
	{
		return new MsilFieldEntryImpl(msilVariableEntryStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilVariableEntryStub createStub(@Nonnull MsilFieldEntry msilFieldEntry, StubElement stubElement)
	{
		String name = msilFieldEntry.getNameFromBytecode();
		return new MsilVariableEntryStub(stubElement, this, name);
	}

	@Override
	public void serialize(@Nonnull MsilVariableEntryStub msilVariableEntryStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilVariableEntryStub.getNameFromBytecode());
	}

	@Nonnull
	@Override
	public MsilVariableEntryStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef ref = inputStream.readName();
		return new MsilVariableEntryStub(stubElement, this, ref);
	}
}
