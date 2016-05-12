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
import org.mustbe.consulo.dotnet.resolve.DotNetGenericWrapperTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRefWithCachedResult;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public class MsilTypeWithTypeArgumentsRefImpl extends DotNetTypeRefWithCachedResult implements DotNetGenericWrapperTypeRef
{
	private DotNetTypeRef myTypeRef;
	private DotNetTypeRef[] myArguments;

	public MsilTypeWithTypeArgumentsRefImpl(DotNetTypeRef typeRef, DotNetTypeRef[] arguments)
	{
		myTypeRef = typeRef;
		myArguments = arguments;
	}

	@RequiredReadAction
	@NotNull
	@Override
	protected DotNetTypeResolveResult resolveResult()
	{
		PsiElement element = myTypeRef.resolve().getElement();
		if(element == null)
		{
			return DotNetTypeResolveResult.EMPTY;
		}
		return new MsilTypeResolveResult(element, myArguments);
	}

	@RequiredReadAction
	@NotNull
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(myTypeRef.toString());
		builder.append("<");
		for(int i = 0; i < myArguments.length; i++)
		{
			if(i != 0)
			{
				builder.append(", ");
			}
			DotNetTypeRef argument = myArguments[i];
			builder.append(argument.toString());
		}
		builder.append(">");
		return builder.toString();
	}

	@NotNull
	@Override
	public DotNetTypeRef[] getArgumentTypeRefs()
	{
		return myArguments;
	}

	@NotNull
	@Override
	public DotNetTypeRef getInnerTypeRef()
	{
		return myTypeRef;
	}
}
