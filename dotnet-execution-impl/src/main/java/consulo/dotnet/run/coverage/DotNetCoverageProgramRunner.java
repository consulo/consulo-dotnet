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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.coverage.CoverageExecutor;
import com.intellij.coverage.CoverageHelper;
import com.intellij.coverage.CoverageRunner;
import com.intellij.coverage.CoverageRunnerData;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ConfigurationInfoProvider;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import consulo.dotnet.run.PatchableRunProfileState;
import consulo.util.NotNullPairFunction;

/**
 * @author VISTALL
 * @since 10.01.15
 */
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

			NotNullPairFunction<DotNetConfigurationWithCoverage, GeneralCommandLine, GeneralCommandLine> modifierForCommandLine = dotNetCoverageRunner.getModifierForCommandLine();

			DotNetConfigurationWithCoverage runProfile = (DotNetConfigurationWithCoverage) environment.getRunProfile();

			PatchableRunProfileState patchableRunProfileState = (PatchableRunProfileState) state;
			patchableRunProfileState.modifyCommandLine(generalCommandLine -> modifierForCommandLine.fun(runProfile, generalCommandLine));

			patchableRunProfileState.setProcessHandlerConsumer(osProcessHandler -> CoverageHelper.attachToProcess((RunConfigurationBase) runProfile, osProcessHandler, environment.getRunnerSettings()));
		}
		else
		{
			throw new ExecutionException("Unknown configuration");
		}
		return super.doExecute(state, environment);
	}
}
