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

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.util.IncorrectOperationException;
import consulo.msil.lang.psi.MsilAssemblyEntry;
import consulo.msil.lang.psi.MsilCustomAttribute;
import consulo.msil.impl.lang.psi.MsilStubElements;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilAssemblyEntryStub;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilAssemblyEntryImpl extends MsilStubElementImpl<MsilAssemblyEntryStub> implements MsilAssemblyEntry
{
	public MsilAssemblyEntryImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilAssemblyEntryImpl(@Nonnull MsilAssemblyEntryStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitAssemblyEntry(this);
	}

	@Override
	public PsiElement setName(@NonNls @Nonnull String s) throws IncorrectOperationException
	{
		return null;
	}

	@Nonnull
	@Override
	public MsilCustomAttribute[] getAttributes()
	{
		return getStubOrPsiChildren(MsilStubElements.CUSTOM_ATTRIBUTE, MsilCustomAttribute.ARRAY_FACTORY);
	}
}
