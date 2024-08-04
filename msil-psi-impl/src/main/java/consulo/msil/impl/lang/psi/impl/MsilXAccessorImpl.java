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
import consulo.application.util.CachedValueProvider;
import consulo.dotnet.psi.*;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiModificationTracker;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.util.LanguageCachedValueUtil;
import consulo.language.util.IncorrectOperationException;
import consulo.msil.impl.lang.psi.MsilStubTokenSets;
import consulo.msil.impl.lang.psi.MsilTokenSets;
import consulo.msil.impl.lang.psi.MsilTokens;
import consulo.msil.lang.psi.*;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilXAccessorStub;
import consulo.util.lang.Comparing;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NonNls;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 24.05.14
 */
public class MsilXAccessorImpl extends MsilStubElementImpl<MsilXAccessorStub> implements MsilXAcessor
{
	private class CacheValueProvider implements CachedValueProvider<MsilMethodEntry>
	{
		@Nullable
		@Override
		public Result<MsilMethodEntry> compute()
		{
			DotNetType targetType = getStubOrPsiChildByIndex(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY, 1);
			if(targetType == null)
			{
				return null;
			}
			String name = getMethodName();
			if(name == null)
			{
				return null;
			}

			PsiElement element = targetType.toTypeRef().resolve().getElement();
			if(!(element instanceof MsilClassEntry))
			{
				return null;
			}

			DotNetTypeRef[] parameterTypeRefs = getParameterTypeRefs();

			MsilMethodEntry method = null;
			for(DotNetNamedElement namedElement : ((MsilClassEntry) element).getMembers())
			{
				if(namedElement instanceof MsilMethodEntry && Comparing.equal(((MsilMethodEntry) namedElement).getNameFromBytecode(), name) && Comparing.equal(((MsilMethodEntry) namedElement)
						.getParameterTypeRefs(), parameterTypeRefs))
				{
					method = (MsilMethodEntry) namedElement;
					break;
				}
			}

			if(method == null)
			{
				return null;
			}
			return Result.create(method, PsiModificationTracker.MODIFICATION_COUNT);
		}
	}

	private CacheValueProvider myCacheValueProvider = new CacheValueProvider();

	public MsilXAccessorImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	public MsilXAccessorImpl(@Nonnull MsilXAccessorStub stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitElement(this);
	}

	@Nonnull
	@Override
	public DotNetCodeBodyProxy getCodeBlock()
	{
		return DotNetCodeBodyProxy.EMPTY;
	}

	@RequiredReadAction
	@Override
	public boolean hasModifier(@Nonnull DotNetModifier modifier)
	{
		MsilMethodEntry msilMethodEntry = resolveToMethod();
		return msilMethodEntry != null && msilMethodEntry.hasModifier(modifier);
	}

	@RequiredReadAction
	@Nullable
	@Override
	public DotNetModifierList getModifierList()
	{
		MsilMethodEntry msilMethodEntry = resolveToMethod();
		return msilMethodEntry != null ? msilMethodEntry.getModifierList() : null;
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return null;
	}

	@Override
	public PsiElement setName(@NonNls @Nonnull String s) throws IncorrectOperationException
	{
		return null;
	}

	@Nullable
	@Override
	public DotNetTypeRef getReturnType()
	{
		DotNetType type = getFirstStubOrPsiChild(MsilStubTokenSets.TYPE_STUBS, DotNetType.ARRAY_FACTORY);
		if(type == null)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}
		return type.toTypeRef();
	}

	@Nullable
	@Override
	public String getMethodName()
	{
		MsilXAccessorStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getMethodName();
		}
		PsiElement childByType = findChildByType(MsilTokenSets.IDENTIFIERS);
		return childByType == null ? null : StringUtil.unquoteString(childByType.getText());
	}

	@Nonnull
	@Override
	public DotNetParameter[] getParameters()
	{
		MsilParameterList stubOrPsiChild = getStubOrPsiChild(MsilStubTokenSets.PARAMETER_LIST);
		if(stubOrPsiChild == null)
		{
			return DotNetParameter.EMPTY_ARRAY;
		}
		return stubOrPsiChild.getParameters();
	}

	@Nonnull
	@Override
	public DotNetTypeRef[] getParameterTypeRefs()
	{
		MsilParameterList stubOrPsiChild = getStubOrPsiChild(MsilStubTokenSets.PARAMETER_LIST);
		if(stubOrPsiChild == null)
		{
			return DotNetTypeRef.EMPTY_ARRAY;
		}
		return stubOrPsiChild.getParameterTypeRefs();
	}

	@Nullable
	@Override
	public MsilMethodEntry resolveToMethod()
	{
		return LanguageCachedValueUtil.getCachedValue(this, myCacheValueProvider);
	}

	@Nullable
	@Override
	public PsiElement getAccessorElement()
	{
		return findNotNullChildByFilter(MsilTokenSets.XXX_ACCESSOR_START);
	}

	@Nullable
	@Override
	public Kind getAccessorKind()
	{
		MsilXAccessorStub stub = getGreenStub();
		if(stub != null)
		{
			return stub.getAccessorType();
		}

		IElementType elementType = findNotNullChildByFilter(MsilTokenSets.XXX_ACCESSOR_START).getNode().getElementType();
		if(elementType == MsilTokens._GET_KEYWORD)
		{
			return Kind.GET;
		}
		else if(elementType == MsilTokens._SET_KEYWORD)
		{
			return Kind.SET;
		}
		else if(elementType == MsilTokens._ADDON_KEYWORD)
		{
			return Kind.ADD;
		}
		else if(elementType == MsilTokens._REMOVEON_KEYWORD)
		{
			return Kind.REMOVE;
		}
		return null;
	}
}
