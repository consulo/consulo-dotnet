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

package org.mustbe.consulo.msil.lang.psi.impl.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceTypeRefImpl extends DotNetTypeRef.Adapter
{
	private final String myRef;
	private final DotNetPsiSearcher.TypeResoleKind myTypeResoleKind;

	public MsilReferenceTypeRefImpl(String ref, DotNetPsiSearcher.TypeResoleKind typeResoleKind)
	{
		myRef = ref;
		myTypeResoleKind = typeResoleKind;
	}

	@NotNull
	@Override
	public String getPresentableText()
	{
		int i = myRef.lastIndexOf("/");
		if(i != -1)
		{
			return myRef.substring(i + 1, myRef.length());
		}
		return StringUtil.getShortName(myRef);
	}

	@NotNull
	@Override
	public String getQualifiedText()
	{
		return myRef;
	}

	@Nullable
	@Override
	public PsiElement resolve(@NotNull PsiElement scope)
	{
		if(DumbService.isDumb(scope.getProject()))
		{
			return null;
		}

		DotNetTypeDeclaration[] types = DotNetPsiSearcher.getInstance(scope.getProject()).findTypes(myRef, scope.getResolveScope(),
				myTypeResoleKind);

		return ArrayUtil.getFirstElement(types);
	}
}
