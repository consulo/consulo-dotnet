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
package consulo.dotnet.packageManager;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.Application;
import consulo.module.Module;
import consulo.ui.annotation.RequiredUIAccess;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Manages the NuGet packages of a module the way its project system keeps them: the {@code PackageReference} items of an SDK-style
 * project file, the entries of a packages.config.
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface DotNetPackageManager {
    /**
     * The manager of the packages of the module, or null if no project system the module belongs to keeps packages.
     */
    @RequiredReadAction
    static @Nullable DotNetPackageManager find(Module module) {
        return Application.get().getExtensionPoint(DotNetPackageManager.class).findFirstSafe(it -> it.isApplicable(module));
    }

    @RequiredReadAction
    boolean isApplicable(Module module);

    /**
     * The packages the module references directly.
     */
    @RequiredReadAction
    List<DotNetPackageReference> getPackages(Module module);

    /**
     * References the version of the package - adds the reference, or changes the version of the existing one - and restores the packages.
     *
     * @return completes when the reference is written; the restore goes on after it
     */
    @RequiredUIAccess
    CompletableFuture<?> install(Module module, String id, String version);

    /**
     * Removes the reference of the package and restores the packages.
     *
     * @return completes when the reference is removed; the restore goes on after it
     */
    @RequiredUIAccess
    CompletableFuture<?> uninstall(Module module, String id);
}
