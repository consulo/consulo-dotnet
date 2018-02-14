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

import javax.annotation.Nonnull;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import consulo.annotations.RequiredDispatchThread;
import consulo.dotnet.debugger.DotNetConfigurationWithDebug;
import consulo.dotnet.debugger.DotNetDebugProcessBase;
import consulo.dotnet.execution.DebugConnectionInfo;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public class DotNetDebuggerProgramRunner extends DefaultProgramRunner
{
	@Nonnull
	@Override
	public String getRunnerId()
	{
		return ".NETDebugger";
	}

	@Override
	@RequiredDispatchThread
	protected RunContentDescriptor doExecute(@Nonnull final RunProfileState state, @Nonnull final ExecutionEnvironment env) throws ExecutionException
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

		final DotNetConfigurationWithDebug configurationWithDebug;
		if(env.getRunProfile() instanceof DotNetConfigurationWithDebug)
		{
			configurationWithDebug = (DotNetConfigurationWithDebug) env.getRunProfile();
		}
		else
		{
			throw new ExecutionException("Debugger is not supported");
		}

		FileDocumentManager.getInstance().saveAllDocuments();
		XDebugSession debugSession = XDebuggerManager.getInstance(env.getProject()).startSession(env, session ->
		{
			DotNetDebugProcessBase process = configurationWithDebug.createDebuggerProcess(session, debugConnectionInfo);
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
		});
		return debugSession.getRunContentDescriptor();
	}

	@Override
	public boolean canRun(@Nonnull String executorId, @Nonnull RunProfile runProfile)
	{
		if(!DefaultDebugExecutor.EXECUTOR_ID.equals(executorId))
		{
			return false;
		}

		if(runProfile instanceof DotNetConfigurationWithDebug)
		{
			return ((DotNetConfigurationWithDebug) runProfile).canRun();
		}

		return false;
	}
}
