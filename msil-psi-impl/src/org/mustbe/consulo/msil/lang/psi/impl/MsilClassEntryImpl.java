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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetConstructorDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetTypeList;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilClassEntryStub;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.Processor;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilClassEntryImpl extends MsilStubElementImpl<MsilClassEntryStub> implements MsilClassEntry
{
	public MsilClassEntryImpl(@NotNull MsilClassEntryStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	public MsilClassEntryImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@NotNull
	public String getNameFromBytecode()
	{
		PsiElement element = getNameIdentifier();
		return element == null ? "" : element.getText();
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitClassEntry(this);
	}

	@Override
	public boolean isInterface()
	{
		return false;
	}

	@Override
	public boolean isStruct()
	{
		return false;
	}

	@Override
	public boolean isEnum()
	{
		return false;
	}

	@Override
	public boolean isInheritAllowed()
	{
		return false;
	}

	@Nullable
	@Override
	public DotNetTypeList getExtendList()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetTypeRef[] getExtendTypeRefs()
	{
		return new DotNetTypeRef[0];
	}

	@Override
	public boolean isInheritor(@NotNull DotNetTypeDeclaration other, boolean deep)
	{
		return false;
	}

	@Override
	public void processConstructors(@NotNull Processor<DotNetConstructorDeclaration> processor)
	{

	}

	@Nullable
	@Override
	public DotNetGenericParameterList getGenericParameterList()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetGenericParameter[] getGenericParameters()
	{
		return new DotNetGenericParameter[0];
	}

	@Override
	public int getGenericParametersCount()
	{
		return 0;
	}

	@NotNull
	@Override
	public DotNetNamedElement[] getMembers()
	{
		return new DotNetNamedElement[0];
	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		return getModifierList().hasModifier(modifier);
	}

	@NotNull
	@Override
	public DotNetModifierList getModifierList()
	{
		return getRequiredStubOrPsiChild(MsilStubElements.MODIFIER_LIST);
	}

	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		MsilClassEntryStub stub = getStub();
		if(stub != null)
		{
			return stub.getNamespace();
		}
		return StringUtil.getPackageName(getNameFromBytecode());
	}

	@Nullable
	@Override
	public String getPresentableQName()
	{
		return getNameFromBytecode();
	}

	@Override
	public String getName()
	{
		MsilClassEntryStub stub = getStub();
		if(stub != null)
		{
			return stub.getName();
		}
		return StringUtil.getShortName(getNameFromBytecode());
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return findChildByType(MsilTokens.IDENTIFIER);
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}
}
