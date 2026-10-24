/*
 * Copyright 2013-2016 must-be.org
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
package consulo.dotnet.debugger.impl.runner.remote;

import consulo.compiler.execution.CompileStepBeforeRun;
import consulo.dotnet.debugger.impl.DotNetConfigurationWithDebug;
import consulo.dotnet.util.DebugConnectionInfo;
import consulo.execution.configuration.*;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.module.Module;
import consulo.process.ExecutionException;
import consulo.project.Project;

import java.util.Collection;
import java.util.List;

/**
 * @author VISTALL
 * @since 27-Dec-16
 */
public abstract class DotNetRemoteConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements RunConfigurationWithSuppressedDefaultRunAction, CompileStepBeforeRun.Suppressor,
    RemoteRunProfile, DotNetConfigurationWithDebug {
    public DotNetRemoteConfiguration(final Project project, ConfigurationFactory configurationFactory) {
        super(new RunConfigurationModule(project), configurationFactory);
    }

    @Override
    protected Class<DotNetRemoteConfigurationOptions> getOptionsClass() {
        return DotNetRemoteConfigurationOptions.class;
    }

    @Override
    public DotNetRemoteConfigurationOptions getOptions() {
        return (DotNetRemoteConfigurationOptions) super.getOptions();
    }

    @Override
    protected void convertLegacyState(LegacyRunConfigurationConverter converter) {
        super.convertLegacyState(converter);

        DotNetRemoteConfigurationLegacyState old = converter.readXml(DotNetRemoteConfigurationLegacyState.class);

        DotNetRemoteConfigurationOptions options = new DotNetRemoteConfigurationOptions();
        options.setServerMode(old.SERVER_MODE);
        options.setHost(old.HOST);
        options.setPort(old.PORT);
        converter.setOptions(options);
    }

    @Override
    public List<RunConfigurationFragment<?>> createFragments() {
        return List.of(new DotNetRemoteConnectionFragment());
    }

    @Override
    public RunProfileState getState(final Executor executor, final ExecutionEnvironment env) throws ExecutionException {
        DotNetRemoteConfigurationOptions options = getOptions();
        DotNetRemoteRunState state = new DotNetRemoteRunState(env);
        state.putUserData(DebugConnectionInfo.KEY, new DebugConnectionInfo(options.getHost(), options.getPort(), options.isServerMode()));
        return state;
    }

    @Override
    public Collection<Module> getValidModules() {
        return getAllModules();
    }
}
