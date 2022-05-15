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

import consulo.execution.DefaultExecutionResult;
import consulo.execution.ExecutionResult;
import consulo.execution.configuration.RunProfileState;
import consulo.execution.debug.DefaultDebugProcessHandler;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.runner.ProgramRunner;
import consulo.execution.ui.console.ConsoleView;
import consulo.execution.ui.console.TextConsoleBuilder;
import consulo.execution.ui.console.TextConsoleBuilderFactory;
import consulo.process.ExecutionException;
import consulo.process.ProcessHandler;
import consulo.util.dataholder.UserDataHolderBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 27-Dec-16
 */
public class DotNetRemoteRunState extends UserDataHolderBase implements RunProfileState
{
	private ExecutionEnvironment myEnv;

	public DotNetRemoteRunState(ExecutionEnvironment env)
	{
		myEnv = env;
	}

	@Nullable
	@Override
	public ExecutionResult execute(Executor executor, @Nonnull ProgramRunner programRunner) throws ExecutionException
	{
		TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(myEnv.getProject());
		ProcessHandler handler = new DefaultDebugProcessHandler();

		ConsoleView console = builder.getConsole();
		console.attachToProcess(handler);
		return new DefaultExecutionResult(console, handler);
	}
}
