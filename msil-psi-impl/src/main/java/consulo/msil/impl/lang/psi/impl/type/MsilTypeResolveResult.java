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

package consulo.msil.impl.lang.psi.impl.type;

import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.dotnet.psi.DotNetGenericParameterListOwner;
import consulo.dotnet.psi.resolve.DotNetGenericExtractor;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.dotnet.psi.resolve.DotNetTypeResolveResult;
import consulo.language.psi.PsiElement;
import consulo.util.lang.lazy.LazyValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public class MsilTypeResolveResult implements DotNetTypeResolveResult
{
	private final PsiElement myElement;
	private final DotNetTypeRef[] myArgumentTypeRefs;
	private final LazyValue<DotNetGenericExtractor> myExtractorValue;

	public MsilTypeResolveResult(PsiElement element, DotNetTypeRef[] argumentTypeRefs)
	{
		myElement = element;
		myArgumentTypeRefs = argumentTypeRefs;

		myExtractorValue = LazyValue.notNull(() ->
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
		});
	}

	@Nullable
	@Override
	public PsiElement getElement()
	{
		return myElement;
	}

	@Nonnull
	@Override
	public DotNetGenericExtractor getGenericExtractor()
	{
		return myExtractorValue.get();
	}
}
