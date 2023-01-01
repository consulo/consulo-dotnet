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

package consulo.dotnet.impl.testIntegration;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.dotnet.psi.DotNetInheritUtil;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.resolve.DotNetShortNameSearcher;
import consulo.dotnet.psi.test.DotNetTestFrameworks;
import consulo.language.editor.testIntegration.TestFinder;
import consulo.language.editor.testIntegration.TestFinderHelper;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiNamedElement;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.project.Project;
import consulo.util.lang.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author VISTALL
 * @since 03.04.2016
 */
@ExtensionImpl
public class DotNetTestFinder implements TestFinder
{
	@Nullable
	@Override
	public DotNetTypeDeclaration findSourceElement(@Nonnull PsiElement from)
	{
		return PsiTreeUtil.getParentOfType(from, DotNetTypeDeclaration.class);
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public Collection<PsiElement> findTestsForClass(@Nonnull PsiElement element)
	{
		DotNetTypeDeclaration sourceElement = findSourceElement(element);
		if(sourceElement == null)
		{
			return Collections.emptyList();
		}
		String name = sourceElement.getName();
		if(name == null)
		{
			return Collections.emptyList();
		}

		Project project = sourceElement.getProject();
		GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(project);

		DotNetShortNameSearcher shortNameSearcher = DotNetShortNameSearcher.getInstance(project);

		Set<PsiElement> elements = new HashSet<>();
		Pattern pattern = Pattern.compile(".*" + name + ".*");
		for(String typeName : shortNameSearcher.getTypeNames(globalSearchScope, null))
		{
			if(pattern.matcher(typeName).matches())
			{
				for(DotNetTypeDeclaration typeDeclaration : shortNameSearcher.getTypes(typeName, globalSearchScope, null))
				{
					if(DotNetTestFrameworks.isTestType(typeDeclaration))
					{
						elements.add(typeDeclaration);
					}
				}
			}
		}
		return elements;
	}

	@Nonnull
	@Override
	@RequiredReadAction
	public Collection<PsiElement> findClassesForTest(@Nonnull PsiElement element)
	{
		DotNetTypeDeclaration sourceElement = findSourceElement(element);
		if(sourceElement == null)
		{
			return Collections.emptyList();
		}
		String name = sourceElement.getName();
		if(name == null)
		{
			return Collections.emptyList();
		}

		Project project = sourceElement.getProject();
		GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(project);
		DotNetShortNameSearcher shortNameSearcher = DotNetShortNameSearcher.getInstance(project);

		List<Pair<? extends PsiNamedElement, Integer>> classesWithWeights = new ArrayList<>();
		for(Pair<String, Integer> eachNameWithWeight : TestFinderHelper.collectPossibleClassNamesWithWeights(name))
		{
			Collection<DotNetTypeDeclaration> types = shortNameSearcher.getTypes(eachNameWithWeight.getFirst(), globalSearchScope, null);
			for(DotNetTypeDeclaration type : types)
			{
				if(!isTestSubject(type))
				{
					continue;
				}
				classesWithWeights.add(Pair.create(type, eachNameWithWeight.second));
			}
		}

		return TestFinderHelper.getSortedElements(classesWithWeights, false);
	}

	/**
	 * Type subject can't be attribute, enum, or test type
	 */
	@RequiredReadAction
	public static boolean isTestSubject(DotNetTypeDeclaration typeDeclaration)
	{
		if(typeDeclaration.isEnum() || DotNetInheritUtil.isAttribute(typeDeclaration) || DotNetTestFrameworks.isTestType(typeDeclaration))
		{
			return false;
		}
		return true;
	}

	@Override
	@RequiredReadAction
	public boolean isTest(@Nonnull PsiElement element)
	{
		DotNetTypeDeclaration sourceElement = findSourceElement(element);
		if(sourceElement == null)
		{
			return false;
		}
		return DotNetTestFrameworks.isTestType(sourceElement);
	}
}
