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
import consulo.annotations.RequiredReadAction;
import consulo.msil.lang.psi.MsilAssemblyEntry;
import consulo.msil.lang.psi.impl.MsilAssemblyEntryImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilAssemblyEntryStub;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilAssemblyStubElementType extends AbstractMsilStubElementType<MsilAssemblyEntryStub, MsilAssemblyEntry>
{
	public MsilAssemblyStubElementType()
	{
		super("MSIL_ASSEMBLY_ENTRY");
	}

	@NotNull
	@Override
	public MsilAssemblyEntry createElement(@NotNull ASTNode astNode)
	{
		return new MsilAssemblyEntryImpl(astNode);
	}

	@NotNull
	@Override
	public MsilAssemblyEntry createPsi(@NotNull MsilAssemblyEntryStub msilAssemblyEntryStub)
	{
		return new MsilAssemblyEntryImpl(msilAssemblyEntryStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilAssemblyEntryStub createStub(@NotNull MsilAssemblyEntry msilAssemblyEntry, StubElement stubElement)
	{
		return new MsilAssemblyEntryStub(stubElement, this);
	}

	@Override
	public void serialize(@NotNull MsilAssemblyEntryStub msilAssemblyEntryStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@NotNull
	@Override
	public MsilAssemblyEntryStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilAssemblyEntryStub(stubElement, this);
	}
}
