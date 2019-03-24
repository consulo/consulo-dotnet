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

package consulo.dotnet.run.coverage;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.intellij.coverage.CoverageEngine;
import com.intellij.coverage.CoverageRunner;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.util.SmartList;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.util.NotNullPairFunction;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public abstract class DotNetCoverageRunner extends CoverageRunner
{
	@Nonnull
	public static List<DotNetCoverageRunner> findAvailableRunners(@Nonnull RunProfile configuration)
	{
		if(!(configuration instanceof DotNetConfigurationWithCoverage))
		{
			return Collections.emptyList();
		}
		Module module = ((DotNetConfigurationWithCoverage) configuration).getConfigurationModule().getModule();
		if(module != null)
		{
			DotNetRunModuleExtension moduleExtension = ModuleUtilCore.getExtension(module, DotNetRunModuleExtension.class);
			if(moduleExtension == null)
			{
				return Collections.emptyList();
			}
			List<DotNetCoverageRunner> list = new SmartList<DotNetCoverageRunner>();
			for(CoverageRunner coverageRunner : CoverageRunner.EP_NAME.getExtensionList())
			{
				if(coverageRunner instanceof DotNetCoverageRunner && ((DotNetCoverageRunner) coverageRunner).acceptModuleExtension(moduleExtension))
				{
					list.add((DotNetCoverageRunner) coverageRunner);
				}
			}
			return list;
		}
		return Collections.emptyList();
	}

	@Nonnull
	public abstract NotNullPairFunction<DotNetConfigurationWithCoverage, GeneralCommandLine, GeneralCommandLine> getModifierForCommandLine();

	public abstract boolean acceptModuleExtension(@Nonnull DotNetRunModuleExtension<?> moduleExtension);

	@Override
	public boolean acceptsCoverageEngine(@Nonnull CoverageEngine engine)
	{
		return engine instanceof DotNetCoverageEngine;
	}
}
