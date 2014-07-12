/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
package org.mustbe.consulo.dotnet.psi.search.searches;

import gnu.trove.THashSet;

import java.lang.ref.Reference;

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiBundle;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchScopeUtil;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ExtensibleQueryFactory;
import com.intellij.reference.SoftReference;
import com.intellij.util.Processor;
import com.intellij.util.Query;
import com.intellij.util.QueryExecutor;
import com.intellij.util.containers.Stack;
import lombok.val;

/**
 * @author max
 *         <p/>
 *         Copied from Java plugin by Jetbrains (com.intellij.psi.search.searches.ClassInheritorsSearch)
 */
@Logger
public class ClassInheritorsSearch extends ExtensibleQueryFactory<DotNetTypeDeclaration, ClassInheritorsSearch.SearchParameters>
{
	public static final ClassInheritorsSearch INSTANCE = new ClassInheritorsSearch();

	static
	{
		INSTANCE.registerExecutor(new QueryExecutor<DotNetTypeDeclaration, SearchParameters>()
		{
			@Override
			public boolean execute(@NotNull final SearchParameters parameters, @NotNull final Processor<DotNetTypeDeclaration> consumer)
			{
				final DotNetTypeDeclaration baseClass = parameters.getClassToProcess();
				final SearchScope searchScope = parameters.getScope();

				LOGGER.assertTrue(searchScope != null);

				ProgressIndicator progress = ProgressIndicatorProvider.getGlobalProgressIndicator();
				if(progress != null)
				{
					progress.pushState();
					String className = ApplicationManager.getApplication().runReadAction(new Computable<String>()
					{
						@Override
						public String compute()
						{
							return baseClass.getName();
						}
					});
					progress.setText(className != null ? PsiBundle.message("psi.search.inheritors.of.class.progress",
							className) : PsiBundle.message("psi.search.inheritors.progress"));
				}

				boolean result = processInheritors(consumer, baseClass, searchScope, parameters);

				if(progress != null)
				{
					progress.popState();
				}

				return result;
			}
		});
	}

	public static class SearchParameters
	{
		private final DotNetTypeDeclaration myClass;
		private final SearchScope myScope;
		private final boolean myCheckDeep;
		private final boolean myCheckInheritance;
		private final Condition<String> myNameCondition;

		public SearchParameters(@NotNull final DotNetTypeDeclaration aClass, @NotNull SearchScope scope, final boolean checkDeep,
				final boolean checkInheritance)
		{
			this(aClass, scope, checkDeep, checkInheritance, Condition.TRUE);
		}

		public SearchParameters(@NotNull final DotNetTypeDeclaration aClass, @NotNull SearchScope scope, final boolean checkDeep,
				final boolean checkInheritance, @NotNull final Condition<String> nameCondition)
		{
			myClass = aClass;
			myScope = scope;
			myCheckDeep = checkDeep;
			myCheckInheritance = checkInheritance;
			myNameCondition = nameCondition;
		}

		@NotNull
		public DotNetTypeDeclaration getClassToProcess()
		{
			return myClass;
		}

		@NotNull
		public Condition<String> getNameCondition()
		{
			return myNameCondition;
		}

		public boolean isCheckDeep()
		{
			return myCheckDeep;
		}

		public SearchScope getScope()
		{
			return myScope;
		}

		public boolean isCheckInheritance()
		{
			return myCheckInheritance;
		}
	}

	private ClassInheritorsSearch()
	{
		super("org.mustbe.consulo.dotnet.core");
	}

	public static Query<DotNetTypeDeclaration> search(@NotNull final DotNetTypeDeclaration aClass, @NotNull SearchScope scope,
			final boolean checkDeep, final boolean checkInheritance)
	{
		return search(new SearchParameters(aClass, scope, checkDeep, checkInheritance));
	}

	public static Query<DotNetTypeDeclaration> search(@NotNull SearchParameters parameters)
	{
		return INSTANCE.createQuery(parameters);
	}

	public static Query<DotNetTypeDeclaration> search(@NotNull final DotNetTypeDeclaration aClass, @NotNull SearchScope scope,
			final boolean checkDeep)
	{
		return search(aClass, scope, checkDeep, true);
	}

	public static Query<DotNetTypeDeclaration> search(@NotNull final DotNetTypeDeclaration aClass, final boolean checkDeep)
	{
		return search(aClass, aClass.getUseScope(), checkDeep);
	}

	public static Query<DotNetTypeDeclaration> search(@NotNull DotNetTypeDeclaration aClass)
	{
		return search(aClass, true);
	}

	private static boolean processInheritors(@NotNull final Processor<DotNetTypeDeclaration> consumer,
			@NotNull final DotNetTypeDeclaration baseClass, @NotNull final SearchScope searchScope, @NotNull final SearchParameters parameters)
	{
		if(isFinal(baseClass))
		{
			return true;
		}

		final String qname = ApplicationManager.getApplication().runReadAction(new Computable<String>()
		{
			@Override
			public String compute()
			{
				return baseClass.getVmQName();
			}
		});
		if(DotNetTypes.System_Object.equals(qname))
		{
			return AllClassesSearch.search(searchScope, baseClass.getProject(), parameters.getNameCondition()).forEach(
					new Processor<DotNetTypeDeclaration>()
			{
				@Override
				public boolean process(final DotNetTypeDeclaration aClass)
				{
					ProgressIndicatorProvider.checkCanceled();
					final String qname1 = ApplicationManager.getApplication().runReadAction(new Computable<String>()
					{
						@Override
						@Nullable
						public String compute()
						{
							return aClass.getVmQName();
						}
					});
					return DotNetTypes.System_Object.equals(qname1) || consumer.process(aClass);
				}
			});
		}

		final Ref<DotNetTypeDeclaration> currentBase = Ref.create(null);
		val stack = new Stack<Pair<Reference<DotNetTypeDeclaration>, String>>();
		// there are two sets for memory optimization: it's cheaper to hold FQN than PsiClass
		val processedFqns = new THashSet<String>(); // FQN of processed classes if the class has one

		final Processor<DotNetTypeDeclaration> processor = new Processor<DotNetTypeDeclaration>()
		{
			@Override
			public boolean process(final DotNetTypeDeclaration candidate)
			{
				ProgressIndicatorProvider.checkCanceled();

				val result = new Ref<Boolean>();
				val fqn = new String[1];
				ApplicationManager.getApplication().runReadAction(new Runnable()
				{
					@Override
					public void run()
					{
						fqn[0] = candidate.getVmQName();
						if(parameters.isCheckInheritance() || parameters.isCheckDeep())
						{
							if(!candidate.isInheritor(currentBase.get(), false))
							{
								result.set(true);
								return;
							}
						}

						if(PsiSearchScopeUtil.isInScope(searchScope, candidate))
						{
							final String name = candidate.getName();
							if(name != null && parameters.getNameCondition().value(name) && !consumer.process(candidate))
							{
								result.set(false);
							}
						}
					}
				});
				if(!result.isNull())
				{
					return result.get().booleanValue();
				}

				if(parameters.isCheckDeep() && !isFinal(candidate))
				{
					Reference<DotNetTypeDeclaration> ref = fqn[0] == null ? createHardReference(candidate) : new
							SoftReference<DotNetTypeDeclaration>(candidate);
					stack.push(Pair.create(ref, fqn[0]));
				}

				return true;
			}
		};
		stack.push(Pair.create(createHardReference(baseClass), qname));
		val projectScope = GlobalSearchScope.allScope(baseClass.getProject());
		final DotNetPsiSearcher facade = DotNetPsiSearcher.getInstance(projectScope.getProject());
		while(!stack.isEmpty())
		{
			ProgressIndicatorProvider.checkCanceled();

			Pair<Reference<DotNetTypeDeclaration>, String> pair = stack.pop();
			DotNetTypeDeclaration psiClass = pair.getFirst().get();
			final String fqn = pair.getSecond();
			if(psiClass == null)
			{
				psiClass = ApplicationManager.getApplication().runReadAction(new Computable<DotNetTypeDeclaration>()
				{
					@Override
					public DotNetTypeDeclaration compute()
					{
						return facade.findType(fqn, projectScope);
					}
				});
				if(psiClass == null)
				{
					continue;
				}
			}

			if(!processedFqns.add(fqn))
			{
				continue;
			}

			currentBase.set(psiClass);
			if(!DirectClassInheritorsSearch.search(psiClass, projectScope, false).forEach(processor))
			{
				return false;
			}
		}
		return true;
	}

	private static Reference<DotNetTypeDeclaration> createHardReference(final DotNetTypeDeclaration candidate)
	{
		return new SoftReference<DotNetTypeDeclaration>(candidate)
		{
			@Override
			public DotNetTypeDeclaration get()
			{
				return candidate;
			}
		};
	}

	private static boolean isFinal(@NotNull final DotNetTypeDeclaration baseClass)
	{
		return ApplicationManager.getApplication().runReadAction(new Computable<Boolean>()
		{
			@Override
			public Boolean compute()
			{
				return !baseClass.hasModifier(DotNetModifier.SEALED);
			}
		});
	}
}
