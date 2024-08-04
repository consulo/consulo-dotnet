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
import consulo.execution.ExecutionBundle;
import consulo.execution.configuration.*;
import consulo.execution.configuration.log.ui.LogConfigurationPanel;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.execution.configuration.ui.SettingsEditorGroup;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.module.Module;
import consulo.process.ExecutionException;
import consulo.project.Project;
import consulo.util.xml.serializer.DefaultJDOMExternalizer;
import consulo.util.xml.serializer.InvalidDataException;
import consulo.util.xml.serializer.WriteExternalException;
import org.jdom.Element;

import jakarta.annotation.Nonnull;
import java.util.Collection;

/**
 * @author VISTALL
 * @since 27-Dec-16
 */
public abstract class DotNetRemoteConfiguration extends ModuleBasedConfiguration<RunConfigurationModule> implements RunConfigurationWithSuppressedDefaultRunAction, CompileStepBeforeRun.Suppressor,
		RemoteRunProfile, DotNetConfigurationWithDebug
{
	public boolean SERVER_MODE;
	public String HOST;
	public int PORT;

	public DotNetRemoteConfiguration(final Project project, ConfigurationFactory configurationFactory)
	{
		super(new RunConfigurationModule(project), configurationFactory);
	}

	@Override
	public void writeExternal(final Element element) throws WriteExternalException
	{
		super.writeExternal(element);
		DefaultJDOMExternalizer.writeExternal(this, element);
	}

	@Override
	public void readExternal(final Element element) throws InvalidDataException
	{
		super.readExternal(element);
		DefaultJDOMExternalizer.readExternal(this, element);
	}

	@Override
	public RunProfileState getState(@Nonnull final Executor executor, @Nonnull final ExecutionEnvironment env) throws ExecutionException
	{
		DotNetRemoteRunState state = new DotNetRemoteRunState(env);
		state.putUserData(DebugConnectionInfo.KEY, new DebugConnectionInfo(HOST, PORT, SERVER_MODE));
		return state;
	}

	@Override
	@Nonnull
	public SettingsEditor<? extends DotNetRemoteConfiguration> getConfigurationEditor()
	{
		SettingsEditorGroup<DotNetRemoteConfiguration> group = new SettingsEditorGroup<>();
		group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), new DotNetRemoteConfigurable<>(getProject()));
		group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<>());
		return group;
	}

	@Override
	public Collection<Module> getValidModules()
	{
		return getAllModules();
	}
}
