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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpStubElements;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpMethodStub;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetStatement;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public abstract class CSharpLikeMethodDeclarationImpl extends CSharpStubMemberImpl<CSharpMethodStub> implements DotNetLikeMethodDeclaration
{
	private CachedValue<DotNetRuntimeType> myCachedValue;

	public CSharpLikeMethodDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpLikeMethodDeclarationImpl(@NotNull CSharpMethodStub stub, @NotNull IStubElementType<? extends CSharpMethodStub, ?> nodeType)
	{
		super(stub, nodeType);
	}

	@Nullable
	@Override
	public DotNetParameterList getParameterList()
	{

		return getStubOrPsiChild(CSharpStubElements.PARAMETER_LIST);
	}

	@NotNull
	@Override
	public DotNetParameter[] getParameters()
	{
		DotNetParameterList parameterList = getParameterList();
		return parameterList == null ? DotNetParameter.EMPTY_ARRAY : parameterList.getParameters();
	}

	@NotNull
	@Override
	public DotNetRuntimeType[] getParameterTypesForRuntime()
	{
		DotNetParameterList parameterList = getParameterList();
		return parameterList == null ? DotNetRuntimeType.EMPTY_ARRAY : parameterList.getParameterTypesForRuntime();
	}

	@Nullable
	@Override
	public PsiElement getCodeBlock()
	{
		return findChildByClass(DotNetStatement.class);
	}

	@Nullable
	@Override
	public DotNetType getReturnType()
	{
		return findChildByClass(DotNetType.class);
	}

	@NotNull
	@Override
	public DotNetRuntimeType getReturnTypeForRuntime()
	{
		if(myCachedValue != null)
		{
			return myCachedValue.getValue();
		}
		myCachedValue = CachedValuesManager.getManager(getProject()).createCachedValue(new CachedValueProvider<DotNetRuntimeType>()
		{
			@Nullable
			@Override
			public Result<DotNetRuntimeType> compute()
			{
				DotNetType returnType = getReturnType();
				DotNetRuntimeType runtimeType = returnType == null ? DotNetRuntimeType.ERROR_TYPE : returnType.toRuntimeType();
				return Result.createSingleDependency(runtimeType, CSharpLikeMethodDeclarationImpl.this);
			}
		}, false);
		return myCachedValue.getValue();
	}

	@Nullable
	@Override
	public DotNetGenericParameterList getGenericParameterList()
	{
		return getStubOrPsiChild(CSharpStubElements.GENERIC_PARAMETER_LIST);
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

		for(DotNetParameter parameter : getParameters())
		{
			if(!processor.execute(parameter, state))
			{
				return false;
			}
		}

		return super.processDeclarations(processor, state, lastParent, place);
	}
}
