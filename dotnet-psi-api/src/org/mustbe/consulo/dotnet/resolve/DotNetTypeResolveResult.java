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

package org.mustbe.consulo.dotnet.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;

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

		@NotNull
		@Override
		public DotNetGenericExtractor getGenericExtractor()
		{
			return DotNetGenericExtractor.EMPTY;
		}

		@Override
		public boolean isNullable()
		{
			return true;
		}
	};

	@Nullable
	PsiElement getElement();

	@NotNull
	DotNetGenericExtractor getGenericExtractor();

	boolean isNullable();
}
