/*
 * Copyright 2013-2014 must-be.org
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

package consulo.dotnet.run;

import consulo.annotation.access.RequiredReadAction;
import consulo.content.bundle.Sdk;
import consulo.debugger.XDebugSession;
import consulo.dotnet.compiler.DotNetMacroUtil;
import consulo.dotnet.debugger.DotNetConfigurationWithDebug;
import consulo.dotnet.debugger.DotNetDebugProcessBase;
import consulo.dotnet.debugger.DotNetModuleExtensionWithDebug;
import consulo.dotnet.execution.DebugConnectionInfo;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import consulo.dotnet.run.coverage.DotNetCoverageConfigurationEditor;
import consulo.dotnet.run.coverage.DotNetCoverageEnabledConfiguration;
import consulo.execution.CommonProgramRunConfigurationParameters;
import consulo.execution.configuration.*;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.process.ExecutionException;
import consulo.process.ProcessConsoleType;
import consulo.process.cmd.GeneralCommandLine;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.InvalidDataException;
import consulo.util.xml.serializer.WriteExternalException;
import consulo.util.xml.serializer.XmlSerializer;
import org.jdom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements CommonProgramRunConfigurationParameters, DotNetConfigurationWithCoverage,
		DotNetConfigurationWithDebug, DotNetConfigurationConsoleTypeProvider
{
	private String myProgramParameters;
	private String myWorkingDir;
	private Map<String, String> myEnvsMap = new HashMap<>();
	private boolean myPassParentEnvs = true;
	private ProcessConsoleType myConsoleType = ProcessConsoleType.BUILTIN;

	public DotNetConfiguration(String name, RunConfigurationModule configurationModule, ConfigurationFactory factory)
	{
		super(name, configurationModule, factory);
	}

	public DotNetConfiguration(RunConfigurationModule configurationModule, ConfigurationFactory factory)
	{
		super(configurationModule, factory);
	}

	@Override
	@RequiredReadAction
	public Collection<Module> getValidModules()
	{
		List<Module> list = new ArrayList<>();
		for(Module module : ModuleManager.getInstance(getProject()).getModules())
		{
			if(ModuleUtilCore.getExtension(module, DotNetModuleExtension.class) != null)
			{
				list.add(module);
			}
		}
		return list;
	}

	@Override
	public void readExternal(Element element) throws InvalidDataException
	{
		super.readExternal(element);

		XmlSerializer.deserializeInto(this, element);

		Element coverageElement = element.getChild("coverage");
		if(coverageElement != null)
		{
			CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.get(this);
			coverageEnabledConfiguration.readExternal(coverageElement);
		}
	}

	@Override
	public void writeExternal(Element element) throws WriteExternalException
	{
		super.writeExternal(element);

		XmlSerializer.serializeInto(this, element);

		CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.get(this);
		Element coverageElement = new Element("coverage");
		coverageEnabledConfiguration.writeExternal(coverageElement);
		element.addContent(coverageElement);
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public SettingsEditor<? extends RunConfiguration> getConfigurationEditor()
	{
		SettingsEditorGroup group = new SettingsEditorGroup();
		group.addEditor("General", new DotNetConfigurationEditor(getProject()));
		group.addEditor("Coverage", new DotNetCoverageConfigurationEditor());
		return group;
	}

	@Nullable
	@Override
	public RunProfileState getState(@Nonnull Executor executor, @Nonnull final ExecutionEnvironment executionEnvironment) throws ExecutionException
	{
		Module module = getConfigurationModule().getModule();
		if(module == null)
		{
			throw new ExecutionException("Module is null");
		}

		DotNetRunModuleExtension<?> extension = ModuleUtilCore.getExtension(module, DotNetRunModuleExtension.class);

		if(extension == null)
		{
			throw new ExecutionException("Module don't have .NET extension");
		}

		Sdk sdk = extension.getSdk();
		if(sdk == null)
		{
			throw new ExecutionException("SDK for module is not defined");
		}

		DebugConnectionInfo debugConnectionInfo = null;
		if(executor instanceof DefaultDebugExecutor)
		{
			debugConnectionInfo = new DebugConnectionInfo("127.0.0.1", -1, true);
		}

		DotNetConfiguration runProfile = (DotNetConfiguration) executionEnvironment.getRunProfile();
		GeneralCommandLine runCommandLine = extension.createDefaultCommandLine(sdk, debugConnectionInfo);
		String programParameters = runProfile.getProgramParameters();
		if(!StringUtil.isEmpty(programParameters))
		{
			runCommandLine.addParameters(StringUtil.split(programParameters, " "));
		}
		runCommandLine.withParentEnvironmentType(runProfile.isPassParentEnvs() ? GeneralCommandLine.ParentEnvironmentType.CONSOLE : GeneralCommandLine.ParentEnvironmentType.NONE);
		runCommandLine.getEnvironment().putAll(runProfile.getEnvs());

		String workDir = myWorkingDir;
		if(consulo.util.lang.StringUtil.isEmptyOrSpaces(workDir))
		{
			workDir = DotNetMacroUtil.expandOutputDir(extension);
		}
		runCommandLine.withWorkDirectory(workDir);

		DotNetRunProfileState state = new DotNetRunProfileState(executionEnvironment, runCommandLine);
		if(debugConnectionInfo != null)
		{
			state.putUserData(DotNetRunKeys.DEBUG_CONNECTION_INFO_KEY, debugConnectionInfo);
		}

		return state;
	}

	@Override
	public void setProgramParameters(@Nullable String s)
	{
		myProgramParameters = s;
	}

	@Nullable
	@Override
	public String getProgramParameters()
	{
		return myProgramParameters;
	}

	@Override
	public void setWorkingDirectory(@Nullable String s)
	{
		myWorkingDir = s;
	}

	@Nullable
	@Override
	public String getWorkingDirectory()
	{
		return myWorkingDir;
	}

	@Override
	public void setEnvs(@Nonnull Map<String, String> map)
	{
		myEnvsMap = map;
	}

	@Nonnull
	@Override
	public Map<String, String> getEnvs()
	{
		return myEnvsMap;
	}

	@Override
	public void setPassParentEnvs(boolean b)
	{
		myPassParentEnvs = b;
	}

	@Override
	public boolean isPassParentEnvs()
	{
		return myPassParentEnvs;
	}

	@Override
	public boolean canRun()
	{
		Module module = getConfigurationModule().getModule();
		if(module == null)
		{
			return false;
		}

		DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		if(extension != null && !extension.isAllowDebugInfo())
		{
			return false;
		}
		return extension instanceof DotNetModuleExtensionWithDebug;
	}

	@Nonnull
	@Override
	public DotNetDebugProcessBase createDebuggerProcess(@Nonnull XDebugSession session, @Nonnull DebugConnectionInfo debugConnectionInfo) throws ExecutionException
	{
		Module module = getConfigurationModule().getModule();
		if(module == null)
		{
			throw new ExecutionException("No module information");
		}

		DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		DotNetModuleExtensionWithDebug moduleExtensionWithDebug = extension instanceof DotNetModuleExtensionWithDebug ? (DotNetModuleExtensionWithDebug) extension : null;

		if(moduleExtensionWithDebug == null)
		{
			throw new ExecutionException("Debugger is not supported");
		}
		return moduleExtensionWithDebug.createDebuggerProcess(session, this, debugConnectionInfo);
	}

	public void setConsoleType(ProcessConsoleType consoleType)
	{
		myConsoleType = consoleType;
	}

	@Nonnull
	@Override
	public ProcessConsoleType getConsoleType()
	{
		if(myConsoleType == null)
		{
			myConsoleType = ProcessConsoleType.BUILTIN;
		}
		return myConsoleType;
	}
}
