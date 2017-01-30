/*
 * Copyright 2013-2017 consulo.io
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

package consulo.msbuild.importProvider;

import consulo.msbuild.solution.model.WProject;

/**
 * @author VISTALL
 * @since 30-Jan-17
 */
public class MSBuildImportProject
{
	private final WProject myProjectInfo;
	private MSBuildImportTarget myTarget;

	public MSBuildImportProject(WProject projectInfo, MSBuildImportTarget target)
	{
		myProjectInfo = projectInfo;

		myTarget = target;
	}

	public WProject getProjectInfo()
	{
		return myProjectInfo;
	}

	public MSBuildImportTarget getTarget()
	{
		return myTarget;
	}

	public void setTarget(MSBuildImportTarget target)
	{
		myTarget = target;
	}
}
