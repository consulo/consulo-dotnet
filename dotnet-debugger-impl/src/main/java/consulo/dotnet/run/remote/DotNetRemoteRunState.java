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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.xdebugger.DefaultDebugProcessHandler;

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
	public ExecutionResult execute(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException
	{
		TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(myEnv.getProject());
		ProcessHandler handler = new DefaultDebugProcessHandler();

		ConsoleView console = builder.getConsole();
		console.attachToProcess(handler);
		return new DefaultExecutionResult(console, handler);
	}
}
