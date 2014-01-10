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
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpTypeListStub;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeList;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public class CSharpStubTypeListImpl extends CSharpStubElementImpl<CSharpTypeListStub> implements DotNetTypeList
{
	public CSharpStubTypeListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpStubTypeListImpl(@NotNull CSharpTypeListStub stub, @NotNull IStubElementType<? extends CSharpTypeListStub, ?> nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	@NotNull
	public String[] getTypeTexts()
	{
		DotNetType[] types = getTypes();
		String[] array = new String[types.length];
		for(int i = 0; i < types.length; i++)
		{
			DotNetType type = types[i];
			array[i] = type.getText();
		}
		return array;
	}

	@NotNull
	@Override
	public DotNetType[] getTypes()
	{
		return findChildrenByClass(DotNetType.class);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitTypeList(this);
	}
}