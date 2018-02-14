/*
 * Copyright 2013-2016 consulo.io
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

package consulo.dotnet.mono.run;

import javax.annotation.Nonnull;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.actions.StopProcessAction;
import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import consulo.dotnet.debugger.DotNetDebugProcessBase;
import consulo.dotnet.execution.DebugConnectionInfo;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.dotnet.mono.debugger.MonoDebugProcess;
import consulo.dotnet.mono.debugger.MonoVirtualMachineListener;
import consulo.dotnet.run.remote.DotNetRemoteConfiguration;
import consulo.module.extension.ModuleExtensionHelper;
import mono.debugger.VirtualMachine;

/**
 * @author VISTALL
 * @since 27-Dec-16
 */
public class MonoRemoteConfiguration extends ConfigurationTypeBase
{
	public MonoRemoteConfiguration()
	{
		super("MonoRemoteConfiguration", "Mono Remote", "", AllIcons.RunConfigurations.Remote);

		addFactory(new ConfigurationFactoryEx(this)
		{
			@Override
			public RunConfiguration createTemplateConfiguration(Project project)
			{
				return new DotNetRemoteConfiguration(project, this)
				{
					@Nonnull
					@Override
					public DotNetDebugProcessBase createDebuggerProcess(@Nonnull XDebugSession session, @Nonnull DebugConnectionInfo info) throws ExecutionException
					{
						MonoDebugProcess process = new MonoDebugProcess(session, this, info);
						process.getDebugThread().addListener(new MonoVirtualMachineListener()
						{
							@Override
							public void connectionSuccess(@Nonnull VirtualMachine machine)
							{
								ProcessHandler processHandler = process.getProcessHandler();
								processHandler.notifyTextAvailable(String.format("Success attach to %s:%d", info.getHost(), info.getPort()), ProcessOutputTypes.STDOUT);
							}

							@Override
							public void connectionStopped()
							{
							}

							@Override
							public void connectionFailed()
							{
								ProcessHandler processHandler = process.getProcessHandler();
								processHandler.notifyTextAvailable(String.format("Failed attach to %s:%d", info.getHost(), info.getPort()), ProcessOutputTypes.STDERR);
								StopProcessAction.stopProcess(processHandler);
							}
						});
						return process;
					}
				};
			}

			@Override
			public boolean isApplicable(@Nonnull Project project)
			{
				return ModuleExtensionHelper.getInstance(project).hasModuleExtension(DotNetModuleExtension.class);
			}
		});
	}
}
