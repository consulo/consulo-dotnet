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
package consulo.dotnet.impl.assembly.summary;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ServiceImpl;
import consulo.application.Application;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.Task;
import consulo.application.util.CachedValueProvider;
import consulo.application.util.CachedValuesManager;
import consulo.application.util.concurrent.JobLauncher;
import consulo.component.util.ModificationTracker;
import consulo.dotnet.assembly.summary.*;
import consulo.language.editor.DaemonCodeAnalyzer;
import consulo.localize.LocalizeValue;
import consulo.module.Module;
import consulo.module.content.ProjectRootManager;
import consulo.project.Project;
import jakarta.inject.Singleton;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Answers from a name table per module - built once from the stored summaries of the libraries the module may add, and
 * cached until the roots of the project change or a summary is computed - so that a lookup is a map get. The missing
 * summaries are computed in the background, concurrently, then the highlighting is restarted.
 *
 * @author VISTALL
 */
@Singleton
@ServiceImpl
public class DotNetAddableLibraryLookupImpl implements DotNetAddableLibraryLookup {
    private record NameTable(Map<String, List<TypeMatch>> types, Map<String, List<ExtensionMethodMatch>> extensionMethods, List<Path> missing) {
    }

    // the files whose summaries are being computed - so that a file is queued once
    private final Set<Path> myQueued = ConcurrentHashMap.newKeySet();

    @RequiredReadAction
    @Override
    public List<TypeMatch> findTypes(Module module, String name) {
        return table(module).types().getOrDefault(name, List.of());
    }

    @RequiredReadAction
    @Override
    public List<ExtensionMethodMatch> findExtensionMethods(Module module, String name) {
        return table(module).extensionMethods().getOrDefault(name, List.of());
    }

    @RequiredReadAction
    private NameTable table(Module module) {
        Project project = module.getProject();
        NameTable table = CachedValuesManager.getManager(project).getCachedValue(module, () -> {
            DotNetAssemblySummaryIndex index = DotNetAssemblySummaryIndex.getInstance();
            ModificationTracker summaries = index::getModificationCount;
            return CachedValueProvider.Result.create(build(module, index), ProjectRootManager.getInstance(project), summaries);
        });
        if (!table.missing().isEmpty()) {
            compute(project, table.missing());
        }
        return table;
    }

    @RequiredReadAction
    private static NameTable build(Module module, DotNetAssemblySummaryIndex index) {
        Map<String, List<TypeMatch>> types = new HashMap<>();
        Map<String, List<ExtensionMethodMatch>> extensionMethods = new HashMap<>();
        List<Path> missing = new ArrayList<>();
        for (DotNetAddableLibraryProvider provider : Application.get().getExtensionList(DotNetAddableLibraryProvider.class)) {
            for (DotNetAddableLibrary library : provider.getAddableLibraries(module)) {
                DotNetAssemblySummary summary = index.getStoredSummary(library.getFile());
                if (summary == null) {
                    missing.add(library.getFile());
                    continue;
                }
                for (DotNetAssemblySummary.Type type : summary.types()) {
                    types.computeIfAbsent(type.name(), k -> new ArrayList<>()).add(new TypeMatch(library, type));
                }
                for (DotNetAssemblySummary.ForwardedType type : summary.forwardedTypes()) {
                    // the kind of a forwarded type is in the assembly it is forwarded to
                    types.computeIfAbsent(type.name(), k -> new ArrayList<>())
                        .add(new TypeMatch(library, new DotNetAssemblySummary.Type(type.name(), type.namespace(), type.arity(), 0)));
                }
                Set<String> seen = new HashSet<>();
                for (DotNetAssemblySummary.ExtensionMethod method : summary.extensionMethods()) {
                    // one match per name and namespace of a library, however many overloads
                    if (seen.add(method.name() + "|" + method.namespace())) {
                        extensionMethods.computeIfAbsent(method.name(), k -> new ArrayList<>()).add(new ExtensionMethodMatch(library, method.namespace()));
                    }
                }
            }
        }
        return new NameTable(types, extensionMethods, missing);
    }

    /**
     * Reads the missing summaries in the background, concurrently, then restarts the highlighting so that the fixes see them.
     * A file that is not an assembly is stored as such, so it is not queued again.
     */
    private void compute(Project project, List<Path> files) {
        List<Path> queued = new ArrayList<>();
        for (Path file : files) {
            if (myQueued.add(file)) {
                queued.add(file);
            }
        }
        if (queued.isEmpty()) {
            return;
        }
        Task.Backgroundable.queue(project, LocalizeValue.localizeTODO("Indexing .NET libraries"), true, (ProgressIndicator indicator) -> {
            try {
                indicator.setIndeterminate(false);
                AtomicInteger done = new AtomicInteger();
                DotNetAssemblySummaryIndex index = DotNetAssemblySummaryIndex.getInstance();
                JobLauncher.getInstance().invokeConcurrentlyUnderProgress(queued, indicator, file -> {
                    index.getSummary(file);
                    indicator.setFraction((double) done.incrementAndGet() / queued.size());
                    return true;
                });
            }
            finally {
                myQueued.removeAll(queued);
            }
            if (!project.isDisposed()) {
                DaemonCodeAnalyzer.getInstance(project).restart();
            }
        });
    }
}
