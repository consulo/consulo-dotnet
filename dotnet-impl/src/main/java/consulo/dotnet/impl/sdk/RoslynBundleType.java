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

package consulo.dotnet.impl.sdk;

import consulo.content.bundle.SdkType;
import consulo.dotnet.icon.DotNetIconGroup;
import consulo.logging.Logger;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.local.CapturingProcessHandler;
import consulo.process.local.ProcessOutput;
import consulo.ui.image.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

/**
 * @author VISTALL
 * @since 08.06.2015
 */
public class RoslynBundleType extends SdkType
{
	private static final Logger LOG = Logger.getInstance(RoslynBundleType.class);

	@Nonnull
	public static RoslynBundleType getInstance()
	{
		return EP_NAME.findExtensionOrFail(RoslynBundleType.class);
	}

	public RoslynBundleType()
	{
		super("ROSLYN_BUNDLE");
	}

	@Override
	public boolean isValidSdkHome(String path)
	{
		return new File(path, "csc.exe").exists();
	}

	@Nullable
	@Override
	public String getVersionString(String sdkHome)
	{
		GeneralCommandLine commandLine = new GeneralCommandLine();
		commandLine.setExePath(sdkHome + "/" + "csc.exe");
		commandLine.addParameter("/version");

		try
		{
			ProcessOutput processOutput = new CapturingProcessHandler(commandLine).runProcess();
			String version = processOutput.getStdout().trim();
			// 3.0.1-dev (dev-build)
			int i = version.indexOf("(");
			if(i != -1)
			{
				version = version.substring(0, i);
				version = version.trim();
			}
			return version;
		}
		catch(ExecutionException e)
		{
			LOG.error(e);
		}
		return "?";
	}

	@Override
	public String suggestSdkName(String currentSdkName, String sdkHome)
	{
		return getPresentableName() + " " + getVersionString(sdkHome);
	}

	@Nonnull
	@Override
	public String getPresentableName()
	{
		return "Roslyn";
	}

	@Nullable
	@Override
	public Image getIcon()
	{
		return DotNetIconGroup.netfoundation();
	}
}
