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

package org.mustbe.consulo.msil.lang.psi.impl.type;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.resolve.DotNetPointerTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRefWithCachedResult;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public class MsilPointerTypeRefImpl extends DotNetTypeRefWithCachedResult implements DotNetPointerTypeRef
{
	private final DotNetTypeRef myInnerTypeRef;

	public MsilPointerTypeRefImpl(DotNetTypeRef innerTypeRef)
	{
		myInnerTypeRef = innerTypeRef;
	}

	@RequiredReadAction
	@NotNull
	@Override
	protected DotNetTypeResolveResult resolveResult()
	{
		return myInnerTypeRef.resolve();
	}

	@RequiredReadAction
	@NotNull
	@Override
	public String toString()
	{
		return myInnerTypeRef.toString() + "*";
	}

	@Override
	@NotNull
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