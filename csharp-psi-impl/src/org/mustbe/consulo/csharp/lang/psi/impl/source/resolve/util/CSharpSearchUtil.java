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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpModifier;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetPropertyDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.scope.PsiScopeProcessor;

/**
 * @author VISTALL
 * @since 18.05.14
 */
public class CSharpSearchUtil
{
	@Nullable
	public static DotNetPropertyDeclaration findPropertyByName(@NotNull final String name, @NotNull DotNetTypeRef typeRef, @NotNull PsiElement scope)
	{
		PsiElement resolve = typeRef.resolve(scope);
		if(resolve == null)
		{
			return null;
		}
		DotNetGenericExtractor genericExtractor = typeRef.getGenericExtractor(resolve, scope);
		return findPropertyByName(name, resolve, genericExtractor);
	}

	@Nullable
	public static DotNetPropertyDeclaration findPropertyByName(@NotNull final String name, @NotNull PsiElement owner,
			@NotNull DotNetGenericExtractor extractor)
	{
		final Ref<DotNetPropertyDeclaration> ref = Ref.create();
		PsiScopeProcessor psiScopeProcessor = new BaseScopeProcessor()
		{
			@Override
			public boolean execute(@NotNull PsiElement element, ResolveState state)
			{

				if(element instanceof DotNetPropertyDeclaration && Comparing.equal(((DotNetPropertyDeclaration) element).getName(), name))
				{
					//FIXME [VISTALL]  stupy hack until override ill supported
					if(((DotNetPropertyDeclaration) element).hasModifier(CSharpModifier.PRIVATE))
					{
						return true;
					}

					ref.set((DotNetPropertyDeclaration) element);
					return false;
				}
				return true;
			}
		};

		ResolveState state = ResolveState.initial();
		state = state.put(CSharpResolveUtil.EXTRACTOR_KEY, extractor);

		CSharpResolveUtil.walkChildren(psiScopeProcessor, owner, false, null, state);
		return ref.get();
	}

	@Nullable
	public static DotNetMethodDeclaration findMethodByName(@NotNull final String name, @NotNull DotNetTypeRef typeRef, @NotNull PsiElement scope)
	{
		PsiElement resolve = typeRef.resolve(scope);
		if(resolve == null)
		{
			return null;
		}
		DotNetGenericExtractor genericExtractor = typeRef.getGenericExtractor(resolve, scope);
		return findMethodByName(name, resolve, genericExtractor);
	}

	@Nullable
	public static DotNetMethodDeclaration findMethodByName(@NotNull final String name, @NotNull PsiElement owner,
			@NotNull DotNetGenericExtractor extractor)
	{
		final Ref<DotNetMethodDeclaration> ref = Ref.create();
		PsiScopeProcessor psiScopeProcessor = new BaseScopeProcessor()
		{
			@Override
			public boolean execute(@NotNull PsiElement element, ResolveState state)
			{
				if(element instanceof DotNetMethodDeclaration && Comparing.equal(((DotNetMethodDeclaration) element).getName(), name))
				{
					//FIXME [VISTALL]  stupy hack until override ill supported
					if(((DotNetMethodDeclaration) element).hasModifier(CSharpModifier.PRIVATE))
					{
						return true;
					}

					DotNetParameter[] parameters = ((DotNetMethodDeclaration) element).getParameters();
					if(parameters.length == 0) //TODO [VISTALL] parameter handling
					{
						ref.set((DotNetMethodDeclaration) element);
						return false;
					}
				}
				return true;
			}
		};

		ResolveState state = ResolveState.initial();
		state = state.put(CSharpResolveUtil.EXTRACTOR_KEY, extractor);

		CSharpResolveUtil.walkChildren(psiScopeProcessor, owner, false, null, state);
		return ref.get();
	}
}
