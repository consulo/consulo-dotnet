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

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceAsElement;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceHelper;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.wrapper.GenericUnwrapTool;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.KeyWithDefaultValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;

/**
 * @author VISTALL
 * @since 17.12.13.
 */
@Logger
public class CSharpResolveUtil
{
	public static final KeyWithDefaultValue<DotNetGenericExtractor> EXTRACTOR_KEY = new KeyWithDefaultValue<DotNetGenericExtractor>
			("dot-net-extractor")
	{
		@Override
		public DotNetGenericExtractor getDefaultValue()
		{
			return DotNetGenericExtractor.EMPTY;
		}
	};
	public static final Key<PsiFile> SELF_FILE = Key.create("self.file");

	public static boolean treeWalkUp(@NotNull PsiScopeProcessor processor, @NotNull PsiElement entrance, @NotNull PsiElement sender,
			@Nullable PsiElement maxScope)
	{
		return treeWalkUp(processor, entrance, sender, maxScope, ResolveState.initial());
	}

	public static boolean treeWalkUp(@NotNull final PsiScopeProcessor processor, @NotNull final PsiElement entrance,
			@NotNull final PsiElement sender, @Nullable PsiElement maxScope, @NotNull final ResolveState state)
	{
		if(!entrance.isValid())
		{
			LOGGER.error(new PsiInvalidElementAccessException(entrance));
		}

		PsiElement prevParent = entrance;
		PsiElement scope = entrance;

		if(maxScope == null)
		{
			maxScope = sender.getContainingFile();
		}

		while(scope != null)
		{
			ProgressIndicatorProvider.checkCanceled();

			if(entrance != sender && scope instanceof PsiFile)
			{
				break;
			}

			if(!scope.processDeclarations(processor, state, prevParent, entrance))
			{
				return false; // resolved
			}

			if(entrance != sender)
			{
				break;
			}

			if(scope == maxScope)
			{
				break;
			}

			prevParent = scope;
			scope = prevParent.getContext();
			if(scope != null && scope != prevParent.getParent() && !scope.isValid())
			{
				break;
			}
		}

		return true;
	}

	public static boolean walkChildren(@NotNull final PsiScopeProcessor processor, @NotNull final PsiElement entrance,
			@NotNull final PsiElement sender, @Nullable PsiElement maxScope, @NotNull final ResolveState state)
	{
		if(entrance instanceof DotNetTypeDeclaration)
		{
			DotNetGenericExtractor extractor = state.get(CSharpResolveUtil.EXTRACTOR_KEY);

			DotNetTypeDeclaration typeDeclaration = (DotNetTypeDeclaration) entrance;
			for(DotNetNamedElement namedElement : typeDeclaration.getMembers())
			{
				DotNetNamedElement extracted = GenericUnwrapTool.extract(namedElement, extractor);

				if(!processor.execute(extracted, state))
				{
					return false;
				}
			}

			DotNetType[] anExtends = typeDeclaration.getExtends();
			if(anExtends.length > 0)
			{
				for(DotNetType anExtend : anExtends)
				{
					DotNetTypeRef dotNetTypeRef = anExtend.toTypeRef();

					PsiElement resolve = dotNetTypeRef.resolve(entrance);

					if(resolve != null && resolve != entrance)
					{
						DotNetGenericExtractor genericExtractor = dotNetTypeRef.getGenericExtractor(resolve, entrance);
						ResolveState newState = state.put(EXTRACTOR_KEY, genericExtractor);

						if(!walkChildren(processor, resolve, sender, maxScope, newState))
						{
							return false;
						}
					}
				}
			}
			else
			{
				DotNetTypeDeclaration type = DotNetPsiFacade.getInstance(entrance.getProject()).findType(DotNetTypes.System_Object,
						entrance.getResolveScope(), 0);
				if(type != null && type != entrance)
				{
					if(!walkChildren(processor, type, sender, maxScope, ResolveState.initial()))
					{
						return false;
					}
				}
			}
		}
		else if(entrance instanceof CSharpNamespaceAsElement)
		{
			ResolveState s = state.put(SELF_FILE, sender.getContainingFile());

			if(!entrance.processDeclarations(processor, s, maxScope, entrance))
			{
				return false;
			}

			String pQName = CSharpNamespaceHelper.getNamespaceForIndexing(((CSharpNamespaceAsElement) entrance).getPresentableParentQName());
			if(Comparing.equal(pQName, CSharpNamespaceHelper.ROOT))
			{
				return true;
			}

			CSharpNamespaceAsElement parentNamespace = new CSharpNamespaceAsElement(entrance.getProject(), pQName, entrance.getResolveScope());
			return walkChildren(processor, parentNamespace, sender, maxScope, s);
		}
		else if(entrance instanceof DotNetNamespaceDeclaration)
		{
			String presentableQName = ((DotNetNamespaceDeclaration) entrance).getPresentableQName();
			if(presentableQName == null)
			{
				return true;
			}
			CSharpNamespaceAsElement parentNamespace = new CSharpNamespaceAsElement(entrance.getProject(), presentableQName,
					entrance.getResolveScope());
			return walkChildren(processor, parentNamespace, sender, maxScope, state);
		}
		else if(entrance instanceof PsiFile)
		{
			CSharpNamespaceAsElement parentNamespace = new CSharpNamespaceAsElement(entrance.getProject(), CSharpNamespaceHelper.ROOT,
					entrance.getResolveScope());
			return walkChildren(processor, parentNamespace, sender, maxScope, state);
		}

		return true;
	}
}
