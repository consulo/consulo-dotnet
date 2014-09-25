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
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceUtil;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.impl.MsilClassEntryImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilClassEntryStub;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilStubIndexer;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilIndexKeys;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.io.StringRef;

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

	@NotNull
	@Override
	public MsilClassEntry createElement(@NotNull ASTNode astNode)
	{
		return new MsilClassEntryImpl(astNode);
	}

	@NotNull
	@Override
	public MsilClassEntry createPsi(@NotNull MsilClassEntryStub msilClassEntryStub)
	{
		return new MsilClassEntryImpl(msilClassEntryStub, this);
	}

	@Override
	public MsilClassEntryStub createStub(@NotNull MsilClassEntry msilClassEntry, StubElement stubElement)
	{
		String namespace = msilClassEntry.getPresentableParentQName();
		String name = msilClassEntry.getName();
		String vmQName = msilClassEntry.getVmQName();
		return new MsilClassEntryStub(stubElement, this, namespace, name, vmQName);
	}

	@Override
	public void serialize(@NotNull MsilClassEntryStub stub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(stub.getNamespace());
		stubOutputStream.writeName(stub.getName());
		stubOutputStream.writeName(stub.getVmQName());
	}

	@NotNull
	@Override
	public MsilClassEntryStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef namespace = inputStream.readName();
		StringRef name = inputStream.readName();
		StringRef vmQName = inputStream.readName();
		return new MsilClassEntryStub(stubElement, this, namespace, name, vmQName);
	}

	@Override
	public void indexStub(@NotNull MsilClassEntryStub msilClassEntryStub, @NotNull IndexSink indexSink)
	{
		indexSink.occurrence(MsilIndexKeys.TYPE_BY_QNAME_INDEX, msilClassEntryStub.getVmQName());

		String namespace = msilClassEntryStub.getNamespace();
		indexSink.occurrence(MsilIndexKeys.NAMESPACE_INDEX, DotNetNamespaceUtil.getIndexableNamespace(namespace));

		if(!StringUtil.isEmpty(namespace))
		{
			QualifiedName parent = QualifiedName.fromDottedString(namespace);
			do
			{
				indexSink.occurrence(MsilIndexKeys.ALL_NAMESPACE_INDEX, DotNetNamespaceUtil.getIndexableNamespace(parent));
			}
			while((parent = parent.getParent()) != null);
		}

		for(MsilStubIndexer indexer : MsilStubIndexer.EP_NAME.getExtensions())
		{
			indexer.indexClass(msilClassEntryStub, indexSink);
		}
	}
}
