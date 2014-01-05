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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpConstructorStub;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpMethodStub;
import org.mustbe.consulo.dotnet.psi.DotNetConstructorDeclaration;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpConstructorDeclarationImpl extends CSharpMethodDeclarationImpl implements DotNetConstructorDeclaration
{
	public CSharpConstructorDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpConstructorDeclarationImpl(@NotNull CSharpConstructorStub stub, @NotNull IStubElementType<? extends CSharpMethodStub, ?> nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitConstructorDeclaration(this);
	}

	@Override
	public boolean isDeConstructor()
	{
		return findChildByType(CSharpTokens.TILDE) != null;
	}
}
