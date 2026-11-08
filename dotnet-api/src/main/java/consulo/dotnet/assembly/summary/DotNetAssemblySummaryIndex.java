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

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.application.Application;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

/**
 * An index of assembly files: one {@link DotNetAssemblySummary} per file, stored on disk and kept up to date by the size
 * and the modification time of the file. It does not depend on a module or a project - an assembly of an SDK, of a NuGet
 * package or of a library has the same summary everywhere; which files a module may add is the business of its project model.
 *
 * @author VISTALL
 */
@ServiceAPI(ComponentScope.APPLICATION)
public interface DotNetAssemblySummaryIndex {
    static DotNetAssemblySummaryIndex getInstance() {
        return Application.get().getInstance(DotNetAssemblySummaryIndex.class);
    }

    /**
     * The summary of the file, read from the metadata and stored if the store has none for the current state of the file.
     * Reads the file - call it outside the event dispatch thread, under a progress indicator for many files.
     *
     * @return null if the file is not a readable .NET assembly
     */
    @Nullable
    DotNetAssemblySummary getSummary(Path file);

    /**
     * The summary of the file only if it is stored for the current state of the file - never reads the assembly.
     *
     * @return null if the summary is not computed yet (or the file is not an assembly)
     */
    @Nullable
    DotNetAssemblySummary getStoredSummary(Path file);

    /**
     * @return a count that changes when a summary is computed - for the caches built from the stored summaries
     */
    long getModificationCount();
}
