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

import consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import consulo.execution.configuration.RunConfigurationBase;
import consulo.execution.coverage.CoverageEnabledConfiguration;
import consulo.execution.coverage.CoverageRunner;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class DotNetCoverageEnabledConfiguration extends CoverageEnabledConfiguration
{
	private final DotNetConfigurationWithCoverage myConfiguration;

	public static DotNetCoverageEnabledConfiguration get(DotNetConfigurationWithCoverage configurationWithCoverage)
	{
		return (DotNetCoverageEnabledConfiguration) CoverageEnabledConfiguration.getOrCreate((RunConfigurationBase) configurationWithCoverage);
	}

	public DotNetCoverageEnabledConfiguration(DotNetConfigurationWithCoverage configuration)
	{
		super((RunConfigurationBase) configuration);
		myConfiguration = configuration;
	}

	/**
	 * The first runner that supports the .NET module extension of the configuration.
	 */
	@Nullable
	@Override
	public CoverageRunner getCoverageRunner()
	{
		List<DotNetCoverageRunner> availableRunners = DotNetCoverageRunner.findAvailableRunners(myConfiguration);
		return availableRunners.isEmpty() ? null : availableRunners.get(0);
	}

	@Nullable
	@Override
	public String getRunnerId()
	{
		CoverageRunner runner = getCoverageRunner();
		return runner == null ? null : runner.getId();
	}
}
