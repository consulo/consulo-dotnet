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

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetRunProfileState extends PatchableRunProfileState
{
	public DotNetRunProfileState(@Nonnull ExecutionEnvironment executionEnvironment, @Nonnull GeneralCommandLine runCommandLine)
	{
		super(executionEnvironment, runCommandLine);
	}

	@Nullable
	@Override
	public ExecutionResult executeImpl(Executor executor, @Nonnull ProgramRunner programRunner) throws ExecutionException
	{
		TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(getExecutionEnvironment().getProject());
		GeneralCommandLine commandLineForRun = getCommandLineForRun();
		OSProcessHandler handler = patchHandler(ProcessHandlerFactory.getInstance().createProcessHandler(new PtyCommandLine(commandLineForRun)));
		handler.setHasPty(true);
		ProcessTerminatedListener.attach(handler, myExecutionEnvironment.getProject());

		ConsoleView console = builder.getConsole();
		console.attachToProcess(handler);
		return new DefaultExecutionResult(console, handler);
	}
}
