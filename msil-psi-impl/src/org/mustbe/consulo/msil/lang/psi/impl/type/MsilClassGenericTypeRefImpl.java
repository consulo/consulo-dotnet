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
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRefWithCachedResult;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;
import org.mustbe.consulo.dotnet.resolve.SimpleTypeResolveResult;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import com.intellij.openapi.util.Comparing;
import com.intellij.util.ArrayUtil;

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
		myParent = parent;
		myName = name;
	}

	@RequiredReadAction
	@NotNull
	@Override
	public String toString()
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
	@NotNull
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
