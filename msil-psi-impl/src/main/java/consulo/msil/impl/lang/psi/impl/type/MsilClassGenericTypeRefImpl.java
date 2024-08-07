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
import consulo.msil.lang.psi.MsilClassEntry;
import consulo.util.collection.ArrayUtil;
import consulo.util.lang.Comparing;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilClassGenericTypeRefImpl extends DotNetTypeRefWithCachedResult
{
	private final MsilClassEntry myParent;
	private final String myName;

	public MsilClassGenericTypeRefImpl(MsilClassEntry parent, String name)
	{
		super(parent.getProject(), parent.getResolveScope());
		myParent = parent;
		myName = name;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public String getVmQName()
	{
		return myName;
	}

	public MsilClassEntry getParent()
	{
		return myParent;
	}

	@RequiredReadAction
	public int getIndex()
	{
		DotNetGenericParameter genericParameter = findGenericParameter();
		if(genericParameter != null)
		{
			return genericParameter.getIndex();
		}
		return -1;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public DotNetTypeResolveResult resolveResult()
	{
		DotNetGenericParameter parameter = findGenericParameter();
		if(parameter != null)
		{
			return new SimpleTypeResolveResult(parameter);
		}
		return DotNetTypeResolveResult.EMPTY;
	}

	@Nullable
	@RequiredReadAction
	private DotNetGenericParameter findGenericParameter()
	{
		DotNetGenericParameter[] genericParameters = myParent.getGenericParameters();
		// we need reverse it, due we can have parameters like <T, T> in nested class entry
		genericParameters = ArrayUtil.reverseArray(genericParameters);

		for(DotNetGenericParameter parameter : genericParameters)
		{
			if(Comparing.equal(myName, parameter.getName()))
			{
				return parameter;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MsilClassGenericTypeRefImpl && myParent.isEquivalentTo(((MsilClassGenericTypeRefImpl) obj).myParent) && myName.equals(((MsilClassGenericTypeRefImpl) obj).myName);
	}
}
