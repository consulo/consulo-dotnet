/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpMethodDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpMethodStub;
import org.mustbe.consulo.dotnet.psi.stub.index.DotNetIndexKeys;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class CSharpMethodStubElementType extends CSharpAbstractStubElementType<CSharpMethodStub, CSharpMethodDeclaration>
{
	public CSharpMethodStubElementType()
	{
		super("METHOD_DECLARATION");
	}

	@Override
	public CSharpMethodDeclaration createPsi(@NotNull ASTNode astNode)
	{
		return new CSharpMethodDeclarationImpl(astNode);
	}

	@Override
	public CSharpMethodDeclaration createPsi(@NotNull CSharpMethodStub cSharpTypeStub)
	{
		return new CSharpMethodDeclarationImpl(cSharpTypeStub, this);
	}

	@Override
	public CSharpMethodStub createStub(@NotNull CSharpMethodDeclaration methodDeclaration, StubElement stubElement)
	{
		return new CSharpMethodStub(stubElement, StringRef.fromNullableString(methodDeclaration.getName()),
				StringRef.fromNullableString(methodDeclaration.getQName()));
	}

	@Override
	public void serialize(@NotNull CSharpMethodStub cSharpTypeStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(cSharpTypeStub.getName());
		stubOutputStream.writeName(cSharpTypeStub.getQName());
	}

	@NotNull
	@Override
	public CSharpMethodStub deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException
	{
		StringRef name = stubInputStream.readName();
		StringRef qname = stubInputStream.readName();
		return new CSharpMethodStub(stubElement, name, qname);
	}

	@Override
	public void indexStub(@NotNull CSharpMethodStub cSharpTypeStub, @NotNull IndexSink indexSink)
	{
		String name = cSharpTypeStub.getName();
		if(!StringUtil.isEmpty(name))
		{
			indexSink.occurrence(DotNetIndexKeys.METHOD_INDEX, name);
		}

		String qName = cSharpTypeStub.getQName();
		if(!StringUtil.isEmpty(qName))
		{
			indexSink.occurrence(DotNetIndexKeys.METHOD_BY_QNAME_INDEX, qName);
		}
	}
}
