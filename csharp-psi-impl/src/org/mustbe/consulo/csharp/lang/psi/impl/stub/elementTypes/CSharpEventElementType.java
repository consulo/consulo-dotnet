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
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpEventDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpEventStub;
import org.mustbe.consulo.dotnet.psi.DotNetEventDeclaration;
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
 * @since 21.12.13.
 */
public class CSharpEventElementType extends CSharpAbstractStubElementType<CSharpEventStub, DotNetEventDeclaration>
{
	public CSharpEventElementType()
	{
		super("EVENT_DECLARATION");
	}

	@Override
	public DotNetEventDeclaration createPsi(@NotNull ASTNode astNode)
	{
		return new CSharpEventDeclarationImpl(astNode);
	}

	@Override
	public DotNetEventDeclaration createPsi(@NotNull CSharpEventStub cSharpEventStub)
	{
		return new CSharpEventDeclarationImpl(cSharpEventStub);
	}

	@Override
	public CSharpEventStub createStub(@NotNull DotNetEventDeclaration dotNetEventDeclaration, StubElement stubElement)
	{
		return new CSharpEventStub(stubElement, StringRef.fromNullableString(dotNetEventDeclaration.getName()),
				StringRef.fromNullableString(dotNetEventDeclaration.getParentQName()));
	}

	@Override
	public void serialize(@NotNull CSharpEventStub cSharpEventStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(cSharpEventStub.getName());
		stubOutputStream.writeName(cSharpEventStub.getParentQName());
	}

	@NotNull
	@Override
	public CSharpEventStub deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException
	{
		StringRef name = stubInputStream.readName();
		StringRef parentQName = stubInputStream.readName();
		return new CSharpEventStub(stubElement, name, parentQName);
	}

	@Override
	public void indexStub(@NotNull CSharpEventStub cSharpEventStub, @NotNull IndexSink indexSink)
	{
		String name = cSharpEventStub.getName();
		if(!StringUtil.isEmpty(name))
		{
			indexSink.occurrence(DotNetIndexKeys.EVENT_INDEX, name);
		}
	}
}
