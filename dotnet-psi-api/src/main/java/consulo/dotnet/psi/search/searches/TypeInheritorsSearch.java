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
package consulo.dotnet.psi.search.searches;

import consulo.annotation.access.RequiredReadAction;
import consulo.application.AccessRule;
import consulo.application.ApplicationManager;
import consulo.application.progress.ProgressIndicatorProvider;
import consulo.application.util.query.EmptyQuery;
import consulo.application.util.query.ExtensibleQueryFactory;
import consulo.application.util.query.Query;
import consulo.content.scope.SearchScope;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.psi.DotNetModifier;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.resolve.DotNetPsiSearcher;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.scope.PsiSearchScopeUtil;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.util.lang.function.Condition;
import consulo.util.lang.function.Conditions;
import consulo.util.lang.ref.SimpleReference;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author VISTALL
 * <p/>
 * Inspired by Jetbrains from java-impl (com.intellij.psi.search.searches.ClassInheritorsSearch) by max
 */
public class TypeInheritorsSearch extends ExtensibleQueryFactory<DotNetTypeDeclaration, TypeInheritorsSearch.SearchParameters> {
    public static final Logger LOGGER = Logger.getInstance(TypeInheritorsSearch.class);

    public static final TypeInheritorsSearch INSTANCE = new TypeInheritorsSearch();

    public static class SearchParameters {
        private final Project myProject;
        private final String myVmQName;
        private final SearchScope myScope;
        private final boolean myCheckDeep;
        private final boolean myCheckInheritance;
        private final Function<DotNetTypeDeclaration, DotNetTypeDeclaration> myTransformer;
        private final Condition<String> myNameCondition;

        public SearchParameters(Project project,
                                final String aClassQName,
                                SearchScope scope,
                                final boolean checkDeep,
                                final boolean checkInheritance,
                                Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer) {
            this(project, aClassQName, scope, checkDeep, checkInheritance, Conditions.<String>alwaysTrue(), transformer);
        }

        public SearchParameters(Project project,
                                final String aClassQName,
                                SearchScope scope,
                                final boolean checkDeep,
                                final boolean checkInheritance,
                                final Condition<String> nameCondition,
                                Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer) {
            myProject = project;
            myVmQName = aClassQName;
            myScope = scope;
            myCheckDeep = checkDeep;
            myCheckInheritance = checkInheritance;
            myNameCondition = nameCondition;
            myTransformer = transformer;
        }

        public String getVmQName() {
            return myVmQName;
        }

        public Condition<String> getNameCondition() {
            return myNameCondition;
        }

        public boolean isCheckDeep() {
            return myCheckDeep;
        }

        public SearchScope getScope() {
            return myScope;
        }

        public boolean isCheckInheritance() {
            return myCheckInheritance;
        }

        public Project getProject() {
            return myProject;
        }
    }

    private TypeInheritorsSearch() {
        super(TypeInheritorsSearchExecutor.class);
    }

    public static Query<DotNetTypeDeclaration> search(final DotNetTypeDeclaration typeDeclaration,
                                                      SearchScope scope,
                                                      final boolean checkDeep,
                                                      final boolean checkInheritance,
                                                      Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer) {
        String vmQName = ApplicationManager.getApplication().runReadAction((Supplier<String>) () -> {
            if (typeDeclaration.hasModifier(DotNetModifier.SEALED)) {
                return null;
            }
            return typeDeclaration.getVmQName();
        });
        if (vmQName == null) {
            return EmptyQuery.getEmptyQuery();
        }
        return search(new SearchParameters(typeDeclaration.getProject(), vmQName, scope, checkDeep, checkInheritance, transformer));
    }

    public static Query<DotNetTypeDeclaration> search(SearchParameters parameters) {
        return INSTANCE.createQuery(parameters);
    }

    public static Query<DotNetTypeDeclaration> search(final DotNetTypeDeclaration typeDeclaration,
                                                      SearchScope scope,
                                                      final boolean checkDeep,
                                                      Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer) {
        return search(typeDeclaration, scope, checkDeep, true, transformer);
    }

    public static Query<DotNetTypeDeclaration> search(final DotNetTypeDeclaration typeDeclaration, final boolean checkDeep) {
        return search(typeDeclaration, typeDeclaration.getUseScope(), checkDeep, DotNetPsiSearcher.DEFAULT_TRANSFORMER);
    }

    public static Query<DotNetTypeDeclaration> search(final DotNetTypeDeclaration typeDeclaration,
                                                      final boolean checkDeep,
                                                      Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer) {
        return search(typeDeclaration, typeDeclaration.getUseScope(), checkDeep, transformer);
    }

    public static Query<DotNetTypeDeclaration> search(DotNetTypeDeclaration typeDeclaration,
                                                      Function<DotNetTypeDeclaration, DotNetTypeDeclaration> transformer) {
        return search(typeDeclaration, true, transformer);
    }

    @RequiredReadAction
    static boolean processInheritors(final Predicate<? super DotNetTypeDeclaration> consumer,
                                     final String baseVmQName,
                                     final SearchScope searchScope,
                                     final SearchParameters parameters) {

        if (DotNetTypes.System.Object.equals(baseVmQName)) {
            return AllTypesSearch.search(searchScope, parameters.getProject(), parameters.getNameCondition()).forEach(aClass -> {
                ProgressIndicatorProvider.checkCanceled();
                final String qname1 = AccessRule.read(aClass::getVmQName);

                return DotNetTypes.System.Object.equals(qname1) || consumer.test(parameters.myTransformer.apply(aClass));
            });
        }

        final SimpleReference<String> currentBase = SimpleReference.create(null);
        final Stack<String> stack = new Stack<>();
        // there are two sets for memory optimization: it's cheaper to hold FQN than PsiClass
        final Set<String> processedFqns = new HashSet<>(); // FQN of processed classes if the class has one

        final Predicate<DotNetTypeDeclaration> processor = candidate -> {
            ProgressIndicatorProvider.checkCanceled();

            final SimpleReference<Boolean> result = SimpleReference.create();
            final SimpleReference<String> vmQNameRef = SimpleReference.create();

            ApplicationManager.getApplication().runReadAction(() -> {
                vmQNameRef.set(candidate.getVmQName());
                if (parameters.isCheckInheritance() || parameters.isCheckDeep()) {
                    if (!candidate.isInheritor(currentBase.get(), false)) {
                        result.set(true);
                        return;
                    }
                }

                if (PsiSearchScopeUtil.isInScope(searchScope, candidate)) {
                    final String name = candidate.getName();
                    if (name != null && parameters.getNameCondition().value(name) && !consumer.test(parameters.myTransformer.apply(candidate))) {
                        result.set(false);
                    }
                }
            });
            if (!result.isNull()) {
                return result.get();
            }

            if (parameters.isCheckDeep() && !isSealed(candidate)) {
                stack.push(vmQNameRef.get());
            }

            return true;
        };
        stack.push(baseVmQName);

        final GlobalSearchScope projectScope = GlobalSearchScope.allScope(parameters.getProject());
        while (!stack.isEmpty()) {
            ProgressIndicatorProvider.checkCanceled();

            String vmQName = stack.pop();

            if (!processedFqns.add(vmQName)) {
                continue;
            }

            currentBase.set(vmQName);
            if (!DirectTypeInheritorsSearch.search(parameters.getProject(), vmQName, projectScope, false).forEach(processor)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSealed(final DotNetTypeDeclaration baseClass) {
        return ApplicationManager.getApplication().runReadAction((Supplier<Boolean>) () -> baseClass.hasModifier(DotNetModifier.SEALED));
    }
}
