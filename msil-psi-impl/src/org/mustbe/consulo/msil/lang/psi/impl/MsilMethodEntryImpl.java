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
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.lang.psi.impl.stub.MsilHelper;
import org.mustbe.consulo.msil.lang.psi.MsilCustomAttribute;
import org.mustbe.consulo.msil.lang.psi.MsilMethodEntry;
import org.mustbe.consulo.msil.lang.psi.MsilParameterAttributeList;
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.MsilStubTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilMethodEntryStub;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilMethodEntryImpl extends MsilStubElementImpl<MsilMethodEntryStub> implements MsilMethodEntry
{
	public MsilMethodEntryImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilMethodEntryImpl(@NotNull MsilMethodEntryStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitMethodEntry(this);
	}

	@NotNull
	@Override
	public DotNetType getReturnType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@NotNull
	@Override
	public DotNetTypeRef getReturnTypeRef()
	{
		return getReturnType().toTypeRef();
	}

	@Nullable
	@Override
	public PsiElement getCodeBlock()
	{
		return null;
	}

	@Nullable
	@Override
	public DotNetGenericParameterList getGenericParameterList()
	{
		return getStubOrPsiChild(MsilStubElements.GENERIC_PARAMETER_LIST);
	}

	@NotNull
	@Override
	public DotNetGenericParameter[] getGenericParameters()
	{
		DotNetGenericParameterList genericParameterList = getGenericParameterList();
		return genericParameterList == null ? DotNetGenericParameter.EMPTY_ARRAY : genericParameterList.getParameters();
	}

	@Override
	public int getGenericParametersCount()
	{
		DotNetGenericParameterList genericParameterList = getGenericParameterList();
		return genericParameterList == null ? 0 : genericParameterList.getGenericParametersCount();
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

	@NotNull
	@Override
	public DotNetTypeRef[] getParameterTypeRefs()
	{
		DotNetParameterList parameterList = getParameterList();
		return parameterList == null ? DotNetTypeRef.EMPTY_ARRAY : parameterList.getParameterTypeRefs();
	}

	@Nullable
	@Override
	public DotNetParameterList getParameterList()
	{
		return getRequiredStubOrPsiChild(MsilStubElements.PARAMETER_LIST);
	}

	@NotNull
	@Override
	public DotNetParameter[] getParameters()
	{
		DotNetParameterList parameterList = getParameterList();
		return parameterList == null ? DotNetParameter.EMPTY_ARRAY : parameterList.getParameters();
	}

	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		return StringUtil.getPackageName(getNameFromBytecode());
	}

	@Nullable
	@Override
	public String getPresentableQName()
	{
		return getNameFromBytecode();
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return findChildByType(MsilTokenSets.IDENTIFIERS_AND_CTOR);
	}

	@Override
	public String getName()
	{
		String nameFromBytecode = getNameFromBytecode();
		if(MsilHelper.CONSTRUCTOR_NAME.equals(nameFromBytecode) || MsilHelper.STATIC_CONSTRUCTOR_NAME.equals(nameFromBytecode))
		{
			return nameFromBytecode;
		}
		return StringUtil.getShortName(nameFromBytecode);
	}

	@Override
	@NotNull
	public String getNameFromBytecode()
	{
		MsilMethodEntryStub stub = getStub();
		if(stub != null)
		{
			return stub.getNameFromBytecode();
		}
		PsiElement element = getNameIdentifier();
		return element == null ? "" : StringUtil.unquoteString(element.getText());
	}

	@NotNull
	@Override
	public MsilCustomAttribute[] getAttributes()
	{
		return getStubOrPsiChildren(MsilStubElements.CUSTOM_ATTRIBUTE, MsilCustomAttribute.ARRAY_FACTORY);
	}

	@NotNull
	@Override
	public MsilCustomAttribute[] getParameterAttributes(int index)
	{
		MsilParameterAttributeList[] list = getStubOrPsiChildren(MsilStubElements.PARAMETER_ATTRIBUTE_LIST, MsilParameterAttributeList
				.ARRAY_FACTORY);
		for(MsilParameterAttributeList attributeList : list)
		{
			if(attributeList.getIndex() == index)
			{
				return attributeList.getAttributes();
			}
		}
		return MsilCustomAttribute.EMPTY_ARRAY;
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement
			place)
	{
		for(DotNetGenericParameter dotNetGenericParameter : getGenericParameters())
		{
			if(!processor.execute(dotNetGenericParameter, state))
			{
				return false;
			}
		}
		return true;
	}

	@Nullable
	@Override
	public DotNetType getTypeForImplement()
	{
		return null;
	}

	@NotNull
	@Override
	public DotNetTypeRef getTypeRefForImplement()
	{
		return DotNetTypeRef.ERROR_TYPE;
	}
}
