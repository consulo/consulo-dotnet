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

import consulo.annotation.component.ExtensionImpl;
import consulo.dotnet.run.PatchableRunProfileState;
import consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import consulo.execution.configuration.*;
import consulo.execution.coverage.*;
import consulo.execution.runner.DefaultProgramRunner;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.ui.RunContentDescriptor;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * @author VISTALL
 * @since 10.01.15
 */
@ExtensionImpl
public class DotNetCoverageProgramRunner extends DefaultProgramRunner
{
	@Nonnull
	@Override
	public String getRunnerId()
	{
		return ".NetCoverage";
	}

	@Override
	public boolean canRun(@Nonnull String executorId, @Nonnull RunProfile profile)
	{
		if(!CoverageExecutor.EXECUTOR_ID.equals(executorId))
		{
			return false;
		}

		if(!(profile instanceof DotNetConfigurationWithCoverage))
		{
			return false;
		}
		CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.getOrCreate((RunConfigurationBase) profile);
		CoverageRunner coverageRunner = coverageEnabledConfiguration.getCoverageRunner();
		if(coverageRunner == null || !coverageEnabledConfiguration.isCoverageEnabled())
		{
			return false;
		}

		if(!DotNetCoverageRunner.findAvailableRunners(profile).contains(coverageRunner))
		{
			return false;
		}
		return true;
	}

	@Nullable
	@Override
	public RunnerSettings createConfigurationData(ConfigurationInfoProvider settingsProvider)
	{
		return new CoverageRunnerData();
	}

	@Nullable
	@Override
	protected RunContentDescriptor doExecute(@Nonnull RunProfileState state, @Nonnull final ExecutionEnvironment environment) throws ExecutionException
	{
		if(state instanceof PatchableRunProfileState)
		{
			CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.getOrCreate((RunConfigurationBase) environment.getRunProfile());

			CoverageRunner coverageRunner = coverageEnabledConfiguration.getCoverageRunner();
			if(!coverageEnabledConfiguration.isCoverageEnabled() || coverageRunner == null)
			{
				throw new ExecutionException("Coverage is not enabled");
			}

			DotNetCoverageRunner dotNetCoverageRunner = (DotNetCoverageRunner) coverageRunner;

			BiFunction<DotNetConfigurationWithCoverage, GeneralCommandLine, GeneralCommandLine> modifierForCommandLine = dotNetCoverageRunner.getModifierForCommandLine();

			DotNetConfigurationWithCoverage runProfile = (DotNetConfigurationWithCoverage) environment.getRunProfile();

			PatchableRunProfileState patchableRunProfileState = (PatchableRunProfileState) state;
			patchableRunProfileState.modifyCommandLine(generalCommandLine -> modifierForCommandLine.apply(runProfile, generalCommandLine));

			patchableRunProfileState.setProcessHandlerConsumer(osProcessHandler -> CoverageHelper.attachToProcess((RunConfigurationBase) runProfile, osProcessHandler, environment.getRunnerSettings()));
		}
		else
		{
			throw new ExecutionException("Unknown configuration");
		}
		return super.doExecute(state, environment);
	}
}
