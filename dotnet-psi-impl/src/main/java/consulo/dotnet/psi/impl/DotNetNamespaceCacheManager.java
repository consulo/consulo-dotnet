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

package consulo.dotnet.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.application.progress.ProgressManager;
import consulo.component.messagebus.MessageBusConnection;
import consulo.content.scope.SearchScope;
import consulo.disposer.Disposable;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.impl.resolve.impl.IndexBasedDotNetPsiSearcher;
import consulo.dotnet.psi.impl.stub.DotNetNamespaceStubUtil;
import consulo.dotnet.psi.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.psi.resolve.DotNetPsiSearcher;
import consulo.language.psi.AnyPsiChangeListener;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiManager;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.IdFilter;
import consulo.language.psi.stub.StubIndex;
import consulo.language.psi.stub.StubIndexKey;
import consulo.module.content.ProjectTopics;
import consulo.module.content.layer.event.ModuleRootEvent;
import consulo.module.content.layer.event.ModuleRootListener;
import consulo.project.DumbService;
import consulo.project.Project;
import consulo.util.collection.SmartList;
import consulo.util.lang.StringUtil;
import consulo.util.lang.ref.SimpleReference;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author VISTALL
 * @since 03.03.2016
 */
@Singleton
public class DotNetNamespaceCacheManager implements Disposable
{
	@Nonnull
	public static DotNetNamespaceCacheManager getInstance(@Nonnull Project project)
	{
		return project.getInstance(DotNetNamespaceCacheManager.class);
	}

	public static interface ItemCalculator
	{
		@Nonnull
		@RequiredReadAction
		Set<PsiElement> compute(@Nonnull Project project,
								@Nullable final IndexBasedDotNetPsiSearcher searcher,
								@Nonnull final String indexKey,
								@Nonnull final String thisQName,
								@Nonnull final SearchScope scope);

		@RequiredReadAction
		default boolean process(@Nonnull Project project,
								@Nullable final IndexBasedDotNetPsiSearcher searcher,
								@Nonnull final String indexKey,
								@Nonnull final String thisQName,
								@Nonnull final SearchScope scope,
								@Nonnull Predicate<PsiElement> processor)
		{
			Set<PsiElement> elements = compute(project, searcher, indexKey, thisQName, scope);

			for(PsiElement element : elements)
			{
				if(!processor.test(element))
				{
					return false;
				}
			}

			return true;
		}

		@Nonnull
		DotNetNamespaceAsElement.ChildrenFilter getFilter();
	}

	public static final ItemCalculator ONLY_ELEMENTS = new ItemCalculator()
	{
		@RequiredReadAction
		@Nonnull
		@Override
		public Set<PsiElement> compute(@Nonnull final Project project,
									   @Nullable final IndexBasedDotNetPsiSearcher searcher,
									   @Nonnull final String indexKey,
									   @Nonnull final String thisQName,
									   @Nonnull final SearchScope scope)
		{
			Set<PsiElement> elements = new HashSet<>();
			process(project, searcher, indexKey, thisQName, scope, it ->
			{
				elements.add(it);
				return true;
			});
			return elements;
		}

		@RequiredReadAction
		@Override
		public boolean process(@Nonnull Project project,
							   @Nullable IndexBasedDotNetPsiSearcher searcher,
							   @Nonnull String indexKey,
							   @Nonnull String thisQName,
							   @Nonnull SearchScope scope,
							   @Nonnull Predicate<PsiElement> processor)
		{
			assert searcher != null;

			StubIndexKey<String, DotNetQualifiedElement> key = searcher.getElementByQNameIndexKey();
			StubIndex stubIndex = StubIndex.getInstance();

			Set<String> qNames = new HashSet<>();
			stubIndex.processAllKeys(key, qName ->
			{
				qNames.add(qName);
				return true;
			}, (GlobalSearchScope) scope, IdFilter.getProjectIdFilter(project, true));

			for(String qName : qNames)
			{
				ProgressManager.checkCanceled();

				if(thisQName.isEmpty() && qName.startsWith(indexKey))
				{
					if(!processElements(project, scope, key, stubIndex, qName, processor))
					{
						return false;
					}
				}
				else if(qName.startsWith(thisQName))
				{
					String packageName = StringUtil.getPackageName(qName);
					if(packageName.equals(thisQName))
					{
						if(!processElements(project, scope, key, stubIndex, qName, processor))
						{
							return false;
						}
					}
				}
			}

			return true;
		}

		private boolean processElements(@Nonnull Project project,
										@Nonnull SearchScope scope,
										@Nonnull StubIndexKey<String, DotNetQualifiedElement> key,
										@Nonnull StubIndex stubIndex,
										@Nonnull String qName,
										@Nonnull Predicate<PsiElement> processor)
		{
			return stubIndex.processElements(key, qName, project, (GlobalSearchScope) scope, DotNetQualifiedElement.class, processor::test);
		}

		@Nonnull
		@Override
		public DotNetNamespaceAsElement.ChildrenFilter getFilter()
		{
			return DotNetNamespaceAsElement.ChildrenFilter.ONLY_ELEMENTS;
		}
	};

	public static final ItemCalculator ONLY_NAMESPACES = new ItemCalculator()
	{
		@RequiredReadAction
		@Nonnull
		@Override
		public Set<PsiElement> compute(@Nonnull final Project project,
									   @Nullable final IndexBasedDotNetPsiSearcher searcher,
									   @Nonnull final String indexKey,
									   @Nonnull final String thisQName,
									   @Nonnull final SearchScope scope)
		{
			Set<PsiElement> elements = new HashSet<>();
			process(project, searcher, indexKey, thisQName, scope, it ->
			{
				elements.add(it);
				return true;
			});
			return elements;
		}

		@RequiredReadAction
		@Override
		public boolean process(@Nonnull Project project,
							   @Nullable IndexBasedDotNetPsiSearcher searcher,
							   @Nonnull String indexKey,
							   @Nonnull String thisQName,
							   @Nonnull SearchScope scope,
							   @Nonnull Predicate<PsiElement> processor)
		{
			assert searcher != null;

			Set<String> fromIndex = new HashSet<>();
			StubIndex.getInstance().processAllKeys(searcher.getNamespaceIndexKey(), it -> {
				fromIndex.add(it);
				return true;
			}, (GlobalSearchScope) scope, IdFilter.getProjectIdFilter(project, true));

			DotNetPsiSearcher psiSearcher = DotNetPsiSearcher.getInstance(project);
			for(String qName : fromIndex)
			{
				ProgressManager.checkCanceled();
				if(DotNetNamespaceStubUtil.ROOT_FOR_INDEXING.equals(qName))
				{
					continue;
				}

				if(qName.startsWith(thisQName))
				{
					String packageName = StringUtil.getPackageName(qName);
					if(packageName.equals(thisQName))
					{
						DotNetNamespaceAsElement namespaceElement = psiSearcher.findNamespace(qName, scope);
						if(namespaceElement != null)
						{
							if(!processor.test(namespaceElement))
							{
								return false;
							}
						}
					}
				}
			}
			return true;
		}

		@Nonnull
		@Override
		public DotNetNamespaceAsElement.ChildrenFilter getFilter()
		{
			return DotNetNamespaceAsElement.ChildrenFilter.ONLY_NAMESPACES;
		}
	};

	private final Map<DotNetNamespaceAsElement, Map<SearchScope, Set<PsiElement>>> myElementsCache = consulo.util.collection.ContainerUtil.newConcurrentMap();
	private final Map<DotNetNamespaceAsElement, Map<SearchScope, Set<PsiElement>>> myChildNamespacesCache = consulo.util.collection.ContainerUtil.newConcurrentMap();
	private final Map<String, Map<SearchScope, Set<DotNetTypeDeclaration>>> myTypesCache = consulo.util.collection.ContainerUtil.newConcurrentMap();
	private final Map<String, Map<SearchScope, SimpleReference<DotNetNamespaceAsElement>>> myNamespacesCache = consulo.util.collection.ContainerUtil.newConcurrentMap();

	private final Project myProject;

	@Inject
	DotNetNamespaceCacheManager(Project project)
	{
		myProject = project;

		if(project.isDefault())
		{
			return;
		}

		MessageBusConnection connect = project.getMessageBus().connect();
		connect.subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener()
		{
			@Override
			public void rootsChanged(ModuleRootEvent event)
			{
				clear();
			}
		});

		connect.subscribe(DumbService.DUMB_MODE, new DumbService.DumbModeListener()
		{
			@Override
			public void exitDumbMode()
			{
				clear();
			}
		});

		project.getMessageBus().connect().subscribe(PsiManager.ANY_PSI_CHANGE_TOPIC, new AnyPsiChangeListener()
		{
			@Override
			public void beforePsiChanged(boolean isPhysical)
			{
				clear();
			}
		});
	}

	private void clear()
	{
		myElementsCache.clear();
		myChildNamespacesCache.clear();
		myNamespacesCache.clear();
		myTypesCache.clear();
	}

	@RequiredReadAction
	@Nullable
	public DotNetNamespaceAsElement computeNamespace(List<DotNetPsiSearcher> searchers, String qName, SearchScope scope)
	{
		Map<SearchScope, SimpleReference<DotNetNamespaceAsElement>> map = myNamespacesCache.get(qName);
		if(map != null)
		{
			SimpleReference<DotNetNamespaceAsElement> ref = map.get(scope);
			if(ref != null)
			{
				return ref.get();
			}
		}

		DotNetNamespaceAsElement compute = computeNamespaceImpl(myProject, searchers, qName, scope);

		if(map == null)
		{
			myNamespacesCache.put(qName, map = new ConcurrentHashMap<>());
		}

		map.put(scope, SimpleReference.create(compute));
		return compute;
	}

	@Nullable
	@RequiredReadAction
	private static DotNetNamespaceAsElement computeNamespaceImpl(Project project, List<DotNetPsiSearcher> searchers, String qName, SearchScope scope)
	{
		if(DumbService.isDumb(project))
		{
			return null;
		}

		List<DotNetNamespaceAsElement> namespaceAsElements = new SmartList<>();
		for(DotNetPsiSearcher searcher : searchers)
		{
			DotNetNamespaceAsElement namespace = searcher.findNamespace(qName, scope);
			if(namespace != null)
			{
				namespaceAsElements.add(namespace);
			}
		}

		if(namespaceAsElements.isEmpty())
		{
			return null;
		}
		else if(namespaceAsElements.size() == 1)
		{
			return namespaceAsElements.get(0);
		}
		return new CompositeDotNetNamespaceAsElement(project, qName, namespaceAsElements);
	}


	@RequiredReadAction
	@Nonnull
	public Set<DotNetTypeDeclaration> computeTypes(@Nonnull List<DotNetPsiSearcher> searchers, String qName, SearchScope scope)
	{
		Map<SearchScope, Set<DotNetTypeDeclaration>> map = myTypesCache.get(qName);
		if(map != null)
		{
			Set<DotNetTypeDeclaration> set = map.get(scope);
			if(set != null)
			{
				return set;
			}
		}

		Set<DotNetTypeDeclaration> compute = computeTypesImpl(searchers, qName, scope);

		if(map == null)
		{
			myTypesCache.put(qName, map = new ConcurrentHashMap<>());
		}

		map.put(scope, compute);
		return compute;
	}

	@Nonnull
	@RequiredReadAction
	private static Set<DotNetTypeDeclaration> computeTypesImpl(List<DotNetPsiSearcher> searchers, String qName, SearchScope scope)
	{
		Set<DotNetTypeDeclaration> typeDeclarations = new HashSet<>();
		for(DotNetPsiSearcher searcher : searchers)
		{
			typeDeclarations.addAll(searcher.findTypesImpl(qName, scope));
		}

		return typeDeclarations.isEmpty() ? Collections.<DotNetTypeDeclaration>emptySet() : typeDeclarations;
	}

	@Nonnull
	@RequiredReadAction
	public Set<PsiElement> computeElements(@Nullable IndexBasedDotNetPsiSearcher searcher,
										   @Nonnull DotNetNamespaceAsElement key,
										   @Nonnull String indexKey,
										   @Nonnull String thisQName,
										   @Nonnull SearchScope scope,
										   @Nonnull ItemCalculator calculator)
	{
		Map<DotNetNamespaceAsElement, Map<SearchScope, Set<PsiElement>>> rootMap = selectMap(calculator);

		Map<SearchScope, Set<PsiElement>> map = rootMap.get(key);
		if(map != null)
		{
			Set<PsiElement> psiElements = map.get(scope);
			if(psiElements != null)
			{
				return psiElements;
			}
		}

		Set<PsiElement> compute = calculator.compute(myProject, searcher, indexKey, thisQName, scope);

		if(map == null)
		{
			rootMap.put(key, map = consulo.util.collection.ContainerUtil.newConcurrentMap());
		}

		map.put(scope, compute);
		return compute;
	}

	@Nonnull
	private Map<DotNetNamespaceAsElement, Map<SearchScope, Set<PsiElement>>> selectMap(ItemCalculator calculator)
	{
		switch(calculator.getFilter())
		{
			case ONLY_ELEMENTS:
				return myElementsCache;
			case ONLY_NAMESPACES:
				return myChildNamespacesCache;
			default:
				throw new IllegalArgumentException("Wrong filter: " + calculator.getFilter());
		}
	}

	@Override
	public void dispose()
	{
		clear();
	}
}
