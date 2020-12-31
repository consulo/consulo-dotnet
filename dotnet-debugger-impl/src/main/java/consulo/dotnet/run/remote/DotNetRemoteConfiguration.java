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

package consulo.dotnet.run.remote;

import com.intellij.compiler.options.CompileStepBeforeRun;
import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import consulo.dotnet.debugger.DotNetConfigurationWithDebug;
import consulo.dotnet.execution.DebugConnectionInfo;
import consulo.dotnet.run.DotNetRunKeys;
import org.jdom.Element;

import javax.annotation.Nonnull;
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
		state.putUserData(DotNetRunKeys.DEBUG_CONNECTION_INFO_KEY, new DebugConnectionInfo(HOST, PORT, SERVER_MODE));
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
