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

package consulo.msil.lang.psi.impl.type;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.dotnet.resolve.DotNetTypeRefWithCachedResult;
import consulo.dotnet.resolve.DotNetTypeResolveResult;
import consulo.msil.lang.psi.MsilClassEntry;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceTypeRefImpl extends DotNetTypeRefWithCachedResult
{
	private final PsiElement myElement;
	protected final String myRef;

	public MsilReferenceTypeRefImpl(@Nonnull PsiElement element, @Nonnull String ref)
	{
		super(element.getProject());
		myElement = element;
		myRef = ref;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public String toString()
	{
		return myRef;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	protected DotNetTypeResolveResult resolveResult()
	{
		Project project = myElement.getProject();
		if(DumbService.isDumb(project))
		{
			return DotNetTypeResolveResult.EMPTY;
		}

		DotNetTypeDeclaration[] types = DotNetPsiSearcher.getInstance(project).findTypes(myRef, myElement.getResolveScope());
		for(DotNetTypeDeclaration type : types)
		{
			if(type instanceof MsilClassEntry)
			{
				return new MsilTypeResolveResult(type, DotNetTypeRef.EMPTY_ARRAY);
			}
		}

		return DotNetTypeResolveResult.EMPTY;
	}

	@Override
	public boolean isEqualToVmQName(@Nonnull String vmQName)
	{
		return vmQName.equals(myRef);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MsilReferenceTypeRefImpl && myRef.equals(((MsilReferenceTypeRefImpl) obj).myRef);
	}
}
