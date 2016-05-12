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
import org.mustbe.consulo.RequiredReadAction;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
@ArrayFactoryFields
public interface DotNetTypeRef
{
	@Deprecated
	public class Adapter implements DotNetTypeRef
	{
		@NotNull
		@Override
		@Deprecated
		public String getPresentableText()
		{
			throw new UnsupportedOperationException();
		}

		@NotNull
		@Override
		@Deprecated
		public String getQualifiedText()
		{
			return getPresentableText();
		}

		@RequiredReadAction
		@NotNull
		@Override
		public DotNetTypeResolveResult resolve()
		{
			return DotNetTypeResolveResult.EMPTY;
		}

		@RequiredReadAction
		@NotNull
		@Deprecated
		@Override
		public DotNetTypeResolveResult resolve(@Nullable("always null") @Deprecated PsiElement scope)
		{
			return resolve();
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
		@Deprecated
		public String getPresentableText()
		{
			return myDelegate.getPresentableText();
		}

		@NotNull
		@Override
		@Deprecated
		public String getQualifiedText()
		{
			return myDelegate.getQualifiedText();
		}

		@RequiredReadAction
		@NotNull
		@Override
		public DotNetTypeResolveResult resolve()
		{
			return myDelegate.resolve();
		}

		@RequiredReadAction
		@NotNull
		@Override
		public DotNetTypeResolveResult resolve(@Nullable("always null") @Deprecated PsiElement scope)
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
		@Deprecated
		public String getPresentableText()
		{
			return "<error>";
		}
	};

	DotNetTypeRef UNKNOWN_TYPE = new Adapter()
	{
		@NotNull
		@Override
		@Deprecated
		public String getPresentableText()
		{
			return "<unknown>";
		}
	};

	DotNetTypeRef AUTO_TYPE = new Adapter()
	{
		@NotNull
		@Override
		@Deprecated
		public String getPresentableText()
		{
			return "var";
		}
	};

	@NotNull
	@Deprecated
	String getPresentableText();

	@NotNull
	@Deprecated
	String getQualifiedText();

	@RequiredReadAction
	@NotNull
	@Deprecated
	DotNetTypeResolveResult resolve(@Nullable("always null") @Deprecated PsiElement scope);

	@RequiredReadAction
	@NotNull
	DotNetTypeResolveResult resolve();
}
