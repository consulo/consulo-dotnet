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

package org.mustbe.consulo.dotnet.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterListOwner;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRefWithInnerTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;
import com.intellij.openapi.util.NullableLazyValue;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 04.01.14.
 */
public class DotNetGenericWrapperTypeRef implements DotNetTypeRef, DotNetTypeRefWithInnerTypeRef
{
	public static class Result implements DotNetTypeResolveResult
	{
		private final DotNetGenericWrapperTypeRef myWrapperTypeRef;
		private final PsiElement myScope;
		private NullableLazyValue<PsiElement> myValue = new NullableLazyValue<PsiElement>()
		{
			@Nullable
			@Override
			protected PsiElement compute()
			{
				return myWrapperTypeRef.getInnerTypeRef().resolve(myScope).getElement();
			}
		};

		public Result(DotNetGenericWrapperTypeRef dotNetGenericWrapperTypeRef, PsiElement scope)
		{
			myWrapperTypeRef = dotNetGenericWrapperTypeRef;
			myScope = scope;
		}

		@Nullable
		@Override
		public PsiElement getElement()
		{
			return myValue.getValue();
		}

		@NotNull
		@Override
		public DotNetGenericExtractor getGenericExtractor()
		{
			PsiElement resolved = getElement();
			if(!(resolved instanceof DotNetGenericParameterListOwner))
			{
				return DotNetGenericExtractor.EMPTY;
			}

			DotNetGenericParameter[] genericParameters = ((DotNetGenericParameterListOwner) resolved).getGenericParameters();
			if(genericParameters.length != myWrapperTypeRef.getArgumentTypeRefs().length)
			{
				return DotNetGenericExtractor.EMPTY;
			}
			return new SimpleGenericExtractorImpl(genericParameters, myWrapperTypeRef.getArgumentTypeRefs());
		}

		@Override
		public boolean isNullable()
		{
			return true;
		}
	}

	private final DotNetTypeRef myInnerTypeRef;
	private final DotNetTypeRef[] myArguments;

	public DotNetGenericWrapperTypeRef(DotNetTypeRef innerTypeRef, DotNetTypeRef[] rArguments)
	{
		myInnerTypeRef = innerTypeRef;
		myArguments = rArguments;
	}

	@NotNull
	@Override
	public String getPresentableText()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getInnerTypeRef().getPresentableText());
		builder.append("<");
		for(int i = 0; i < getArgumentTypeRefs().length; i++)
		{
			if(i != 0)
			{
				builder.append(", ");
			}
			DotNetTypeRef argument = getArgumentTypeRefs()[i];
			builder.append(argument.getPresentableText());
		}
		builder.append(">");
		return builder.toString();
	}

	@NotNull
	@Override
	public String getQualifiedText()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getInnerTypeRef().getQualifiedText());
		builder.append("<");
		for(int i = 0; i < getArgumentTypeRefs().length; i++)
		{
			if(i != 0)
			{
				builder.append(", ");
			}
			DotNetTypeRef argument = getArgumentTypeRefs()[i];
			builder.append(argument.getQualifiedText());
		}
		builder.append(">");
		return builder.toString();
	}

	@NotNull
	@Override
	public DotNetTypeResolveResult resolve(@NotNull PsiElement scope)
	{
		return new Result(this, scope);
	}

	@Override
	@NotNull
	public DotNetTypeRef getInnerTypeRef()
	{
		return myInnerTypeRef;
	}

	@NotNull
	public DotNetTypeRef[] getArgumentTypeRefs()
	{
		return myArguments;
	}
}
