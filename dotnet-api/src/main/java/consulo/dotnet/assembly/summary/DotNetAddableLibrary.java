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

import consulo.annotation.access.RequiredWriteAction;
import consulo.module.Module;

import java.nio.file.Path;

/**
 * A library a module does not reference yet and may add - an assembly of its SDK, a NuGet package…
 * How it is added depends on the project model (a library order entry of a Consulo .NET module, a {@code <Reference>}
 * or a {@code <PackageReference>} of an MSBuild project…), see {@link DotNetAddableLibraryProvider}.
 *
 * @author VISTALL
 */
public interface DotNetAddableLibrary {
    /**
     * @return the name shown to the user and kept in the project model: the assembly name, the package id…
     */
    String getName();

    /**
     * @return the assembly whose {@link DotNetAssemblySummary} tells what the library offers
     */
    Path getFile();

    /**
     * Adds the library to the module, inside a write action and a command of the caller
     */
    @RequiredWriteAction
    void addTo(Module module);
}
