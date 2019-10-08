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

package consulo.dotnet.lang.psi.impl;

import com.intellij.ProjectTopics;
import com.intellij.codeInsight.daemon.impl.SmartHashSet;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.lang.psi.impl.stub.DotNetNamespaceStubUtil;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import consulo.dotnet.resolve.GlobalSearchScopeFilter;
import consulo.dotnet.resolve.impl.IndexBasedDotNetPsiSearcher;
import gnu.trove.THashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		return ServiceManager.getService(project, DotNetNamespaceCacheManager.class);
	}

	public static interface ItemCalculator
	{
		@Nonnull
		@RequiredReadAction
		Set<PsiElement> compute(@Nonnull Project project,
				@Nullable final IndexBasedDotNetPsiSearcher searcher,
				@Nonnull final String indexKey,
				@Nonnull final String thisQName,
				@Nonnull final GlobalSearchScope scope);

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
				@Nonnull final GlobalSearchScope scope)
		{
			assert searcher != null;

			final StubIndexKey<String, DotNetQualifiedElement> key = searcher.getElementByQNameIndexKey();
			final Set<String> qNames = new THashSet<>();
			StubIndex.getInstance().processAllKeys(key, qName ->
			{
				ProgressManager.checkCanceled();

				if(thisQName.isEmpty() && qName.startsWith(indexKey))
				{
					qNames.add(qName);
				}
				else if(qName.startsWith(thisQName))
				{
					String packageName = StringUtil.getPackageName(qName);
					if(packageName.equals(thisQName))
					{
						qNames.add(qName);
					}
				}
				return true;
			}, scope, new GlobalSearchScopeFilter(scope));

			final Set<PsiElement> elements = new THashSet<>(qNames.size());
			for(String qName : qNames)
			{
				ProgressManager.checkCanceled();

				elements.addAll(StubIndex.getElements(key, qName, project, scope, DotNetQualifiedElement.class));
			}

			return elements.isEmpty() ? Collections.<PsiElement>emptySet() : elements;
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
				@Nonnull final GlobalSearchScope scope)
		{
			assert searcher != null;

			final Set<String> qNames = new THashSet<>();
			StubIndex.getInstance().processAllKeys(searcher.getNamespaceIndexKey(), qName ->
			{
				ProgressManager.checkCanceled();
				if(DotNetNamespaceStubUtil.ROOT_FOR_INDEXING.equals(qName))
				{
					return true;
				}
				if(qName.startsWith(thisQName))
				{
					String packageName = StringUtil.getPackageName(qName);
					if(packageName.equals(thisQName))
					{
						qNames.add(qName);
					}
				}

				return true;
			}, scope, new GlobalSearchScopeFilter(scope));


			final Set<PsiElement> namespaces = new THashSet<>(qNames.size());
			for(String qName : qNames)
			{
				ProgressManager.checkCanceled();

				DotNetNamespaceAsElement namespace = DotNetPsiSearcher.getInstance(project).findNamespace(qName, scope);
				if(namespace != null)
				{
					namespaces.add(namespace);
				}
			}
			return namespaces.isEmpty() ? Collections.<PsiElement>emptySet() : namespaces;
		}

		@Nonnull
		@Override
		public DotNetNamespaceAsElement.ChildrenFilter getFilter()
		{
			return DotNetNamespaceAsElement.ChildrenFilter.ONLY_NAMESPACES;
		}
	};

	private final Map<DotNetNamespaceAsElement, Map<GlobalSearchScope, Set<PsiElement>>> myElementsCache = ContainerUtil.newConcurrentMap();
	private final Map<DotNetNamespaceAsElement, Map<GlobalSearchScope, Set<PsiElement>>> myChildNamespacesCache = ContainerUtil.newConcurrentMap();
	private final Map<String, Map<GlobalSearchScope, Set<DotNetTypeDeclaration>>> myTypesCache = ContainerUtil.newConcurrentMap();
	private final Map<String, Map<GlobalSearchScope, Ref<DotNetNamespaceAsElement>>> myNamespacesCache = ContainerUtil.newConcurrentMap();

	private final Project myProject;

	private long myLastOutOfCodeCount;

	@Inject
	DotNetNamespaceCacheManager(Project project, PsiModificationTracker modificationTracker)
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

		connect.subscribe(PsiModificationTracker.TOPIC, () ->
		{
			long outOfCodeBlockModificationCount = modificationTracker.getOutOfCodeBlockModificationCount();
			if(myLastOutOfCodeCount != outOfCodeBlockModificationCount)
			{
				myLastOutOfCodeCount = outOfCodeBlockModificationCount;

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
	public DotNetNamespaceAsElement computeNamespace(List<DotNetPsiSearcher> searchers, String qName, GlobalSearchScope scope)
	{
		Map<GlobalSearchScope, Ref<DotNetNamespaceAsElement>> map = myNamespacesCache.get(qName);
		if(map != null)
		{
			Ref<DotNetNamespaceAsElement> ref = map.get(scope);
			if(ref != null)
			{
				return ref.get();
			}
		}

		DotNetNamespaceAsElement compute = computeNamespaceImpl(myProject, searchers, qName, scope);

		if(map == null)
		{
			myNamespacesCache.put(qName, map = ContainerUtil.createWeakKeyWeakValueMap());
		}

		map.put(scope, Ref.create(compute));
		return compute;
	}

	@Nullable
	@RequiredReadAction
	private static DotNetNamespaceAsElement computeNamespaceImpl(Project project, List<DotNetPsiSearcher> searchers, String qName, GlobalSearchScope scope)
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
	public Set<DotNetTypeDeclaration> computeTypes(@Nonnull List<DotNetPsiSearcher> searchers, String qName, GlobalSearchScope scope)
	{
		Map<GlobalSearchScope, Set<DotNetTypeDeclaration>> map = myTypesCache.get(qName);
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
			myTypesCache.put(qName, map = ContainerUtil.createWeakKeyWeakValueMap());
		}

		map.put(scope, compute);
		return compute;
	}

	@Nonnull
	@RequiredReadAction
	private static Set<DotNetTypeDeclaration> computeTypesImpl(List<DotNetPsiSearcher> searchers, String qName, GlobalSearchScope scope)
	{
		Set<DotNetTypeDeclaration> typeDeclarations = new SmartHashSet<>();
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
			@Nonnull GlobalSearchScope scope,
			@Nonnull ItemCalculator calculator)
	{
		Map<DotNetNamespaceAsElement, Map<GlobalSearchScope, Set<PsiElement>>> rootMap = selectMap(calculator);

		Map<GlobalSearchScope, Set<PsiElement>> map = rootMap.get(key);
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
			rootMap.put(key, map = ContainerUtil.<GlobalSearchScope, Set<PsiElement>>newConcurrentMap());
		}

		map.put(scope, compute);
		return compute;
	}

	@Nonnull
	private Map<DotNetNamespaceAsElement, Map<GlobalSearchScope, Set<PsiElement>>> selectMap(ItemCalculator calculator)
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
