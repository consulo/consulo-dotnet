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
import consulo.msil.lang.psi.MsilMethodEntry;
import consulo.msil.lang.psi.MsilMethodGenericType;
import consulo.msil.lang.psi.MsilTokens;
import consulo.msil.lang.psi.impl.elementType.stub.MsilMethodGenericTypeStub;
import consulo.msil.lang.psi.impl.type.MsilMethodGenericTypeRefImpl;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilMethodGenericTypeImpl extends MsilTypeImpl<MsilMethodGenericTypeStub> implements MsilMethodGenericType
{
	public MsilMethodGenericTypeImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilMethodGenericTypeImpl(@Nonnull MsilMethodGenericTypeStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef toTypeRefImpl()
	{
		MsilMethodEntry parent = getStubOrPsiParentOfType(MsilMethodEntry.class);
		assert parent != null;
		return new MsilMethodGenericTypeRefImpl(parent, getIndex());
	}

	@Override
	public int getIndex()
	{
		MsilMethodGenericTypeStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getIndex();
		}
		PsiElement element = findNotNullChildByType(MsilTokens.NUMBER_LITERAL);
		return Integer.parseInt(element.getText());
	}
}
