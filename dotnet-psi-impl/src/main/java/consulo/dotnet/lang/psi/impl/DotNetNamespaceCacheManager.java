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
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.AnyPsiChangeListener;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.util.Processor;
import com.intellij.util.SmartList;
import com.intellij.util.indexing.IdFilter;
import com.intellij.util.messages.MessageBusConnection;
import consulo.annotation.access.RequiredReadAction;
import consulo.disposer.Disposable;
import consulo.dotnet.lang.psi.impl.stub.DotNetNamespaceStubUtil;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.resolve.DotNetNamespaceAsElement;
import consulo.dotnet.resolve.DotNetPsiSearcher;
import consulo.dotnet.resolve.impl.IndexBasedDotNetPsiSearcher;
import consulo.util.lang.ref.SimpleReference;
import gnu.trove.THashSet;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

		@RequiredReadAction
		default boolean process(@Nonnull Project project,
								@Nullable final IndexBasedDotNetPsiSearcher searcher,
								@Nonnull final String indexKey,
								@Nonnull final String thisQName,
								@Nonnull final GlobalSearchScope scope,
								@Nonnull Processor<PsiElement> processor)
		{
			Set<PsiElement> elements = compute(project, searcher, indexKey, thisQName, scope);

			for(PsiElement element : elements)
			{
				if(!processor.process(element))
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
									   @Nonnull final GlobalSearchScope scope)
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
							   @Nonnull GlobalSearchScope scope,
							   @Nonnull Processor<PsiElement> processor)
		{
			assert searcher != null;

			StubIndexKey<String, DotNetQualifiedElement> key = searcher.getElementByQNameIndexKey();
			StubIndex stubIndex = StubIndex.getInstance();

			Set<String> qNames = new THashSet<>();
			stubIndex.processAllKeys(key, qName ->
			{
				qNames.add(qName);
				return true;
			}, scope, IdFilter.getProjectIdFilter(project, true));

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
										@Nonnull GlobalSearchScope scope,
										@Nonnull StubIndexKey<String, DotNetQualifiedElement> key,
										@Nonnull StubIndex stubIndex,
										@Nonnull String qName,
										@Nonnull Processor<PsiElement> processor)
		{
			return stubIndex.processElements(key, qName, project, scope, DotNetQualifiedElement.class, processor::process);
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
							   @Nonnull GlobalSearchScope scope,
							   @Nonnull Processor<PsiElement> processor)
		{
			assert searcher != null;

			Set<String> fromIndex = new THashSet<>();
			StubIndex.getInstance().processAllKeys(searcher.getNamespaceIndexKey(), it -> {
				fromIndex.add(it);
				return true;
			}, scope, IdFilter.getProjectIdFilter(project, true));

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
							if(!processor.process(namespaceElement))
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

	private final Map<DotNetNamespaceAsElement, Map<GlobalSearchScope, Set<PsiElement>>> myElementsCache = consulo.util.collection.ContainerUtil.newConcurrentMap();
	private final Map<DotNetNamespaceAsElement, Map<GlobalSearchScope, Set<PsiElement>>> myChildNamespacesCache = consulo.util.collection.ContainerUtil.newConcurrentMap();
	private final Map<String, Map<GlobalSearchScope, Set<DotNetTypeDeclaration>>> myTypesCache = consulo.util.collection.ContainerUtil.newConcurrentMap();
	private final Map<String, Map<GlobalSearchScope, SimpleReference<DotNetNamespaceAsElement>>> myNamespacesCache = consulo.util.collection.ContainerUtil.newConcurrentMap();

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

		project.getMessageBus().connect().subscribe(PsiManagerImpl.ANY_PSI_CHANGE_TOPIC, new AnyPsiChangeListener()
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
	public DotNetNamespaceAsElement computeNamespace(List<DotNetPsiSearcher> searchers, String qName, GlobalSearchScope scope)
	{
		Map<GlobalSearchScope, SimpleReference<DotNetNamespaceAsElement>> map = myNamespacesCache.get(qName);
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
			myTypesCache.put(qName, map = new ConcurrentHashMap<>());
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
			rootMap.put(key, map = consulo.util.collection.ContainerUtil.<GlobalSearchScope, Set<PsiElement>>newConcurrentMap());
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
