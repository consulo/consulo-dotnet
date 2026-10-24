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

package consulo.dotnet.run.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.content.bundle.Sdk;
import consulo.dotnet.compiler.DotNetMacroUtil;
import consulo.dotnet.debugger.impl.DotNetConfigurationWithDebug;
import consulo.dotnet.debugger.impl.DotNetDebugProcess;
import consulo.dotnet.debugger.impl.DotNetModuleExtensionWithDebug;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;
import consulo.dotnet.run.DotNetConfigurationConsoleTypeProvider;
import consulo.dotnet.run.DotNetRunProfileState;
import consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import consulo.dotnet.util.DebugConnectionInfo;
import consulo.execution.CommonProgramRunConfigurationParameters;
import consulo.execution.configuration.*;
import consulo.execution.configuration.fragment.EnvironmentFragmentState;
import consulo.execution.configuration.fragment.ProgramParametersFragmentState;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.WorkingDirectoryFragmentState;
import consulo.execution.debug.DefaultDebugExecutor;
import consulo.execution.debug.XDebugSession;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.process.ExecutionException;
import consulo.process.ProcessConsoleType;
import consulo.process.cmd.GeneralCommandLine;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements CommonProgramRunConfigurationParameters,
    DotNetConfigurationWithCoverage,
    DotNetConfigurationWithDebug,
    DotNetConfigurationConsoleTypeProvider {
    public DotNetConfiguration(String name, RunConfigurationModule configurationModule, ConfigurationFactory factory) {
        super(name, configurationModule, factory);
    }

    public DotNetConfiguration(RunConfigurationModule configurationModule, ConfigurationFactory factory) {
        super(configurationModule, factory);
    }

    @Override
    @RequiredReadAction
    public Collection<Module> getValidModules() {
        List<Module> list = new ArrayList<>();
        for (Module module : ModuleManager.getInstance(getProject()).getModules()) {
            if (ModuleUtilCore.getExtension(module, DotNetModuleExtension.class) != null) {
                list.add(module);
            }
        }
        return list;
    }

    @Override
    protected Class<DotNetConfigurationOptions> getOptionsClass() {
        return DotNetConfigurationOptions.class;
    }

    @Override
    public DotNetConfigurationOptions getOptions() {
        return (DotNetConfigurationOptions) super.getOptions();
    }

    @Override
    protected void convertLegacyState(LegacyRunConfigurationConverter converter) {
        super.convertLegacyState(converter);

        DotNetConfigurationLegacyState old = converter.readXml(DotNetConfigurationLegacyState.class);

        DotNetConfigurationOptions options = new DotNetConfigurationOptions();
        ProcessConsoleType consoleType = old.getConsoleType();
        options.setConsoleType(consoleType == null || consoleType == ProcessConsoleType.BUILTIN ? null : consoleType.name());
        converter.setOptions(options);

        converter.addFragment(new ProgramParametersFragmentState(old.getProgramParameters()));
        converter.addFragment(new WorkingDirectoryFragmentState(old.getWorkingDirectory()));
        converter.addFragment(new EnvironmentFragmentState(old.getEnvs(), old.isPassParentEnvs()));
    }

    @Override
    public List<RunConfigurationFragment<?>> createFragments() {
        return List.of(new DotNetConsoleTypeFragment());
    }

    @Nullable
    @Override
    public RunProfileState getState(Executor executor, final ExecutionEnvironment executionEnvironment) throws ExecutionException {
        Module module = getConfigurationModule().getModule();
        if (module == null) {
            throw new ExecutionException("Module is null");
        }

        DotNetRunModuleExtension<?> extension = ModuleUtilCore.getExtension(module, DotNetRunModuleExtension.class);

        if (extension == null) {
            throw new ExecutionException("Module don't have .NET extension");
        }

        Sdk sdk = extension.getSdk();
        if (sdk == null) {
            throw new ExecutionException("SDK for module is not defined");
        }

        DebugConnectionInfo debugConnectionInfo = null;
        if (executor instanceof DefaultDebugExecutor) {
            debugConnectionInfo = new DebugConnectionInfo("127.0.0.1", -1, true);
        }

        DotNetConfiguration runProfile = (DotNetConfiguration) executionEnvironment.getRunProfile();
        GeneralCommandLine runCommandLine = extension.createDefaultCommandLine(sdk, debugConnectionInfo);
        String programParameters = runProfile.getProgramParameters();
        if (!StringUtil.isEmpty(programParameters)) {
            runCommandLine.addParameters(StringUtil.split(programParameters, " "));
        }
        runCommandLine.withParentEnvironmentType(runProfile.isPassParentEnvs() ? GeneralCommandLine.ParentEnvironmentType.CONSOLE : GeneralCommandLine.ParentEnvironmentType.NONE);
        runCommandLine.getEnvironment().putAll(runProfile.getEnvs());

        String workDir = getWorkingDirectory();
        if (consulo.util.lang.StringUtil.isEmptyOrSpaces(workDir)) {
            workDir = DotNetMacroUtil.expandOutputDir(extension);
        }
        runCommandLine.withWorkDirectory(workDir);

        DotNetRunProfileState state = new DotNetRunProfileState(executionEnvironment, runCommandLine);
        if (debugConnectionInfo != null) {
            state.putUserData(DebugConnectionInfo.KEY, debugConnectionInfo);
        }

        return state;
    }

    @Override
    public boolean canRun() {
        Module module = getConfigurationModule().getModule();
        if (module == null) {
            return false;
        }

        DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
        if (extension != null && !extension.isAllowDebugInfo()) {
            return false;
        }
        return extension instanceof DotNetModuleExtensionWithDebug;
    }

    @Override
    public DotNetDebugProcess createDebuggerProcess(XDebugSession session, DebugConnectionInfo debugConnectionInfo) throws ExecutionException {
        Module module = getConfigurationModule().getModule();
        if (module == null) {
            throw new ExecutionException("No module information");
        }

        DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
        DotNetModuleExtensionWithDebug moduleExtensionWithDebug = extension instanceof DotNetModuleExtensionWithDebug ? (DotNetModuleExtensionWithDebug) extension : null;

        if (moduleExtensionWithDebug == null) {
            throw new ExecutionException("Debugger is not supported");
        }
        return moduleExtensionWithDebug.createDebuggerProcess(session, this, debugConnectionInfo);
    }

    public void setConsoleType(@Nullable ProcessConsoleType consoleType) {
        getOptions().setConsoleType(consoleType == null || consoleType == ProcessConsoleType.BUILTIN ? null : consoleType.name());
    }

    @Override
    public ProcessConsoleType getConsoleType() {
        String name = getOptions().getConsoleType();
        if (name != null) {
            for (ProcessConsoleType type : ProcessConsoleType.values()) {
                if (type.name().equals(name)) {
                    return type;
                }
            }
        }
        return ProcessConsoleType.BUILTIN;
    }
}
