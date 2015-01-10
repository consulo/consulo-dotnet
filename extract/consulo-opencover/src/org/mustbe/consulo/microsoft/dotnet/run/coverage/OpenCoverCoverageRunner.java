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

package org.mustbe.consulo.microsoft.dotnet.run.coverage;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.consulo.lombok.annotations.LazyInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.run.DotNetConfiguration;
import org.mustbe.consulo.dotnet.run.coverage.DotNetCoverageEnabledConfiguration;
import org.mustbe.consulo.dotnet.run.coverage.DotNetCoverageRunner;
import org.mustbe.consulo.dotnet.run.coverage.hack.HackClassData;
import org.mustbe.consulo.microsoft.dotnet.module.extension.MicrosoftDotNetModuleExtension;
import com.intellij.coverage.CoverageSuite;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.rt.coverage.data.ProjectData;
import com.intellij.util.NotNullPairFunction;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class OpenCoverCoverageRunner extends DotNetCoverageRunner
{
	@NotNull
	@LazyInstance
	public static File getOpenCoverConsoleExecutable()
	{
		PluginClassLoader classLoader = (PluginClassLoader) OpenCoverCoverageRunner.class.getClassLoader();
		IdeaPluginDescriptor plugin = PluginManager.getPlugin(classLoader.getPluginId());
		assert plugin != null;
		return new File(new File(plugin.getPath(), "OpenCover"), "OpenCover.Console.exe");
	}

	@Override
	public ProjectData loadCoverageData(@NotNull File sessionDataFile, @Nullable CoverageSuite baseCoverageSuite)
	{
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(CoverageSession.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			CoverageSession unmarshal = (CoverageSession) unmarshaller.unmarshal(sessionDataFile);

			ProjectData projectData = new ProjectData();
			CoverageSession.Modules modules = unmarshal.Modules;
			if(modules != null)
			{
				CoverageSession.Module[] modules2 = modules.Modules;
				if(modules2 != null)
				{
					for(CoverageSession.Module module : modules2)
					{
						CoverageSession.Classes classes = module.Classes;
						if(classes == null)
						{
							continue;
						}
						CoverageSession.Class[] classes1 = classes.Classes;
						if(classes1 == null)
						{
							continue;
						}

						for(CoverageSession.Class aClass : classes1)
						{
							HackClassData classData = HackClassData.getOrCreateClassData(projectData, aClass.FullName);

							classData.setSequenceCoverage(aClass.Summary.sequenceCoverage);
						}
					}
				}
			}
			return projectData;
		}
		catch(JAXBException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getPresentableName()
	{
		return "OpenCover";
	}

	@Override
	public String getDataFileExtension()
	{
		return "xml";
	}

	@Override
	public String getId()
	{
		return "OpenCoverDotNetCoverageRunner";
	}

	@NotNull
	@Override
	public NotNullPairFunction<DotNetConfiguration, GeneralCommandLine, GeneralCommandLine> getModifierForCommandLine()
	{
		return new NotNullPairFunction<DotNetConfiguration, GeneralCommandLine, GeneralCommandLine>()
		{
			@NotNull
			@Override
			public GeneralCommandLine fun(DotNetConfiguration t, GeneralCommandLine v)
			{
				CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.getOrCreate(t);

				File openCoverConsoleExecutable = getOpenCoverConsoleExecutable();

				GeneralCommandLine newCommandLine = new GeneralCommandLine();
				newCommandLine.setExePath(openCoverConsoleExecutable.getPath());
				newCommandLine.addParameter("-register:user");
				newCommandLine.addParameter("-target:" + v.getExePath());
				newCommandLine.addParameter("-filter:+[*]*");
				newCommandLine.addParameter("-output:" + coverageEnabledConfiguration.getCoverageFilePath());
				return newCommandLine;
			}
		};
	}

	@Override
	public boolean acceptModuleExtension(@NotNull DotNetModuleExtension<?> moduleExtension)
	{
		return moduleExtension instanceof MicrosoftDotNetModuleExtension;
	}
}