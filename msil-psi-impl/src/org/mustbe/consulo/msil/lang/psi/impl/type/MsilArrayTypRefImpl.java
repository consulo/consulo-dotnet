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
import consulo.dotnet.resolve.DotNetArrayTypeRef;
import consulo.dotnet.resolve.DotNetTypeRef;
import consulo.dotnet.resolve.DotNetTypeRefWithCachedResult;
import consulo.dotnet.resolve.DotNetTypeResolveResult;
import com.intellij.openapi.util.Comparing;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilArrayTypRefImpl extends DotNetTypeRefWithCachedResult implements DotNetArrayTypeRef
{
	private final DotNetTypeRef myInnerTypeRef;
	private final int[] myLowerValues;

	public MsilArrayTypRefImpl(DotNetTypeRef innerTypeRef, int[] lowerValues)
	{
		myInnerTypeRef = innerTypeRef;
		myLowerValues = lowerValues;
	}

	@RequiredReadAction
	@NotNull
	@Override
	public String toString()
	{
		return myInnerTypeRef.toString() + "[]";
	}

	@RequiredReadAction
	@NotNull
	@Override
	public DotNetTypeResolveResult resolveResult()
	{
		return DotNetTypeResolveResult.EMPTY;
	}

	@NotNull
	@Override
	public DotNetTypeRef getInnerTypeRef()
	{
		return myInnerTypeRef;
	}

	public int[] getLowerValues()
	{
		return myLowerValues;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MsilArrayTypRefImpl && myInnerTypeRef.equals(((MsilArrayTypRefImpl) obj).getInnerTypeRef()) && Comparing.equal
				(myLowerValues, ((MsilArrayTypRefImpl) obj).myLowerValues);
	}
}
