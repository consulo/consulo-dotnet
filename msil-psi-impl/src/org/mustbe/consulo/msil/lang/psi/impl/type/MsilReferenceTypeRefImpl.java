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
import consulo.annotations.RequiredReadAction;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRefWithCachedResult;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.index.MsilTypeByQNameIndex;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.CommonProcessors;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceTypeRefImpl extends DotNetTypeRefWithCachedResult
{
	private final PsiElement myElement;
	protected final String myRef;
	protected final DotNetPsiSearcher.TypeResoleKind myTypeResoleKind;

	public MsilReferenceTypeRefImpl(@NotNull PsiElement element, @NotNull String ref, @NotNull DotNetPsiSearcher.TypeResoleKind typeResoleKind)
	{
		myElement = element;
		myRef = ref;
		myTypeResoleKind = typeResoleKind;
	}

	@NotNull
	@Deprecated
	public DotNetPsiSearcher.TypeResoleKind getTypeResoleKind()
	{
		return myTypeResoleKind;
	}

	@RequiredReadAction
	@NotNull
	@Override
	public String toString()
	{
		return myRef;
	}

	@RequiredReadAction
	@NotNull
	@Override
	protected DotNetTypeResolveResult resolveResult()
	{
		Project project = myElement.getProject();
		if(DumbService.isDumb(project))
		{
			return DotNetTypeResolveResult.EMPTY;
		}

		CommonProcessors.FindFirstProcessor<MsilClassEntry> processor = new CommonProcessors.FindFirstProcessor<MsilClassEntry>();
		StubIndex.getInstance().processElements(MsilTypeByQNameIndex.getInstance().getKey(), myRef, project, myElement.getResolveScope(), MsilClassEntry.class, processor);

		MsilClassEntry foundValue = processor.getFoundValue();
		if(foundValue == null)
		{
			return DotNetTypeResolveResult.EMPTY;
		}
		return new MsilTypeResolveResult(foundValue, DotNetTypeRef.EMPTY_ARRAY);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MsilReferenceTypeRefImpl && myRef.equals(((MsilReferenceTypeRefImpl) obj).myRef) && myTypeResoleKind == ((MsilReferenceTypeRefImpl) obj).myTypeResoleKind;
	}
}
