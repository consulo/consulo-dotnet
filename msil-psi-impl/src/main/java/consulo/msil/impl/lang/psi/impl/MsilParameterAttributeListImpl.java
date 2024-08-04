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

package consulo.msil.impl.lang.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.msil.impl.lang.psi.MsilStubElements;
import consulo.msil.impl.lang.psi.MsilTokens;
import consulo.msil.lang.psi.*;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilParameterAttributeListStub;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilParameterAttributeListImpl extends MsilStubElementImpl<MsilParameterAttributeListStub> implements MsilParameterAttributeList
{
	public MsilParameterAttributeListImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilParameterAttributeListImpl(@Nonnull MsilParameterAttributeListStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitParameterAttributeList(this);
	}

	@Override
	@RequiredReadAction
	public int getIndex()
	{
		MsilParameterAttributeListStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getIndex();
		}
		PsiElement element = findChildByType(MsilTokens.NUMBER_LITERAL);
		return element == null ? -1 : Integer.parseInt(element.getText());
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public MsilCustomAttribute[] getAttributes()
	{
		return getStubOrPsiChildren(MsilStubElements.CUSTOM_ATTRIBUTE, MsilCustomAttribute.ARRAY_FACTORY);
	}

	@Nullable
	@Override
	@RequiredReadAction
	public MsilConstantValue getValue()
	{
		return getStubOrPsiChild(MsilStubElements.CONSTANT_VALUE);
	}
}
