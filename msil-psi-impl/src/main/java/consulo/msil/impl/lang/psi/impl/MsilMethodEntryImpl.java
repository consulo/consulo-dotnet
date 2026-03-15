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
import consulo.dotnet.psi.*;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.util.IncorrectOperationException;
import consulo.msil.impl.lang.psi.MsilStubElements;
import consulo.msil.impl.lang.psi.MsilStubTokenSets;
import consulo.msil.impl.lang.psi.MsilTokenSets;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilMethodEntryStub;
import consulo.msil.lang.psi.*;
import consulo.util.lang.StringUtil;

import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilMethodEntryImpl extends MsilStubElementImpl<MsilMethodEntryStub> implements MsilMethodEntry
{
	public MsilMethodEntryImpl(ASTNode node)
	{
		super(node);
	}

	public MsilMethodEntryImpl(MsilMethodEntryStub stub, IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitMethodEntry(this);
	}

	@RequiredReadAction
	@Override
	public DotNetType getReturnType()
	{
		return getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@Override
	public DotNetTypeRef getReturnTypeRef()
	{
		return getReturnType().toTypeRef();
	}

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
	public boolean hasModifier(DotNetModifier modifier)
	{
		return getModifierList().hasModifier(modifier);
	}

	@RequiredReadAction
	@Override
	public DotNetModifierList getModifierList()
	{
		return getRequiredStubOrPsiChild(MsilStubElements.MODIFIER_LIST);
	}

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
	@Override
	public MsilCustomAttribute[] getAttributes()
	{
		return getStubOrPsiChildren(MsilStubElements.CUSTOM_ATTRIBUTE, MsilCustomAttribute.ARRAY_FACTORY);
	}

	@RequiredReadAction
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
	@Override
	public MsilCustomAttribute[] getGenericParameterAttributes(String name)
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
	public PsiElement setName(String s) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement
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

	@Override
	public DotNetTypeRef getTypeRefForImplement()
	{
		return DotNetTypeRef.ERROR_TYPE;
	}
}
