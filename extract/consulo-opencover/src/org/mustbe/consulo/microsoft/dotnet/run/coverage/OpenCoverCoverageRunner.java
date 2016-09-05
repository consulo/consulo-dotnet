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

import gnu.trove.TIntObjectHashMap;

import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import org.mustbe.consulo.dotnet.run.coverage.DotNetCoverageEnabledConfiguration;
import org.mustbe.consulo.dotnet.run.coverage.DotNetCoverageRunner;
import org.mustbe.consulo.microsoft.dotnet.module.extension.MicrosoftDotNetModuleExtension;
import com.intellij.coverage.CoverageSuite;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.rt.coverage.data.ClassData;
import com.intellij.rt.coverage.data.LineCoverage;
import com.intellij.rt.coverage.data.LineData;
import com.intellij.rt.coverage.data.ProjectData;
import com.intellij.util.execution.ParametersListUtil;
import consulo.lombok.annotations.Lazy;
import consulo.util.NotNullPairFunction;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class OpenCoverCoverageRunner extends DotNetCoverageRunner
{
	@NotNull
	@Lazy
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
						TIntObjectHashMap<String> filePaths = new TIntObjectHashMap<String>();
						CoverageSession.File[] files = module.Files == null ? null : module.Files.Files;
						if(files != null)
						{
							for(CoverageSession.File file : files)
							{
								filePaths.put(file.UId, file.FullPath);
							}
						}

						for(CoverageSession.Class aClass : classes1)
						{
							ClassData classData = projectData.getOrCreateClassData(aClass.FullName);

							SortedMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
							CoverageSession.Method[] methods = aClass.Methods == null ? null : aClass.Methods.Methods;
							if(methods != null)
							{
								for(CoverageSession.Method method : methods)
								{
									CoverageSession.SequencePoints sequencePoints = method.SequencePoints;
									if(sequencePoints != null)
									{
										CoverageSession.SequencePoint[] points = sequencePoints.Points;
										if(points != null)
										{
											for(CoverageSession.SequencePoint point : points)
											{
												for(int i = point.StartLine; i <= point.EndLine; i++)
												{
													Integer count = map.get(i);
													if(count == null)
													{
														map.put(i, point.VisitCount);
													}
													else
													{
														map.put(i, count + point.VisitCount);
													}
												}

												int fileUId = point.FileUId;
												String filePath = filePaths.get(fileUId);
												if(filePath != null)
												{
													classData.setSource(filePath);
												}
											}
										}
									}
								}
							}

							if(!map.isEmpty())
							{
								LineData[] lineDatas = new LineData[map.lastKey() + 1];
								for(int i = 0; i < lineDatas.length; i++)
								{
									Integer invokeCount = map.get(i);
									if(invokeCount != null)
									{
										LineData lineData = new LineData(i, "");
										if(invokeCount != 0)
										{
											lineData.setStatus(LineCoverage.FULL);
										}
										else
										{
											lineData.setStatus(LineCoverage.PARTIAL);
										}

										lineData.setHits(invokeCount);
										lineDatas[i] = lineData;
									}
								}
								classData.setLines(lineDatas);
							}
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
	public NotNullPairFunction<DotNetConfigurationWithCoverage, GeneralCommandLine, GeneralCommandLine> getModifierForCommandLine()
	{
		return new NotNullPairFunction<DotNetConfigurationWithCoverage, GeneralCommandLine, GeneralCommandLine>()
		{
			@NotNull
			@Override
			public GeneralCommandLine fun(DotNetConfigurationWithCoverage t, GeneralCommandLine v)
			{
				CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.get(t);

				File openCoverConsoleExecutable = getOpenCoverConsoleExecutable();

				GeneralCommandLine newCommandLine = new GeneralCommandLine();
				newCommandLine.setExePath(openCoverConsoleExecutable.getPath());
				newCommandLine.addParameter("-register:user");
				newCommandLine.addParameter("-target:" + v.getExePath());
				newCommandLine.addParameter("-filter:+[*]*");
				newCommandLine.addParameter("-output:" + coverageEnabledConfiguration.getCoverageFilePath());

				String parametersAsString = ParametersListUtil.join(v.getParametersList().getParameters());
				if(!StringUtil.isEmpty(parametersAsString))
				{
					newCommandLine.addParameter("-targetargs:" + parametersAsString + "");
				}
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
