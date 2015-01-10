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
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.consulo.lombok.annotations.LazyInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.run.DotNetConfiguration;
import org.mustbe.consulo.dotnet.run.coverage.DotNetCoverageEnabledConfiguration;
import org.mustbe.consulo.dotnet.run.coverage.DotNetCoverageRunner;
import org.mustbe.consulo.microsoft.dotnet.module.extension.MicrosoftDotNetModuleExtension;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
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
			FileReader reader = new FileReader(sessionDataFile.getAbsolutePath());
			OpenCoverXmlHandler xmlOutputParser = new OpenCoverXmlHandler();
			try
			{
				XMLReader xmlReader = XMLReaderFactory.createXMLReader();
				xmlReader.setEntityResolver(new EntityResolver()
				{
					@Override
					public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
					{
						return new InputSource(new StringReader(""));
					}
				});
				xmlReader.setContentHandler(xmlOutputParser);
				xmlReader.parse(new InputSource(reader));
				return xmlOutputParser.getProjectData();
			}
			finally
			{
				reader.close();
			}
		}
		catch(SAXException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
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
