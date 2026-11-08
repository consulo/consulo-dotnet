/*
 * Copyright 2013-2026 consulo.io
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
package consulo.dotnet.assembly.summary;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.application.Application;
import consulo.module.Module;

import java.util.List;

/**
 * Finds types and extension methods in the libraries a module may add ({@link DotNetAddableLibraryProvider}) - for the
 * quick fixes that import a namespace and add the library. Answers from the stored summaries only
 * ({@link DotNetAssemblySummaryIndex}); the summaries that are missing are computed in the background, and the answer is
 * complete once they are.
 *
 * @author VISTALL
 */
@ServiceAPI(ComponentScope.APPLICATION)
public interface DotNetAddableLibraryLookup {
    /**
     * @param library the library to add
     * @param type    a public type of it - for a forwarded type, the library is the one forwarding it (the one to reference)
     */
    record TypeMatch(DotNetAddableLibrary library, DotNetAssemblySummary.Type type) {
    }

    /**
     * @param library   the library to add
     * @param namespace the namespace of the static class declaring the method
     */
    record ExtensionMethodMatch(DotNetAddableLibrary library, String namespace) {
    }

    static DotNetAddableLibraryLookup getInstance() {
        return Application.get().getInstance(DotNetAddableLibraryLookup.class);
    }

    /**
     * @param name the simple name of the type, without the generic arity
     */
    @RequiredReadAction
    List<TypeMatch> findTypes(Module module, String name);

    @RequiredReadAction
    List<ExtensionMethodMatch> findExtensionMethods(Module module, String name);
}
