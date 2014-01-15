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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterListOwner;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 04.01.14.
 */
public class CSharpGenericWrapperRuntimeType extends DotNetRuntimeType.Adapter
{
	private final DotNetRuntimeType myInner;
	private final DotNetRuntimeType[] myArguments;

	public CSharpGenericWrapperRuntimeType(DotNetRuntimeType inner, DotNetRuntimeType[] rArguments)
	{
		myInner = inner;
		myArguments = rArguments;
	}

	@Nullable
	@Override
	public String getPresentableText()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(myInner.getPresentableText());
		builder.append("<");
		for(int i = 0; i < myArguments.length; i++)
		{
			if(i != 0)
			{
				builder.append(", ");
			}
			DotNetRuntimeType argument = myArguments[i];
			builder.append(argument.getPresentableText());
		}
		builder.append(">");
		return builder.toString();
	}

	@Nullable
	@Override
	public String getQualifiedText()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(myInner.getQualifiedText());
		builder.append("<");
		for(int i = 0; i < myArguments.length; i++)
		{
			if(i != 0)
			{
				builder.append(", ");
			}
			DotNetRuntimeType argument = myArguments[i];
			builder.append(argument.getQualifiedText());
		}
		builder.append(">");
		return builder.toString();
	}

	@Override
	public boolean isNullable()
	{
		return myInner.isNullable();
	}

	@Nullable
	@Override
	public PsiElement toPsiElement()
	{
		return myInner.toPsiElement();
	}

	@NotNull
	@Override
	public DotNetRuntimeGenericExtractor getGenericExtractor()
	{
		PsiElement psiElement = myInner.toPsiElement();
		if(!(psiElement instanceof DotNetGenericParameterListOwner))
		{
			return DotNetRuntimeGenericExtractor.EMPTY;
		}

		DotNetGenericParameter[] genericParameters = ((DotNetGenericParameterListOwner) psiElement).getGenericParameters();
		if(genericParameters.length != myArguments.length)
		{
			return DotNetRuntimeGenericExtractor.EMPTY;
		}
		return new CSharpRuntimeGenericExtractor(genericParameters, myArguments);
	}
}
