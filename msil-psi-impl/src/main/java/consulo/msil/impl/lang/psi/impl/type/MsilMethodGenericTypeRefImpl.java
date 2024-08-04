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
import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.dotnet.psi.resolve.DotNetTypeRefWithCachedResult;
import consulo.dotnet.psi.resolve.DotNetTypeResolveResult;
import consulo.dotnet.psi.resolve.SimpleTypeResolveResult;
import consulo.dotnet.util.ArrayUtil2;
import consulo.language.psi.PsiElement;
import consulo.msil.lang.psi.MsilMethodEntry;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilMethodGenericTypeRefImpl extends DotNetTypeRefWithCachedResult
{
	private final MsilMethodEntry myParent;
	private final int myIndex;

	public MsilMethodGenericTypeRefImpl(MsilMethodEntry parent, int index)
	{
		super(parent.getProject(), parent.getResolveScope());
		myParent = parent;
		myIndex = index;
	}

	public MsilMethodEntry getParent()
	{
		return myParent;
	}

	public int getIndex()
	{
		return myIndex;
	}

	@Nonnull
	@Override
	public String getVmQName()
	{
		return "";
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public String toString()
	{
		PsiElement resolve = resolve().getElement();
		if(resolve instanceof DotNetGenericParameter)
		{
			return ((DotNetGenericParameter) resolve).getName();
		}
		return String.valueOf(myIndex);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeResolveResult resolveResult()
	{
		DotNetGenericParameter dotNetGenericParameter = ArrayUtil2.safeGet(myParent.getGenericParameters(), myIndex);
		if(dotNetGenericParameter == null)
		{
			return DotNetTypeResolveResult.EMPTY;
		}
		return new SimpleTypeResolveResult(dotNetGenericParameter);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MsilMethodGenericTypeRefImpl && myParent.isEquivalentTo(((MsilMethodGenericTypeRefImpl) obj).myParent) && myIndex == (
				(MsilMethodGenericTypeRefImpl) obj).myIndex;
	}
}
