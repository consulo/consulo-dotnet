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

package org.mustbe.consulo.msil.lang.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.MsilClassGenericType;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilClassGenericTypeStub;
import org.mustbe.consulo.msil.lang.psi.impl.type.MsilClassGenericTypeRefImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilClassGenericTypeImpl extends MsilStubElementImpl<MsilClassGenericTypeStub> implements MsilClassGenericType
{
	public MsilClassGenericTypeImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilClassGenericTypeImpl(@NotNull MsilClassGenericTypeStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{

	}

	@Override
	public String getGenericName()
	{
		MsilClassGenericTypeStub stub = getStub();
		if(stub != null)
		{
			return stub.getName();
		}
		PsiElement childByType = findChildByType(MsilTokens.IDENTIFIER);
		return childByType == null ? "" : childByType.getText();
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetTypeRef toTypeRef()
	{
		MsilClassEntry parent = getStubOrPsiParentOfType(MsilClassEntry.class);
		assert parent != null;
		return new MsilClassGenericTypeRefImpl(parent, getGenericName());
	}
}
