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

import consulo.execution.coverage.BaseCoverageSuite;
import consulo.execution.coverage.CoverageEngine;
import consulo.execution.coverage.CoverageFileProvider;
import consulo.execution.coverage.CoverageRunner;
import consulo.project.Project;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class DotNetCoverageSuite extends BaseCoverageSuite
{
	private CoverageEngine myEngine;

	public DotNetCoverageSuite(CoverageEngine engine)
	{
		myEngine = engine;
	}

	public DotNetCoverageSuite(String name,
			@Nullable CoverageFileProvider fileProvider,
			long lastCoverageTimeStamp,
			boolean coverageByTestEnabled,
			boolean tracingEnabled,
			boolean trackTestFolders,
			CoverageRunner coverageRunner,
			CoverageEngine engine)
	{
		super(name, fileProvider, lastCoverageTimeStamp, coverageByTestEnabled, tracingEnabled, trackTestFolders, coverageRunner);
		myEngine = engine;
	}

	public DotNetCoverageSuite(String name,
			@Nullable CoverageFileProvider fileProvider,
			long lastCoverageTimeStamp,
			boolean coverageByTestEnabled,
			boolean tracingEnabled,
			boolean trackTestFolders,
			CoverageRunner coverageRunner,
			Project project,
			CoverageEngine engine)
	{
		super(name, fileProvider, lastCoverageTimeStamp, coverageByTestEnabled, tracingEnabled, trackTestFolders, coverageRunner, project);
		myEngine = engine;
	}

	@Nonnull
	@Override
	public CoverageEngine getCoverageEngine()
	{
		return myEngine;
	}
}
