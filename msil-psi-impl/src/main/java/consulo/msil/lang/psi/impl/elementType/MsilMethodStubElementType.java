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
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import consulo.annotation.access.RequiredReadAction;
import consulo.msil.lang.psi.MsilMethodEntry;
import consulo.msil.lang.psi.impl.MsilMethodEntryImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilMethodEntryStub;
import consulo.msil.lang.psi.impl.elementType.stub.MsilStubIndexer;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilMethodStubElementType extends AbstractMsilStubElementType<MsilMethodEntryStub, MsilMethodEntry>
{
	public MsilMethodStubElementType()
	{
		super("MSIL_METHOD_ENTRY");
	}

	@Nonnull
	@Override
	public MsilMethodEntry createElement(@Nonnull ASTNode astNode)
	{
		return new MsilMethodEntryImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilMethodEntry createPsi(@Nonnull MsilMethodEntryStub msilMethodEntryStub)
	{
		return new MsilMethodEntryImpl(msilMethodEntryStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilMethodEntryStub createStub(@Nonnull MsilMethodEntry msilMethodEntry, StubElement stubElement)
	{
		String name = msilMethodEntry.getNameFromBytecode();
		return new MsilMethodEntryStub(stubElement, this, name);
	}

	@Override
	public void serialize(@Nonnull MsilMethodEntryStub msilMethodEntryStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilMethodEntryStub.getNameFromBytecode());
	}

	@Nonnull
	@Override
	public MsilMethodEntryStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef ref = inputStream.readName();
		return new MsilMethodEntryStub(stubElement, this, StringRef.toString(ref));
	}

	@Override
	public void indexStub(@Nonnull MsilMethodEntryStub msilMethodEntryStub, @Nonnull IndexSink indexSink)
	{
		for(MsilStubIndexer indexer : MsilStubIndexer.EP_NAME.getExtensionList())
		{
			indexer.indexMethod(msilMethodEntryStub, indexSink);
		}
	}
}
