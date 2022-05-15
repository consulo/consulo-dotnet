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

import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import consulo.util.collection.ArrayFactory;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public interface DotNetTypeRef
{
	public static final DotNetTypeRef[] EMPTY_ARRAY = new DotNetTypeRef[0];

	public static ArrayFactory<DotNetTypeRef> ARRAY_FACTORY = ArrayFactory.of(DotNetTypeRef[]::new);

	public class AdapterInternal implements DotNetTypeRef
	{
		private String myText;

		public AdapterInternal(String text)
		{
			myText = text;
		}

		@Nonnull
		@Override
		public Project getProject()
		{
			throw new UnsupportedOperationException(myText);
		}

		@Nonnull
		@Override
		public GlobalSearchScope getResolveScope()
		{
			throw new UnsupportedOperationException(myText);
		}

		@Nonnull
		@Override
		public String getVmQName()
		{
			return "__" + myText;
		}

		@RequiredReadAction
		@Nonnull
		@Override
		public DotNetTypeResolveResult resolve()
		{
			return DotNetTypeResolveResult.EMPTY;
		}

		@Override
		public String toString()
		{
			return myText;
		}
	}

	public class Delegate implements DotNetTypeRef
	{
		private final DotNetTypeRef myDelegate;

		public Delegate(DotNetTypeRef delegate)
		{
			myDelegate = delegate;
		}

		@Nonnull
		@Override
		public GlobalSearchScope getResolveScope()
		{
			return myDelegate.getResolveScope();
		}

		@Nonnull
		@Override
		@Deprecated
		public String getVmQName()
		{
			return myDelegate.getVmQName();
		}

		@RequiredReadAction
		@Nonnull
		@Override
		public DotNetTypeResolveResult resolve()
		{
			return myDelegate.resolve();
		}

		@Nonnull
		@Override
		public Project getProject()
		{
			return myDelegate.getProject();
		}

		@Nonnull
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

	DotNetTypeRef ERROR_TYPE = new AdapterInternal("<error>");

	DotNetTypeRef UNKNOWN_TYPE = new AdapterInternal("<unknown>");

	DotNetTypeRef AUTO_TYPE = new AdapterInternal("var");

	@Nonnull
	Project getProject();

	@Nonnull
	GlobalSearchScope getResolveScope();

	@Nonnull
	String getVmQName();

	/**
	 * @return true if type ref equal to `vmQName`. It's optimize version. If this method failed - need call #resolve()
	 */
	default boolean isEqualToVmQName(@Nonnull String vmQName)
	{
		return false;
	}

	@RequiredReadAction
	@Nonnull
	DotNetTypeResolveResult resolve();
}
