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
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterListOwner;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeResolveResult;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public class MsilTypeResolveResult implements DotNetTypeResolveResult
{
	private final PsiElement myElement;
	private DotNetTypeRef[] myArgumentTypeRefs;
	private NotNullLazyValue<DotNetGenericExtractor> myExtractorValue = new NotNullLazyValue<DotNetGenericExtractor>()
	{
		@NotNull
		@Override
		protected DotNetGenericExtractor compute()
		{
			if(myArgumentTypeRefs.length == 0)
			{
				return DotNetGenericExtractor.EMPTY;
			}
			if(myElement instanceof DotNetGenericParameterListOwner)
			{
				DotNetGenericParameter[] genericParameters = ((DotNetGenericParameterListOwner) myElement).getGenericParameters();

				return new MsilGenericExtractorImpl(genericParameters, myArgumentTypeRefs);
			}
			return DotNetGenericExtractor.EMPTY;
		}
	};

	public MsilTypeResolveResult(PsiElement element, DotNetTypeRef[] argumentTypeRefs)
	{
		myElement = element;
		myArgumentTypeRefs = argumentTypeRefs;
	}

	@Nullable
	@Override
	public PsiElement getElement()
	{
		return myElement;
	}

	@NotNull
	@Override
	public DotNetGenericExtractor getGenericExtractor()
	{
		return myExtractorValue.getValue();
	}

	@Override
	public boolean isNullable()
	{
		throw new UnsupportedOperationException("We can't calculate it");
	}
}
