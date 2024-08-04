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
import consulo.dotnet.psi.impl.stub.DotNetNamespaceStubUtil;
import consulo.index.io.StringRef;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.msil.impl.lang.psi.impl.MsilClassEntryImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilClassEntryStub;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilStubIndexer;
import consulo.msil.impl.lang.psi.impl.elementType.stub.index.MsilIndexKeys;

import jakarta.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilClassStubElementType extends AbstractMsilStubElementType<MsilClassEntryStub, MsilClassEntry>
{
	public MsilClassStubElementType()
	{
		super("MSIL_CLASS_ENTRY");
	}

	@Nonnull
	@Override
	public MsilClassEntry createElement(@Nonnull ASTNode astNode)
	{
		return new MsilClassEntryImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilClassEntry createPsi(@Nonnull MsilClassEntryStub msilClassEntryStub)
	{
		return new MsilClassEntryImpl(msilClassEntryStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilClassEntryStub createStub(@Nonnull MsilClassEntry msilClassEntry, StubElement stubElement)
	{
		String namespace = msilClassEntry.getPresentableParentQName();
		String name = msilClassEntry.getName();
		String vmQName = msilClassEntry.getVmQName();
		return new MsilClassEntryStub(stubElement, this, namespace, name, vmQName);
	}

	@Override
	public void serialize(@Nonnull MsilClassEntryStub stub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(stub.getNamespace());
		stubOutputStream.writeName(stub.getName());
		stubOutputStream.writeName(stub.getVmQName());
	}

	@Nonnull
	@Override
	public MsilClassEntryStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef namespace = inputStream.readName();
		StringRef name = inputStream.readName();
		StringRef vmQName = inputStream.readName();
		return new MsilClassEntryStub(stubElement, this, namespace, name, vmQName);
	}

	@Override
	public void indexStub(@Nonnull MsilClassEntryStub msilClassEntryStub, @Nonnull IndexSink indexSink)
	{
		indexSink.occurrence(MsilIndexKeys.TYPE_BY_NAME_INDEX, MsilHelper.cutGenericMarker(msilClassEntryStub.getName()));
		indexSink.occurrence(MsilIndexKeys.TYPE_BY_QNAME_INDEX, msilClassEntryStub.getVmQName().hashCode());

		if(!msilClassEntryStub.isNested())
		{
			DotNetNamespaceStubUtil.indexStub(indexSink, MsilIndexKeys.ELEMENT_BY_QNAME_INDEX, MsilIndexKeys.NAMESPACE_INDEX, msilClassEntryStub.getNamespace(), msilClassEntryStub.getName());
		}

		for(MsilStubIndexer indexer : MsilStubIndexer.EP_NAME.getExtensionList())
		{
			indexer.indexClass(msilClassEntryStub, indexSink);
		}
	}
}
