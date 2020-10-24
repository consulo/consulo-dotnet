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
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.NotNullFunction;
import com.intellij.util.containers.ContainerUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.access.RequiredWriteAction;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.lang.psi.impl.DotNetTypeRefCacheUtil;
import consulo.dotnet.psi.*;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.msil.lang.psi.*;
import consulo.msil.lang.psi.impl.elementType.stub.MsilClassEntryStub;
import consulo.msil.lang.psi.impl.type.MsilNativeTypeRefImpl;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilClassEntryImpl extends MsilStubElementImpl<MsilClassEntryStub> implements MsilClassEntry
{
	private static class Resolver implements NotNullFunction<MsilClassEntryImpl, DotNetTypeRef>
	{
		private static final Resolver INSTANCE = new Resolver();

		@Nonnull
		@Override
		@RequiredReadAction
		public DotNetTypeRef fun(MsilClassEntryImpl msilClassEntry)
		{
			DotNetFieldDeclaration value = findFieldByName(msilClassEntry, "__value");
			return value != null ? value.toTypeRef(false) : new MsilNativeTypeRefImpl(msilClassEntry.getProject(), msilClassEntry.getResolveScope(), DotNetTypes.System.Int32);
		}
	}

	public MsilClassEntryImpl(@Nonnull MsilClassEntryStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	public MsilClassEntryImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	@Override
	@Nonnull
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

	@RequiredReadAction
	@Override
	public boolean isInterface()
	{
		return hasModifier(MsilTokens.INTERFACE_KEYWORD);
	}

	@RequiredReadAction
	@Override
	public boolean isStruct()
	{
		return DotNetInheritUtil.isStruct(this);
	}

	@RequiredReadAction
	@Override
	public boolean isEnum()
	{
		return DotNetInheritUtil.isEnum(this);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public DotNetTypeList getExtendList()
	{
		return null;
	}

	@RequiredReadAction
	@Nonnull
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

	@RequiredReadAction
	@Override
	public boolean isInheritor(@Nonnull String otherVmQName, boolean deep)
	{
		return DotNetInheritUtil.isInheritor(this, otherVmQName, deep);
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public DotNetTypeRef getTypeRefForEnumConstants()
	{
		return DotNetTypeRefCacheUtil.cacheTypeRef(this, Resolver.INSTANCE);
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
	@Nonnull
	@Override
	public DotNetNamedElement[] getMembers()
	{
		return CachedValuesManager.getCachedValue(this, () ->
		{
			DotNetNamedElement[] stubOrPsiChildren = getStubOrPsiChildren(MsilStubTokenSets.MEMBER_STUBS, DotNetNamedElement.ARRAY_FACTORY);
			return CachedValueProvider.Result.create(stubOrPsiChildren, PsiModificationTracker.OUT_OF_CODE_BLOCK_MODIFICATION_COUNT);
		});
	}

	@RequiredReadAction
	@Override
	public boolean hasModifier(@Nonnull DotNetModifier modifier)
	{
		return getModifierList().hasModifier(modifier);
	}

	@Override
	public boolean processDeclarations(@Nonnull PsiScopeProcessor processor, @Nonnull ResolveState state, PsiElement lastParent, @Nonnull PsiElement place)
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

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetModifierList getModifierList()
	{
		return getRequiredStubOrPsiChild(MsilStubElements.MODIFIER_LIST);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		MsilClassEntryStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getNamespace();
		}
		return StringUtil.getPackageName(getNameFromBytecode());
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getPresentableQName()
	{
		MsilClassEntryStub stub = getGreenStub();
		if(stub != null)
		{
			return MsilHelper.append(stub.getNamespace(), stub.getName());
		}
		return getNameFromBytecode();
	}

	@Override
	@RequiredReadAction
	public String getVmQName()
	{
		MsilClassEntryStub stub = getGreenStub();
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
	@RequiredReadAction
	public String getVmName()
	{
		return getName();
	}

	@RequiredReadAction
	@Override
	public String getName()
	{
		MsilClassEntryStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getName();
		}
		return StringUtil.getShortName(getNameFromBytecode());
	}

	@RequiredReadAction
	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return findChildByType(MsilTokenSets.IDENTIFIERS);
	}

	@RequiredWriteAction
	@Override
	public PsiElement setName(@NonNls @Nonnull String s) throws IncorrectOperationException
	{
		return null;
	}

	@RequiredReadAction
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

	@Nonnull
	@Override
	public MsilCustomAttribute[] getAttributes()
	{
		return getStubOrPsiChildren(MsilStubElements.CUSTOM_ATTRIBUTE, MsilCustomAttribute.ARRAY_FACTORY);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public MsilCustomAttribute[] getGenericParameterAttributes(@Nonnull String name)
	{
		MsilTypeParameterAttributeList[] list = getStubOrPsiChildren(MsilStubElements.TYPE_PARAMETER_ATTRIBUTE_LIST, MsilTypeParameterAttributeList.ARRAY_FACTORY);
		for(MsilTypeParameterAttributeList attributeList : list)
		{
			if(name.equals(attributeList.getGenericParameterName()))
			{
				return attributeList.getAttributes();
			}
		}
		return MsilCustomAttribute.EMPTY_ARRAY;
	}

	@Nullable
	private static DotNetFieldDeclaration findFieldByName(@Nonnull DotNetTypeDeclaration tp, @Nonnull String name)
	{
		for(DotNetNamedElement element : tp.getMembers())
		{
			if(element instanceof DotNetFieldDeclaration && Comparing.equal(element.getName(), name))
			{
				return (DotNetFieldDeclaration) element;
			}
		}
		return null;
	}
}
