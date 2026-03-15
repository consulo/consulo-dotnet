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
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.lang.psi.MsilAssemblyEntry;
import consulo.msil.impl.lang.psi.impl.MsilAssemblyEntryImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilAssemblyEntryStub;

import java.io.IOException;

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

	@Override
	public MsilAssemblyEntry createElement(ASTNode astNode)
	{
		return new MsilAssemblyEntryImpl(astNode);
	}

	@Override
	public MsilAssemblyEntry createPsi(MsilAssemblyEntryStub msilAssemblyEntryStub)
	{
		return new MsilAssemblyEntryImpl(msilAssemblyEntryStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilAssemblyEntryStub createStub(MsilAssemblyEntry msilAssemblyEntry, StubElement stubElement)
	{
		return new MsilAssemblyEntryStub(stubElement, this);
	}

	@Override
	public void serialize(MsilAssemblyEntryStub msilAssemblyEntryStub, StubOutputStream stubOutputStream) throws IOException
	{

	}

	@Override
	public MsilAssemblyEntryStub deserialize(StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilAssemblyEntryStub(stubElement, this);
	}
}
