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

package org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpConversionMethodDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpConversionMethodStub;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.MemberStub;
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
 * @since 09.01.14
 */
public class CSharpConversionMethodStubElementType extends CSharpAbstractStubElementType<CSharpConversionMethodStub,
		CSharpConversionMethodDeclarationImpl>
{
	public CSharpConversionMethodStubElementType()
	{
		super("CONVERSION_METHOD_DECLARATION");
	}

	@Override
	public CSharpConversionMethodDeclarationImpl createPsi(@NotNull ASTNode astNode)
	{
		return new CSharpConversionMethodDeclarationImpl(astNode);
	}

	@Override
	public CSharpConversionMethodDeclarationImpl createPsi(@NotNull CSharpConversionMethodStub cSharpTypeStub)
	{
		return new CSharpConversionMethodDeclarationImpl(cSharpTypeStub);
	}

	@Override
	public CSharpConversionMethodStub createStub(@NotNull CSharpConversionMethodDeclarationImpl methodDeclaration, StubElement stubElement)
	{
		StringRef name = StringRef.fromNullableString(methodDeclaration.getName());
		StringRef qname = StringRef.fromNullableString(methodDeclaration.getPresentableParentQName());
		int modifierMask = MemberStub.getModifierMask(methodDeclaration);
		return new CSharpConversionMethodStub(stubElement, name, qname, modifierMask);
	}

	@Override
	public void serialize(@NotNull CSharpConversionMethodStub cSharpTypeStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(cSharpTypeStub.getName());
		stubOutputStream.writeName(cSharpTypeStub.getParentQName());
		stubOutputStream.writeInt(cSharpTypeStub.getModifierMask());
	}

	@NotNull
	@Override
	public CSharpConversionMethodStub deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException
	{
		StringRef name = stubInputStream.readName();
		StringRef qname = stubInputStream.readName();
		int modifierMask = stubInputStream.readInt();
		return new CSharpConversionMethodStub(stubElement, name, qname, modifierMask);
	}

	@Override
	public void indexStub(@NotNull CSharpConversionMethodStub cSharpTypeStub, @NotNull IndexSink indexSink)
	{
		String name = cSharpTypeStub.getName();
		if(!StringUtil.isEmpty(name))
		{
			indexSink.occurrence(DotNetIndexKeys.METHOD_INDEX, name);
		}
	}
}