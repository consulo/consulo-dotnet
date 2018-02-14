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

import javax.annotation.Nonnull;
import com.intellij.openapi.project.Project;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.resolve.DotNetRefTypeRef;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.dotnet.resolve.DotNetTypeRefWithCachedResult;
import consulo.dotnet.resolve.DotNetTypeResolveResult;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilRefTypeRefImpl extends DotNetTypeRefWithCachedResult implements DotNetRefTypeRef
{
	private final DotNetTypeRef myTypeRef;

	public MsilRefTypeRefImpl(Project project, DotNetTypeRef typeRef)
	{
		super(project);
		myTypeRef = typeRef;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	protected DotNetTypeResolveResult resolveResult()
	{
		return myTypeRef.resolve();
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public String toString()
	{
		return myTypeRef.toString() + "&";
	}

	@Nonnull
	@Override
	public DotNetTypeRef getInnerTypeRef()
	{
		return myTypeRef;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MsilRefTypeRefImpl && getInnerTypeRef().equals(((MsilRefTypeRefImpl) obj).getInnerTypeRef());
	}
}
