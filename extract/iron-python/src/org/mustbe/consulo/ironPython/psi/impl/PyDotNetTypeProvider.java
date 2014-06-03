/*
 * Copyright 2000-2013 JetBrains s.r.o.
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

package org.mustbe.consulo.ironPython.psi.impl;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyParameterList;
import com.jetbrains.python.psi.impl.ParamHelper;
import com.jetbrains.python.psi.search.PySuperMethodsSearch;
import com.jetbrains.python.psi.types.PyType;
import com.jetbrains.python.psi.types.PyTypeProviderBase;
import com.jetbrains.python.psi.types.TypeEvalContext;

/**
 * @author yole
 * @author VISTALL
 */
public class PyDotNetTypeProvider extends PyTypeProviderBase
{
	@Override
	@Nullable
	public PyType getReferenceType(@NotNull final PsiElement referenceTarget, TypeEvalContext context, @Nullable PsiElement anchor)
	{
		if(referenceTarget instanceof DotNetTypeDeclaration)
		{
			return new PyDotNetClassType((DotNetTypeDeclaration) referenceTarget, true);
		}
		if(referenceTarget instanceof DotNetNamespaceAsElement)
		{
			return new PyDotNetNamespaceType((DotNetNamespaceAsElement) referenceTarget, anchor == null ? null : ModuleUtil.findModuleForPsiElement
					(anchor));
		}
		if(referenceTarget instanceof DotNetLikeMethodDeclaration)
		{
			DotNetLikeMethodDeclaration method = (DotNetLikeMethodDeclaration) referenceTarget;
			return new PyDotNetMethodType(method);
		}
		if(referenceTarget instanceof DotNetVariable)
		{
			return asPyType(((DotNetVariable) referenceTarget).toTypeRef(true), referenceTarget);
		}
		return null;
	}

	@Nullable
	public static PyType asPyType(DotNetTypeRef type, PsiElement scope)
	{
		PsiElement resolve = type.resolve(scope);
		if(resolve instanceof DotNetTypeDeclaration)
		{
			return new PyDotNetClassType((DotNetTypeDeclaration) resolve, false);
		}
		return null;
	}

	@Override
	public PyType getParameterType(@NotNull final PyNamedParameter param, @NotNull final PyFunction func, @NotNull TypeEvalContext context)
	{
		if(!(param.getParent() instanceof PyParameterList))
		{
			return null;
		}
		List<PyNamedParameter> params = ParamHelper.collectNamedParameters((PyParameterList) param.getParent());
		final int index = params.indexOf(param);
		if(index < 0)
		{
			return null;
		}
		final List<PyType> superMethodParameterTypes = new ArrayList<PyType>();
		PySuperMethodsSearch.search(func).forEach(new Processor<PsiElement>()
		{
			@Override
			public boolean process(final PsiElement psiElement)
			{
				if(psiElement instanceof DotNetLikeMethodDeclaration)
				{
					final DotNetLikeMethodDeclaration method = (DotNetLikeMethodDeclaration) psiElement;
					final DotNetParameter[] psiParameters = method.getParameterList().getParameters();
					int javaIndex = method.hasModifier(DotNetModifier.STATIC) ? index : index - 1; // adjust for 'self' parameter
					if(javaIndex < psiParameters.length)
					{
						DotNetTypeRef paramType = psiParameters[javaIndex].toTypeRef(true);
						PsiElement resolve = paramType.resolve(psiElement);
						if(resolve instanceof DotNetTypeDeclaration)
						{
							superMethodParameterTypes.add(new PyDotNetClassType((DotNetTypeDeclaration) resolve, false));
						}
					}
				}
				return true;
			}
		});
		if(superMethodParameterTypes.size() > 0)
		{
			return superMethodParameterTypes.get(0);
		}
		return null;
	}
}
