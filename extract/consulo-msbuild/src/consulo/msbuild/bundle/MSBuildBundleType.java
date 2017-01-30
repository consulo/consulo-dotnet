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

package consulo.msbuild.bundle;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.containers.ContainerUtil;
import consulo.msbuild.MSBuildIcons;
import consulo.msbuild.MicrosoftVisualStudioVersion;

/**
 * @author VISTALL
 * @since 08.06.2015
 */
public class MSBuildBundleType  extends BaseMSBuildBundleType
{
	@NotNull
	public static String getExecutable(String home)
	{
		return home + "/bin/MSBuild.exe";
	}

	@NotNull
	public static MSBuildBundleType getInstance()
	{
		return EP_NAME.findExtension(MSBuildBundleType.class);
	}

	public MSBuildBundleType()
	{
		super("MSBUILD_BUNDLE");
	}

	@Override
	public boolean canCreatePredefinedSdks()
	{
		return true;
	}

	@NotNull
	@Override
	public Collection<String> suggestHomePaths()
	{
		if(!SystemInfo.isWindows)
		{
			return Collections.emptyList();
		}

		Map<MicrosoftVisualStudioVersion, String> compilerPaths = new TreeMap<MicrosoftVisualStudioVersion, String>();
		collectVisualStudioCompilerPaths(compilerPaths, "ProgramFiles");
		collectVisualStudioCompilerPaths(compilerPaths, "ProgramFiles(x86)");

		return compilerPaths.values();
	}

	private void collectVisualStudioCompilerPaths(Map<MicrosoftVisualStudioVersion, String> map, String env)
	{
		String programFiles = System.getenv(env);
		if(programFiles != null)
		{
			File msbuildDir = new File(programFiles, "MSBuild");

			for(MicrosoftVisualStudioVersion version : MicrosoftVisualStudioVersion.values())
			{
				File compilerPath = new File(msbuildDir, version.getInternalVersion());
				if(compilerPath.exists())
				{
					map.put(version, compilerPath.getPath());
				}
			}
		}
	}

	@Override
	public boolean isValidSdkHome(String path)
	{
		return new File(getExecutable(path)).exists();
	}

	@Nullable
	@Override
	public String getVersionString(String sdkHome)
	{
		try
		{
			ProcessOutput processOutput = ExecUtil.execAndGetOutput(new GeneralCommandLine(getExecutable(sdkHome), "/version").withWorkDirectory(sdkHome));
			return ContainerUtil.getLastItem(processOutput.getStdoutLines());
		}
		catch(ExecutionException e)
		{
			return "0.0";
		}
	}

	@NotNull
	@Override
	public String getPresentableName()
	{
		return "MSBuild";
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return MSBuildIcons.Msbuild;
	}
}
