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

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;
import org.mustbe.consulo.dotnet.resolve.SimpleTypeResolveResult;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilTypeByQNameIndex;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceTypeRefImpl extends DotNetTypeRef.Adapter
{
	protected final String myRef;
	protected final DotNetPsiSearcher.TypeResoleKind myTypeResoleKind;

	public MsilReferenceTypeRefImpl(String ref, DotNetPsiSearcher.TypeResoleKind typeResoleKind)
	{
		myRef = ref;
		myTypeResoleKind = typeResoleKind;
	}

	@NotNull
	public DotNetPsiSearcher.TypeResoleKind getTypeResoleKind()
	{
		return myTypeResoleKind;
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

	@NotNull
	@Override
	public DotNetTypeResolveResult resolve(@NotNull PsiElement scope)
	{
		if(DumbService.isDumb(scope.getProject()))
		{
			return DotNetTypeResolveResult.EMPTY;
		}

		Collection<MsilClassEntry> entries = MsilTypeByQNameIndex.getInstance().get(myRef, scope.getProject(), scope.getResolveScope());

		if(entries.isEmpty())
		{
			return DotNetTypeResolveResult.EMPTY;
		}
		return new SimpleTypeResolveResult(ContainerUtil.getFirstItem(entries), myTypeResoleKind != DotNetPsiSearcher.TypeResoleKind.STRUCT);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MsilReferenceTypeRefImpl && myRef.equals(((MsilReferenceTypeRefImpl) obj).myRef) && myTypeResoleKind == (
				(MsilReferenceTypeRefImpl) obj).myTypeResoleKind;
	}
}
