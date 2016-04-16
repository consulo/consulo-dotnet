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

package org.mustbe.consulo.dotnet.run;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.RequiredDispatchThread;
import org.mustbe.consulo.dotnet.debugger.MonoDebugProcessImpl;
import org.mustbe.consulo.dotnet.debugger.DotNetModuleExtensionWithDebug;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public class DotNetDebuggerProgramRunner extends DefaultProgramRunner
{
	@NotNull
	@Override
	public String getRunnerId()
	{
		return ".NETDebugger";
	}

	@Override
	@RequiredDispatchThread
	protected RunContentDescriptor doExecute(@NotNull final RunProfileState state, @NotNull final ExecutionEnvironment env) throws ExecutionException
	{
		final DebugConnectionInfo debugConnectionInfo;
		if(state instanceof UserDataHolder)
		{
			debugConnectionInfo = ((UserDataHolder) state).getUserData(DotNetRunKeys.DEBUG_CONNECTION_INFO_KEY);
		}
		else
		{
			debugConnectionInfo = null;
		}

		if(debugConnectionInfo == null)
		{
			throw new ExecutionException("No debug connect information");
		}

		DotNetModuleExtensionWithDebug moduleExtensionWithDebug = null;
		RunProfile runProfile = env.getRunProfile();
		if(runProfile instanceof DotNetConfigurationWithCoverage)
		{
			Module module = ((DotNetConfigurationWithCoverage) runProfile).getConfigurationModule().getModule();
			if(module == null)
			{
				throw new ExecutionException("No module information");
			}

			DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
			moduleExtensionWithDebug = extension instanceof DotNetModuleExtensionWithDebug ? (DotNetModuleExtensionWithDebug) extension : null;
		}

		if(moduleExtensionWithDebug == null)
		{
			throw new ExecutionException("Debugger is not supported");
		}

		FileDocumentManager.getInstance().saveAllDocuments();
		final XDebugSession debugSession = XDebuggerManager.getInstance(env.getProject()).startSession(env, new XDebugProcessStarter()
		{
			@NotNull
			@Override
			public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException
			{
				MonoDebugProcessImpl process = new MonoDebugProcessImpl(session, debugConnectionInfo, env.getRunProfile());
				if(!debugConnectionInfo.isServer())
				{
					process.start();
				}

				process.setExecutionResult(state.execute(env.getExecutor(), DotNetDebuggerProgramRunner.this));
				if(debugConnectionInfo.isServer())
				{
					process.start();
				}
				return process;
			}
		});
		return debugSession.getRunContentDescriptor();
	}

	@Override
	public boolean canRun(@NotNull String executorId, @NotNull RunProfile runProfile)
	{
		if(!DefaultDebugExecutor.EXECUTOR_ID.equals(executorId))
		{
			return false;
		}

		if(runProfile instanceof DotNetConfigurationWithCoverage)
		{
			Module module = ((DotNetConfigurationWithCoverage) runProfile).getConfigurationModule().getModule();
			if(module == null)
			{
				return false;
			}

			DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
			if(extension != null && !extension.isAllowDebugInfo())
			{
				return false;
			}
			return true;
		}

		return false;
	}
}
