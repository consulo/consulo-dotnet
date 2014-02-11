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

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.psi.AccessDirection;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyQualifiedExpression;
import com.jetbrains.python.psi.resolve.PyResolveContext;
import com.jetbrains.python.psi.resolve.RatedResolveResult;
import com.jetbrains.python.psi.types.PyCallableParameter;
import com.jetbrains.python.psi.types.PyCallableType;
import com.jetbrains.python.psi.types.PyType;
import com.jetbrains.python.psi.types.TypeEvalContext;

/**
 * @author yole
 * @author VISTALL
 */
public class PyDotNetMethodType implements PyCallableType
{
	private final DotNetLikeMethodDeclaration myMethod;

	public PyDotNetMethodType(DotNetLikeMethodDeclaration method)
	{
		myMethod = method;
	}

	@Override
	public boolean isCallable()
	{
		return true;
	}

	@Nullable
	@Override
	public PyType getCallType(@NotNull TypeEvalContext context, @Nullable PyQualifiedExpression callSite)
	{
		return PyDotNetTypeProvider.asPyType(myMethod.getReturnTypeRef(), callSite);
	}

	@Nullable
	@Override
	public List<PyCallableParameter> getParameters(@NotNull TypeEvalContext context)
	{
		return null;
	}

	@Nullable
	@Override
	public List<? extends RatedResolveResult> resolveMember(@NotNull String name, @Nullable PyExpression location,
			@NotNull AccessDirection direction, @NotNull PyResolveContext resolveContext)
	{
		return Collections.emptyList();
	}

	@Override
	public Object[] getCompletionVariants(String completionPrefix, PsiElement location, ProcessingContext context)
	{
		return ArrayUtil.EMPTY_OBJECT_ARRAY;
	}

	@Nullable
	@Override
	public String getName()
	{
		return myMethod.getPresentableQName();
	}

	@Override
	public boolean isBuiltin(TypeEvalContext context)
	{
		return false;
	}

	@Override
	public void assertValid(String message)
	{
	}
}
