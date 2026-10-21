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

package consulo.dotnet.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.resolve.DotNetNamespaceAsElement;
import consulo.language.Language;
import consulo.language.psi.PsiElement;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public class CompositeDotNetNamespaceAsElement extends BaseDotNetNamespaceAsElement {
    private List<DotNetNamespaceAsElement> myList;

    public CompositeDotNetNamespaceAsElement(Project project, String qName, List<DotNetNamespaceAsElement> list) {
        super(project, Language.ANY, qName);
        myList = list;
    }

    @Override
    public boolean isWritable() {
        for (DotNetNamespaceAsElement namespaceAsElement : myList) {
            if (namespaceAsElement.isWritable()) {
                return true;
            }
        }
        return false;
    }

    @RequiredReadAction
    @Override
    public boolean processDeclarations(PsiScopeProcessor processor, ResolveState state, PsiElement lastParent, PsiElement place) {
        for (DotNetNamespaceAsElement dotNetNamespaceAsElement : myList) {
            if (!dotNetNamespaceAsElement.processDeclarations(processor, state, lastParent, place)) {
                return false;
            }
        }
        return true;
    }

    @RequiredReadAction
    @Override
    public Collection<PsiElement> findChildren(String name,
                                               GlobalSearchScope globalSearchScope,
                                               Function<PsiElement, PsiElement> transformer,
                                               ChildrenFilter filter) {
        // every part looks a child namespace up through DotNetPsiSearcher, which merges all parts - the same child comes from each of them
        Set<PsiElement> children = new LinkedHashSet<>();
        for (DotNetNamespaceAsElement dotNetNamespaceAsElement : myList) {
            children.addAll(dotNetNamespaceAsElement.findChildren(name, globalSearchScope, transformer, filter));
        }
        return children.isEmpty() ? Collections.emptyList() : new ArrayList<>(children);
    }

    @RequiredReadAction
    @Override
    @SuppressWarnings("unchecked")
    public Collection<PsiElement> getChildren(GlobalSearchScope globalSearchScope, Function<PsiElement, PsiElement> transformer, ChildrenFilter filter) {
        Set<PsiElement> children = new LinkedHashSet<>();
        for (DotNetNamespaceAsElement element : myList) {
            children.addAll(element.getChildren(globalSearchScope, transformer, filter));
        }
        return children.isEmpty() ? Collections.emptyList() : new ArrayList<>(children);
    }

    @RequiredReadAction
    @Override
    public boolean processChildren(GlobalSearchScope globalSearchScope,
                                   Function<PsiElement, PsiElement> transformer,
                                   ChildrenFilter filter,
                                   Predicate<PsiElement> processor) {
        for (DotNetNamespaceAsElement element : myList) {
            if (!element.processChildren(globalSearchScope, transformer, filter, processor)) {
                return false;
            }
        }
        return true;
    }
}
