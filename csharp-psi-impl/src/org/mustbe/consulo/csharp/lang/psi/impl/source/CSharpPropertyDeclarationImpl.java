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
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpStubElements;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpPropertyStub;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetPropertyDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetXXXAccessor;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public class CSharpPropertyDeclarationImpl extends CSharpStubVariableImpl<CSharpPropertyStub> implements DotNetPropertyDeclaration
{
	public CSharpPropertyDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpPropertyDeclarationImpl(@NotNull CSharpPropertyStub stub)
	{
		super(stub, CSharpStubElements.PROPERTY_DECLARATION);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitPropertyDeclaration(this);
	}

	@NotNull
	@Override
	public DotNetXXXAccessor[] getAccessors()
	{
		return findChildrenByClass(DotNetXXXAccessor.class);
	}

	@NotNull
	@Override
	public DotNetType getType()
	{
		return findNotNullChildByClass(DotNetType.class);
	}

	@Nullable
	@Override
	public DotNetExpression getInitializer()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetNamedElement[] getMembers()
	{
		return findChildrenByClass(DotNetNamedElement.class);
	}
}
