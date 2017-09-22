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

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiModificationTracker;
import consulo.annotations.RequiredReadAction;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public abstract class DotNetTypeRefWithCachedResult implements DotNetTypeRef
{
	private final Project myProject;

	private volatile DotNetTypeResolveResult myResult;
	private volatile long myLastComputedCount = -1;

	protected DotNetTypeRefWithCachedResult(Project project)
	{
		myProject = project;
	}

	@NotNull
	@Override
	public Project getProject()
	{
		return myProject;
	}

	@NotNull
	@Override
	public final String getPresentableText()
	{
		return toString();
	}

	@NotNull
	@Override
	public final String getQualifiedText()
	{
		return toString();
	}

	@RequiredReadAction
	@NotNull
	@Override
	public final DotNetTypeResolveResult resolve()
	{
		PsiModificationTracker modificationTracker = PsiModificationTracker.SERVICE.getInstance(myProject);

		long current = modificationTracker.getModificationCount();

		DotNetTypeResolveResult thisResult = myResult;
		if(myLastComputedCount != current)
		{
			myResult = null;
			thisResult = null;
		}

		if(thisResult == null)
		{
			DotNetTypeResolveResult result = resolveResult();
			myResult = result;
			myLastComputedCount = current;
			return result;
		}
		else
		{
			return thisResult;
		}
	}

	@RequiredReadAction
	@NotNull
	protected abstract DotNetTypeResolveResult resolveResult();

	@RequiredReadAction
	@NotNull
	public abstract String toString();
}
