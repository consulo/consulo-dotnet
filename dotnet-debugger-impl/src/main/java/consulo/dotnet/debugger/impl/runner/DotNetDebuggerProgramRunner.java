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

package consulo.dotnet.debugger.impl.runner;

import consulo.document.FileDocumentManager;
import consulo.dotnet.debugger.impl.DotNetConfigurationWithDebug;
import consulo.dotnet.debugger.impl.DotNetDebugProcessBase;
import consulo.dotnet.util.DebugConnectionInfo;
import consulo.execution.configuration.RunProfile;
import consulo.execution.configuration.RunProfileState;
import consulo.execution.debug.DefaultDebugExecutor;
import consulo.execution.debug.XDebugSession;
import consulo.execution.debug.XDebuggerManager;
import consulo.execution.runner.DefaultProgramRunner;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.ui.RunContentDescriptor;
import consulo.process.ExecutionException;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.dataholder.UserDataHolder;

import javax.annotation.Nonnull;

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
	@RequiredUIAccess
	protected RunContentDescriptor doExecute(@Nonnull final RunProfileState state, @Nonnull final ExecutionEnvironment env) throws ExecutionException
	{
		final DebugConnectionInfo debugConnectionInfo;
		if(state instanceof UserDataHolder)
		{
			debugConnectionInfo = ((UserDataHolder) state).getUserData(DebugConnectionInfo.KEY);
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
