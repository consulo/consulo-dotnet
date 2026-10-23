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
package consulo.dotnet.psi.resolve;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.Application;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.language.Language;

/**
 * Opt-in of a .NET language for interop: other .NET languages (C#) see its {@link DotNetTypeDeclaration}s and their
 * members as their own types, through wrappers made from the DotNet* interfaces only.
 * <p>
 * The language describes its declarations as compiled: members are what other languages call (an F# module function is
 * a static method, a union case is a nested type), types and members navigate to the source.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface DotNetLanguageInteropProvider {
    Language getLanguage();

    /**
     * @return whether this type of the language is visible to other languages
     */
    default boolean isAvailable(DotNetTypeDeclaration type) {
        return true;
    }

    static boolean isInteropType(DotNetTypeDeclaration type) {
        Language language = type.getLanguage();
        for (DotNetLanguageInteropProvider provider : Application.get().getExtensionList(DotNetLanguageInteropProvider.class)) {
            if (provider.getLanguage() == language && provider.isAvailable(type)) {
                return true;
            }
        }
        return false;
    }
}
