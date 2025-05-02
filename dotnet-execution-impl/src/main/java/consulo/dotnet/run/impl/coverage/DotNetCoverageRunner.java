/*
 * Copyright 2013-2015 must-be.org
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

package consulo.dotnet.run.impl.coverage;

import consulo.application.Application;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import consulo.execution.configuration.RunProfile;
import consulo.execution.coverage.CoverageEngine;
import consulo.execution.coverage.CoverageRunner;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.process.cmd.GeneralCommandLine;
import consulo.util.collection.SmartList;

import jakarta.annotation.Nonnull;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author VISTALL
 * @since 2015-01-10
 */
public abstract class DotNetCoverageRunner extends CoverageRunner {
    @Nonnull
    public static List<DotNetCoverageRunner> findAvailableRunners(@Nonnull RunProfile configuration) {
        if (!(configuration instanceof DotNetConfigurationWithCoverage dotNetConfigurationWithCoverage)) {
            return Collections.emptyList();
        }
        Module module = dotNetConfigurationWithCoverage.getConfigurationModule().getModule();
        if (module != null) {
            DotNetRunModuleExtension moduleExtension = ModuleUtilCore.getExtension(module, DotNetRunModuleExtension.class);
            if (moduleExtension == null) {
                return Collections.emptyList();
            }
            return Application.get().getExtensionPoint(CoverageRunner.class).collectMapped(
                new SmartList<>(),
                coverageRunner -> coverageRunner instanceof DotNetCoverageRunner dotNetCoverageRunner
                    && dotNetCoverageRunner.acceptModuleExtension(moduleExtension) ? dotNetCoverageRunner : null
            );
        }
        return Collections.emptyList();
    }

    @Nonnull
    public abstract BiFunction<DotNetConfigurationWithCoverage, GeneralCommandLine, GeneralCommandLine> getModifierForCommandLine();

    public abstract boolean acceptModuleExtension(@Nonnull DotNetRunModuleExtension<?> moduleExtension);

    @Override
    public boolean acceptsCoverageEngine(@Nonnull CoverageEngine engine) {
        return engine instanceof DotNetCoverageEngine;
    }
}
