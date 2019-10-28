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

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.*;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.msil.lang.psi.*;
import consulo.msil.lang.psi.impl.elementType.stub.MsilMethodEntryStub;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilMethodEntryImpl extends MsilStubElementImpl<MsilMethodEntryStub> implements MsilMethodEntry
{
	public MsilMethodEntryImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilMethodEntryImpl(@Nonnull MsilMethodEntryStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitMethodEntry(this);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetType getReturnType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeRef getReturnTypeRef()
	{
		return getReturnType().toTypeRef();
	}

	@Nonnull
	@Override
	public DotNetCodeBodyProxy getCodeBlock()
	{
		return DotNetCodeBodyProxy.EMPTY;
	}

	@Nullable
	@Override
	public DotNetGenericParameterList getGenericParameterList()
	{
		return getStubOrPsiChild(MsilStubElements.GENERIC_PARAMETER_LIST);
	}

	@Nonnull
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

	@RequiredReadAction
	@Override
	public boolean hasModifier(@Nonnull DotNetModifier modifier)
	{
		return getModifierList().hasModifier(modifier);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetModifierList getModifierList()
	{
		return getRequiredStubOrPsiChild(MsilStubElements.MODIFIER_LIST);
	}

	@Nonnull
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

	@Nonnull
	@Override
	public DotNetParameter[] getParameters()
	{
		DotNetParameterList parameterList = getParameterList();
		return parameterList == null ? DotNetParameter.EMPTY_ARRAY : parameterList.getParameters();
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		return StringUtil.getPackageName(getNameFromBytecode());
	}

	@RequiredReadAction
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
	@Nonnull
	public String getNameFromBytecode()
	{
		MsilMethodEntryStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getNameFromBytecode();
		}
		PsiElement element = getNameIdentifier();
		return element == null ? "" : StringUtil.unquoteString(element.getText());
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public MsilCustomAttribute[] getAttributes()
	{
		return getStubOrPsiChildren(MsilStubElements.CUSTOM_ATTRIBUTE, MsilCustomAttribute.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public MsilCustomAttribute[] getParameterAttributes(int index)
	{
		MsilParameterAttributeList parameterAttributeList = findParameterAttributeList(index);
		return parameterAttributeList == null ? MsilCustomAttribute.EMPTY_ARRAY : parameterAttributeList.getAttributes();
	}

	@RequiredReadAction
	@Nullable
	@Override
	public MsilConstantValue getConstantValue(int index)
	{
		MsilParameterAttributeList parameterAttributeList = findParameterAttributeList(index);
		return parameterAttributeList == null ? null : parameterAttributeList.getValue();
	}

	@Nullable
	@RequiredReadAction
	private MsilParameterAttributeList findParameterAttributeList(int index)
	{
		index ++;  // index is zero based, but in file it started with one

		MsilParameterAttributeList[] list = getStubOrPsiChildren(MsilStubElements.PARAMETER_ATTRIBUTE_LIST, MsilParameterAttributeList.ARRAY_FACTORY);
		for(MsilParameterAttributeList attributeList : list)
		{
			if(attributeList.getIndex() == index)
			{
				return attributeList;
			}
		}
		return null;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public MsilCustomAttribute[] getGenericParameterAttributes(@Nonnull String name)
	{
		MsilTypeParameterAttributeList[] list = getStubOrPsiChildren(MsilStubElements.TYPE_PARAMETER_ATTRIBUTE_LIST, MsilTypeParameterAttributeList
				.ARRAY_FACTORY);
		for(MsilTypeParameterAttributeList attributeList : list)
		{
			if(name.equals(attributeList.getGenericParameterName()))
			{
				return attributeList.getAttributes();
			}
		}
		return MsilCustomAttribute.EMPTY_ARRAY;
	}

	@Override
	public PsiElement setName(@NonNls @Nonnull String s) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement
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

	@Nonnull
	@Override
	public DotNetTypeRef getTypeRefForImplement()
	{
		return DotNetTypeRef.ERROR_TYPE;
	}
}
