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
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingNamespaceListImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpUsingNamespaceListStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;

/**
 * @author VISTALL
 * @since 15.01.14
 */
public class CSharpUsingNamespaceListStubElementType extends CSharpAbstractStubElementType<CSharpUsingNamespaceListStub, CSharpUsingNamespaceListImpl>
{
	public CSharpUsingNamespaceListStubElementType()
	{
		super("USING_NAMESPACE_LIST");
	}

	@Override
	public CSharpUsingNamespaceListImpl createPsi(@NotNull ASTNode astNode)
	{
		return new CSharpUsingNamespaceListImpl(astNode);
	}

	@Override
	public CSharpUsingNamespaceListImpl createPsi(@NotNull CSharpUsingNamespaceListStub cSharpUsingNamespaceListStub)
	{
		return new CSharpUsingNamespaceListImpl(cSharpUsingNamespaceListStub);
	}

	@Override
	public CSharpUsingNamespaceListStub createStub(@NotNull CSharpUsingNamespaceListImpl cSharpUsingNamespaceList, StubElement stubElement)
	{
		return new CSharpUsingNamespaceListStub(stubElement, this);
	}

	@Override
	public void serialize(@NotNull CSharpUsingNamespaceListStub cSharpUsingNamespaceListStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@NotNull
	@Override
	public CSharpUsingNamespaceListStub deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException
	{
		return new CSharpUsingNamespaceListStub(stubElement, this);
	}
}
