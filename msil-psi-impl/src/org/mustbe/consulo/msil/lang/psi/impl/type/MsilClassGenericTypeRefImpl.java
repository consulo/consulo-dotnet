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
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;
import org.mustbe.consulo.dotnet.resolve.SimpleTypeResolveResult;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import com.intellij.openapi.util.Comparing;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilClassGenericTypeRefImpl extends DotNetTypeRef.Adapter
{
	private final MsilClassEntry myParent;
	private final String myName;

	public MsilClassGenericTypeRefImpl(MsilClassEntry parent, String name)
	{
		myParent = parent;
		myName = name;
	}

	@NotNull
	@Override
	public String getPresentableText()
	{
		return myName;
	}

	public MsilClassEntry getParent()
	{
		return myParent;
	}

	public int getIndex()
	{
		DotNetGenericParameter[] genericParameters = myParent.getGenericParameters();
		for(int i = 0; i < genericParameters.length; i++)
		{
			DotNetGenericParameter genericParameter = genericParameters[i];
			if(Comparing.equal(myName, genericParameter.getName()))
			{
				return i;
			}
		}
		return -1;
	}
	@NotNull
	@Override
	public DotNetTypeResolveResult resolve(@NotNull PsiElement scope)
	{
		for(DotNetGenericParameter parameter : myParent.getGenericParameters())
		{
			if(Comparing.equal(myName, parameter.getName()))
			{
				return new SimpleTypeResolveResult(parameter);
			}
		}
		return DotNetTypeResolveResult.EMPTY;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MsilClassGenericTypeRefImpl && myParent.isEquivalentTo(((MsilClassGenericTypeRefImpl) obj).myParent) && myName.equals(
				((MsilClassGenericTypeRefImpl) obj).myName);
	}
}
