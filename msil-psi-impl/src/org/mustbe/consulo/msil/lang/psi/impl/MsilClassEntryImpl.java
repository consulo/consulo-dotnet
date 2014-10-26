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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.lang.psi.impl.stub.MsilHelper;
import org.mustbe.consulo.dotnet.psi.*;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.MsilCustomAttribute;
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.MsilStubTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilClassEntryStub;
import org.mustbe.consulo.msil.lang.psi.impl.type.MsilNativeTypeRefImpl;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;

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
		return element == null ? "" : StringUtil.unquoteString(element.getText());
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitClassEntry(this);
	}

	@Override
	public boolean isInterface()
	{
		return hasModifier(MsilTokens.INTERFACE_KEYWORD);
	}

	@Override
	public boolean isStruct()
	{
		return DotNetInheritUtil.isStruct(this);
	}

	@Override
	public boolean isEnum()
	{
		return DotNetInheritUtil.isEnum(this);
	}

	@Nullable
	@Override
	public DotNetTypeList getExtendList()
	{
		return null;
	}

	@Nullable
	@Override
	public DotNetFieldDeclaration findFieldByName(@NotNull String name, boolean dep)
	{
		return DotNetTypeDeclarationUtil.findFieldByName(this, name, dep);
	}

	@NotNull
	@Override
	public DotNetTypeRef[] getExtendTypeRefs()
	{
		DotNetTypeList extendList = getStubOrPsiChild(MsilStubElements.EXTENDS_TYPE_LIST);
		DotNetTypeList implementList = getStubOrPsiChild(MsilStubElements.IMPLEMENTS_TYPE_LIST);

		DotNetTypeRef[] types = extendList == null ? DotNetTypeRef.EMPTY_ARRAY : extendList.getTypeRefs();
		DotNetTypeRef[] types1 = implementList == null ? DotNetTypeRef.EMPTY_ARRAY : implementList.getTypeRefs();
		List<DotNetTypeRef> list = new ArrayList<DotNetTypeRef>(types.length + types1.length);
		Collections.addAll(list, types);
		Collections.addAll(list, types1);
		return ContainerUtil.toArray(list, DotNetTypeRef.EMPTY_ARRAY);
	}

	@Override
	public boolean isInheritor(@NotNull DotNetTypeDeclaration other, boolean deep)
	{
		return DotNetInheritUtil.isInheritor(this, other, deep);
	}

	@Override
	public DotNetTypeRef getTypeRefForEnumConstants()
	{
		DotNetFieldDeclaration value = findFieldByName("__value", false);
		return value != null ? value.toTypeRef(false) : new MsilNativeTypeRefImpl(DotNetTypes.System.Int32,
				DotNetPsiSearcher.TypeResoleKind.UNKNOWN);
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

	@NotNull
	@Override
	public DotNetNamedElement[] getMembers()
	{
		return getStubOrPsiChildren(MsilStubTokenSets.MEMBER_STUBS, DotNetNamedElement.ARRAY_FACTORY);
	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		return getModifierList().hasModifier(modifier);
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent,
			@NotNull PsiElement place)
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
		MsilClassEntryStub stub = getStub();
		if(stub != null)
		{
			return MsilHelper.append(stub.getNamespace(), stub.getName());
		}
		return getNameFromBytecode();
	}

	@Override
	public String getVmQName()
	{
		MsilClassEntryStub stub = getStub();
		if(stub != null)
		{
			return stub.getVmQName();
		}
		if(hasModifier(MsilTokens.NESTED_KEYWORD))
		{
			PsiElement parent = getParent();
			if(parent instanceof MsilClassEntry)  //dont throw exception if tree is broken
			{
				return ((MsilClassEntry) parent).getVmQName() + "/" + getNameFromBytecode();
			}
		}
		return getNameFromBytecode();
	}

	@Nullable
	@Override
	public String getVmName()
	{
		return getName();
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
		return findChildByType(MsilTokenSets.IDENTIFIERS);
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean isNested()
	{
		PsiElement parentByStub = getParentByStub();
		return parentByStub instanceof MsilClassEntry;
	}

	@Override
	public boolean isEquivalentTo(PsiElement another)
	{
		if(another instanceof DotNetTypeDeclaration)
		{
			return Comparing.equal(getVmQName(), ((DotNetTypeDeclaration) another).getVmQName());
		}
		return super.isEquivalentTo(another);
	}

	@NotNull
	@Override
	public MsilCustomAttribute[] getAttributes()
	{
		return getStubOrPsiChildren(MsilStubElements.CUSTOM_ATTRIBUTE, MsilCustomAttribute.ARRAY_FACTORY);
	}
}
