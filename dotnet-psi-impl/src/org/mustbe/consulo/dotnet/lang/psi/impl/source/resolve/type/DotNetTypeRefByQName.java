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

package org.mustbe.consulo.dotnet.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.NotNullFunction;

/**
 * @author VISTALL
 * @since 13.07.14
 */
public class DotNetTypeRefByQName extends DotNetTypeRef.Adapter
{
	private final String myQualifiedName;
	private final boolean myNullable;
	private final NotNullFunction<DotNetTypeDeclaration, DotNetTypeDeclaration> myTransformer;

	public DotNetTypeRefByQName(@NotNull String qualifiedName)
	{
		this(qualifiedName, DotNetPsiSearcher.DEFAULT_TRANSFORMER);
	}

	public DotNetTypeRefByQName(@NotNull String qualifiedName, boolean nullable)
	{
		this(qualifiedName, DotNetPsiSearcher.DEFAULT_TRANSFORMER, nullable);
	}

	public DotNetTypeRefByQName(@NotNull String qualifiedName, @NotNull NotNullFunction<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer)
	{
		this(qualifiedName, transformer, true);
	}

	public DotNetTypeRefByQName(@NotNull String qualifiedName, @NotNull NotNullFunction<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer,
			boolean nullable)
	{
		myQualifiedName = qualifiedName;
		myTransformer = transformer;
		myNullable = nullable;
	}

	@Override
	public boolean isNullable()
	{
		return myNullable;
	}

	@NotNull
	@Override
	public String getPresentableText()
	{
		return StringUtil.getShortName(myQualifiedName);
	}

	@NotNull
	@Override
	public String getQualifiedText()
	{
		return myQualifiedName;
	}

	@Nullable
	@Override
	public PsiElement resolve(@NotNull PsiElement scope)
	{
		return DotNetPsiSearcher.getInstance(scope.getProject()).findType(myQualifiedName, scope.getResolveScope(),
				DotNetPsiSearcher.TypeResoleKind.UNKNOWN, myTransformer);
	}
}