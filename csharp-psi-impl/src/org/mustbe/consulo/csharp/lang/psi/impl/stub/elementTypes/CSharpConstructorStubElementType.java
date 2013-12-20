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
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpConstructorDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpConstructorStub;
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
public class CSharpConstructorStubElementType extends CSharpAbstractStubElementType<CSharpConstructorStub, CSharpConstructorDeclarationImpl>
{
	public CSharpConstructorStubElementType()
	{
		super("CONSTRUCTOR_DECLARATION");
	}

	@Override
	public CSharpConstructorDeclarationImpl createPsi(@NotNull ASTNode astNode)
	{
		return new CSharpConstructorDeclarationImpl(astNode);
	}

	@Override
	public CSharpConstructorDeclarationImpl createPsi(@NotNull CSharpConstructorStub cSharpTypeStub)
	{
		return new CSharpConstructorDeclarationImpl(cSharpTypeStub, this);
	}

	@Override
	public CSharpConstructorStub createStub(@NotNull CSharpConstructorDeclarationImpl methodDeclaration, StubElement stubElement)
	{
		return new CSharpConstructorStub(stubElement, StringRef.fromNullableString(methodDeclaration.getName()),
				StringRef.fromNullableString(methodDeclaration.getParentQName()));
	}

	@Override
	public void serialize(@NotNull CSharpConstructorStub cSharpTypeStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(cSharpTypeStub.getName());
		stubOutputStream.writeName(cSharpTypeStub.getParentQName());
	}

	@NotNull
	@Override
	public CSharpConstructorStub deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException
	{
		StringRef name = stubInputStream.readName();
		StringRef qname = stubInputStream.readName();
		return new CSharpConstructorStub(stubElement, name, qname);
	}

	@Override
	public void indexStub(@NotNull CSharpConstructorStub cSharpTypeStub, @NotNull IndexSink indexSink)
	{
		String name = cSharpTypeStub.getName();
		if(!StringUtil.isEmpty(name))
		{
			indexSink.occurrence(DotNetIndexKeys.METHOD_INDEX, name);

			String parentQName = cSharpTypeStub.getParentQName();
			indexSink.occurrence(DotNetIndexKeys.MEMBER_BY_NAMESPACE_QNAME_INDEX, parentQName);

			indexSink.occurrence(DotNetIndexKeys.METHOD_BY_QNAME_INDEX, StringUtil.isEmpty(parentQName) ? name : parentQName + "." + name);
		}
	}
}