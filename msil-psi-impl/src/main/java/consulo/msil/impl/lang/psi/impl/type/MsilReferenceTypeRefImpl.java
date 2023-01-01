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

package consulo.msil.impl.lang.psi.impl.type;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.resolve.*;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.project.DumbService;
import consulo.project.Project;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceTypeRefImpl extends DotNetTypeRefWithCachedResult
{
	@Nonnull
	protected final String myRef;

	public MsilReferenceTypeRefImpl(@Nonnull Project project, @Nonnull GlobalSearchScope resolveScope, @Nonnull String ref)
	{
		super(project, resolveScope);
		myRef = ref;
	}

	@Nonnull
	@Override
	public String getVmQName()
	{
		return myRef;
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
		if(DumbService.isDumb(myProject))
		{
			return DotNetTypeResolveResult.EMPTY;
		}

		DotNetTypeDeclaration[] types = DotNetPsiSearcher.getInstance(myProject).findTypes(myRef, myResolveScope);
		for(DotNetTypeDeclaration type : types)
		{
			if(type instanceof MsilClassEntry)
			{
				return new MsilTypeResolveResult(type, DotNetTypeRef.EMPTY_ARRAY);
			}
		}

		if(types.length > 0)
		{
			return new SimpleTypeResolveResult(types[0], true);
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
