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

package consulo.dotnet.psi.resolve;

import consulo.language.psi.PsiElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 20.10.14
 */
public class SimpleTypeResolveResult implements DotNetTypeResolveResult
{
	private PsiElement myElement;
	private DotNetGenericExtractor myExtractor;
	private boolean myNullable;

	public SimpleTypeResolveResult(@Nullable PsiElement element)
	{
		this(element, DotNetGenericExtractor.EMPTY);
	}

	public SimpleTypeResolveResult(@Nullable PsiElement element, @Nonnull DotNetGenericExtractor extractor)
	{
		myElement = element;
		myExtractor = extractor;
	}

	public SimpleTypeResolveResult(@Nullable PsiElement element, boolean nullable)
	{
		this(element, DotNetGenericExtractor.EMPTY, nullable);
	}

	public SimpleTypeResolveResult(@Nullable PsiElement element, @Nonnull DotNetGenericExtractor extractor, boolean nullable)
	{
		myElement = element;
		myExtractor = extractor;
		myNullable= nullable;
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
		return myExtractor;
	}

	@Override
	public boolean isNullable()
	{
		return myNullable;
	}
}
