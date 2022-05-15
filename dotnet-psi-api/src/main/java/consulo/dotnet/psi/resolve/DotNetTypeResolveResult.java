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
public interface DotNetTypeResolveResult
{
	DotNetTypeResolveResult EMPTY = new DotNetTypeResolveResult()
	{
		@Nullable
		@Override
		public PsiElement getElement()
		{
			return null;
		}
	};

	@Nullable
	PsiElement getElement();

	@Nonnull
	default DotNetGenericExtractor getGenericExtractor()
	{
		return DotNetGenericExtractor.EMPTY;
	}

	/**
	 * Return true if it's reference type, or struct wrapper (System.Nullable)
	 */
	default boolean isNullable()
	{
		return true;
	}

	/**
	 * Return true if reference type declate as nullable
	 */
	default boolean isExplicitNullable()
	{
		return false;
	}
}
