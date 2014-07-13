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
import org.mustbe.consulo.dotnet.debugger.DotNetDebugProcess;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
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
	protected RunContentDescriptor doExecute(
			Project project, final RunProfileState state, RunContentDescriptor contentToReuse, final ExecutionEnvironment env) throws
			ExecutionException
	{
		assert state instanceof DotNetRunProfileState;
		FileDocumentManager.getInstance().saveAllDocuments();
		final XDebugSession debugSession = XDebuggerManager.getInstance(project).startSession(this, env, contentToReuse, new XDebugProcessStarter()
		{
			@NotNull
			@Override
			public XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException
			{
				DotNetRunProfileState dotNetRunProfileState = (DotNetRunProfileState) state;
				DotNetDebugProcess process = new DotNetDebugProcess(session, dotNetRunProfileState, env.getRunProfile());
				if(!dotNetRunProfileState.getDebugConnectionInfo().isServer())
				{
					process.start();
				}

				process.setExecutionResult(state.execute(env.getExecutor(), DotNetDebuggerProgramRunner.this));
				if(dotNetRunProfileState.getDebugConnectionInfo().isServer())
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
		return DefaultDebugExecutor.EXECUTOR_ID.equals(executorId) && runProfile instanceof DotNetConfiguration;
	}
}
