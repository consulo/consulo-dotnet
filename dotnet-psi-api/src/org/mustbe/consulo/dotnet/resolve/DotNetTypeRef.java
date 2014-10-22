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
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
@ArrayFactoryFields
public interface DotNetTypeRef
{
	public class Adapter implements DotNetTypeRef
	{
		@NotNull
		@Override
		public String getPresentableText()
		{
			return null;
		}

		@NotNull
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

		@NotNull
		@Override
		public DotNetTypeResolveResult resolve(@NotNull PsiElement scope)
		{
			return DotNetTypeResolveResult.EMPTY;
		}

		@Override
		public String toString()
		{
			return getPresentableText();
		}
	}

	public class Delegate implements DotNetTypeRef
	{
		private final DotNetTypeRef myDelegate;

		public Delegate(DotNetTypeRef delegate)
		{
			myDelegate = delegate;
		}

		@NotNull
		@Override
		public String getPresentableText()
		{
			return myDelegate.getPresentableText();
		}

		@NotNull
		@Override
		public String getQualifiedText()
		{
			return myDelegate.getQualifiedText();
		}

		@Override
		public boolean isNullable()
		{
			return myDelegate.isNullable();
		}

		@NotNull
		@Override
		public DotNetTypeResolveResult resolve(@NotNull PsiElement scope)
		{
			return myDelegate.resolve(scope);
		}

		@NotNull
		public DotNetTypeRef getDelegate()
		{
			return myDelegate;
		}

		@Override
		public String toString()
		{
			return myDelegate.toString();
		}
	}

	DotNetTypeRef ERROR_TYPE = new Adapter()
	{
		@NotNull
		@Override
		public String getPresentableText()
		{
			return "<error>";
		}
	};

	DotNetTypeRef UNKNOWN_TYPE = new Adapter()
	{
		@NotNull
		@Override
		public String getPresentableText()
		{
			return "<unknown>";
		}
	};

	DotNetTypeRef AUTO_TYPE = new Adapter()
	{
		@NotNull
		@Override
		public String getPresentableText()
		{
			return "var";
		}
	};

	@NotNull
	String getPresentableText();

	@NotNull
	String getQualifiedText();

	boolean isNullable();

	@NotNull
	DotNetTypeResolveResult resolve(@NotNull PsiElement scope);
}
