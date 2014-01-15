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

import org.consulo.lombok.annotations.ArrayFactoryFields;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
@ArrayFactoryFields
public interface DotNetRuntimeType
{
	public class Adapter implements DotNetRuntimeType
	{
		@Nullable
		@Override
		public String getPresentableText()
		{
			return null;
		}

		@Nullable
		@Override
		public String getQualifiedText()
		{
			return getPresentableText();
		}

		@Override
		public boolean isNullable()
		{
			return true;
		}

		@Nullable
		@Override
		public PsiElement toPsiElement()
		{
			return null;
		}

		@NotNull
		@Override
		public DotNetRuntimeGenericExtractor getGenericExtractor()
		{
			return DotNetRuntimeGenericExtractor.EMPTY;
		}
	}

	DotNetRuntimeType ERROR_TYPE = new Adapter()
	{
		@Nullable
		@Override
		public String getPresentableText()
		{
			return "<error>";
		}
	};

	DotNetRuntimeType UNKNOWN_TYPE = new Adapter()
	{
		@Nullable
		@Override
		public String getPresentableText()
		{
			return "<unknown>";
		}
	};

	DotNetRuntimeType AUTO_TYPE = new Adapter()
	{
		@Nullable
		@Override
		public String getPresentableText()
		{
			return "var";
		}
	};

	DotNetRuntimeType NULL_TYPE = new Adapter()
	{
		@Nullable
		@Override
		public String getPresentableText()
		{
			return "null";
		}
	};

	@Nullable
	String getPresentableText();

	@Nullable
	String getQualifiedText();

	boolean isNullable();

	@Nullable
	PsiElement toPsiElement();

	@NotNull
	DotNetRuntimeGenericExtractor getGenericExtractor();
}
