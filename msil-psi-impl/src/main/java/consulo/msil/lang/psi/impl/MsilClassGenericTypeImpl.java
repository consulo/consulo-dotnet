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

package consulo.msil.lang.psi.impl;

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.msil.lang.psi.MsilClassGenericType;
import consulo.msil.lang.psi.MsilTokens;
import consulo.msil.lang.psi.impl.elementType.stub.MsilClassGenericTypeStub;
import consulo.msil.lang.psi.impl.type.MsilClassGenericTypeRefImpl;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilClassGenericTypeImpl extends MsilTypeImpl<MsilClassGenericTypeStub> implements MsilClassGenericType
{
	public MsilClassGenericTypeImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilClassGenericTypeImpl(@Nonnull MsilClassGenericTypeStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@RequiredReadAction
	@Override
	public String getGenericName()
	{
		MsilClassGenericTypeStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getName();
		}
		PsiElement childByType = findChildByType(MsilTokens.IDENTIFIER);
		return childByType == null ? "" : childByType.getText();
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef toTypeRefImpl()
	{
		MsilClassEntry parent = getStubOrPsiParentOfType(MsilClassEntry.class);
		assert parent != null;
		return new MsilClassGenericTypeRefImpl(parent, getGenericName());
	}
}
