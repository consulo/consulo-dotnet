/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import consulo.annotation.access.RequiredReadAction;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public abstract class DotNetTypeRefWithCachedResult implements DotNetTypeRef
{
	protected final Project myProject;
	protected final GlobalSearchScope myResolveScope;

	private volatile DotNetTypeResolveResult myResult;

	protected DotNetTypeRefWithCachedResult(Project project, GlobalSearchScope resolveScope)
	{
		myProject = project;
		myResolveScope = resolveScope;
	}

	@Nonnull
	@Override
	public Project getProject()
	{
		return myProject;
	}

	@Nonnull
	@Override
	public GlobalSearchScope getResolveScope()
	{
		return myResolveScope;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public final DotNetTypeResolveResult resolve()
	{
		DotNetTypeResolveResult thisResult = myResult;

		if(thisResult == null)
		{
			DotNetTypeResolveResult result = resolveResult();
			myResult = result;
			return result;
		}
		else
		{
			return thisResult;
		}
	}

	@RequiredReadAction
	@Nonnull
	protected abstract DotNetTypeResolveResult resolveResult();

	@RequiredReadAction
	@Nonnull
	public String toString()
	{
		return getVmQName();
	}
}
