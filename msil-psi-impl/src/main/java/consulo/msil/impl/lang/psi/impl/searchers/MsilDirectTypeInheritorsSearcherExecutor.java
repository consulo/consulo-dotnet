/*
 * Copyright 2013-2017 consulo.io
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
package consulo.msil.impl.lang.psi.impl.searchers;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.ApplicationManager;
import consulo.application.ReadAction;
import consulo.application.progress.ProgressIndicatorProvider;
import consulo.application.util.function.Computable;
import consulo.application.util.function.Processor;
import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.DotNetTypeList;
import consulo.dotnet.psi.search.searches.DirectTypeInheritorsSearch;
import consulo.dotnet.psi.search.searches.DirectTypeInheritorsSearchExecutor;
import consulo.internal.dotnet.msil.decompiler.util.MsilHelper;
import consulo.language.psi.scope.EverythingGlobalScope;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.msil.impl.lang.psi.impl.elementType.stub.index.MsilExtendsListIndex;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;

import java.util.*;

@ExtensionImpl
public class MsilDirectTypeInheritorsSearcherExecutor implements DirectTypeInheritorsSearchExecutor
{
	@Override
	public boolean execute(@Nonnull final DirectTypeInheritorsSearch.SearchParameters p, @Nonnull final Processor<? super DotNetTypeDeclaration> consumer)
	{
		String vmQName = p.getVmQName();

		/*if(DotNetTypes.System_Object.equals(qualifiedName))
		{
			final SearchScope scope = useScope;

			return AllClassesSearch.search(scope, aClass.getProject()).forEach(new Processor<DotNetTypeDeclaration>()
			{
				@Override
				public boolean process(final DotNetTypeDeclaration typeDcl)
				{
					if(typeDcl.isInterface())
					{
						return consumer.process(typeDcl);
					}
					final DotNetTypeDeclaration superClass = typeDcl.getSuperClass();
					if(superClass != null && DotNetTypes.System_Object.equals(ApplicationManager.getApplication().runReadAction(
							new Computable<String>()
					{
						public String compute()
						{
							return superClass.getPresentableQName();
						}
					})))
					{
						return consumer.process(typeDcl);
					}
					return true;
				}
			});
		}  */

		SearchScope useScope = p.getScope();
		final GlobalSearchScope scope = useScope instanceof GlobalSearchScope ? (GlobalSearchScope) useScope : new EverythingGlobalScope(p.getProject());
		final String searchKey = MsilHelper.cutGenericMarker(StringUtil.getShortName(vmQName));

		if(StringUtil.isEmpty(searchKey))
		{
			return true;
		}

		Collection<DotNetTypeList> candidates = ApplicationManager.getApplication().runReadAction((Computable<Collection<DotNetTypeList>>) () -> MsilExtendsListIndex.getInstance().get(searchKey, p
				.getProject(), scope));

		Map<String, List<DotNetTypeDeclaration>> classes = new HashMap<>();

		for(DotNetTypeList referenceList : candidates)
		{
			ProgressIndicatorProvider.checkCanceled();

			ReadAction.run(() ->
			{
				final DotNetTypeDeclaration candidate = (DotNetTypeDeclaration) referenceList.getParent();
				if(!checkInheritance(p, vmQName, candidate))
				{
					return;
				}

				String fqn = candidate.getPresentableQName();
				List<DotNetTypeDeclaration> list = classes.get(fqn);
				if(list == null)
				{
					list = new ArrayList<>();
					classes.put(fqn, list);
				}
				list.add(candidate);
			});
		}

		for(List<DotNetTypeDeclaration> sameNamedClasses : classes.values())
		{
			for(DotNetTypeDeclaration sameNamedClass : sameNamedClasses)
			{
				if(!consumer.process(sameNamedClass))
				{
					return false;
				}
			}
		}

		return true;
	}

	@RequiredReadAction
	private static boolean checkInheritance(final DirectTypeInheritorsSearch.SearchParameters p, final String vmQName, final DotNetTypeDeclaration candidate)
	{
		return !p.isCheckInheritance() || candidate.isInheritor(vmQName, false);
	}
}
