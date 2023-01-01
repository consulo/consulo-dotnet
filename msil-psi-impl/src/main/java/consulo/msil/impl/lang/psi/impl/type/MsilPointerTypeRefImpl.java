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

package consulo.msil.impl.lang.psi.impl.type;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.resolve.DotNetPointerTypeRef;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.dotnet.psi.resolve.DotNetTypeRefWithCachedResult;
import consulo.dotnet.psi.resolve.DotNetTypeResolveResult;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public class MsilPointerTypeRefImpl extends DotNetTypeRefWithCachedResult implements DotNetPointerTypeRef
{
	private final DotNetTypeRef myInnerTypeRef;

	public MsilPointerTypeRefImpl(DotNetTypeRef innerTypeRef)
	{
		super(innerTypeRef.getProject(), innerTypeRef.getResolveScope());
		myInnerTypeRef = innerTypeRef;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	protected DotNetTypeResolveResult resolveResult()
	{
		return myInnerTypeRef.resolve();
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public String getVmQName()
	{
		return myInnerTypeRef.toString() + "*";
	}

	@Override
	@Nonnull
	public DotNetTypeRef getInnerTypeRef()
	{
		return myInnerTypeRef;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MsilPointerTypeRefImpl && myInnerTypeRef.equals(((MsilPointerTypeRefImpl) obj).getInnerTypeRef());
	}
}
