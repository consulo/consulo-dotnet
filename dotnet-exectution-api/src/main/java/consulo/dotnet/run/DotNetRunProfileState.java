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

import consulo.execution.DefaultExecutionResult;
import consulo.execution.ExecutionResult;
import consulo.execution.configuration.RunProfile;
import consulo.execution.executor.Executor;
import consulo.execution.process.ProcessTerminatedListener;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.runner.ProgramRunner;
import consulo.execution.ui.console.ConsoleView;
import consulo.execution.ui.console.TextConsoleBuilder;
import consulo.execution.ui.console.TextConsoleBuilderFactory;
import consulo.process.ExecutionException;
import consulo.process.ProcessConsoleType;
import consulo.process.ProcessHandler;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.local.ProcessHandlerFactory;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
		GeneralCommandLine commandLineForRun = getCommandLineForRun();

		boolean enableConsole = true;
		ProcessHandler handler;
		RunProfile runProfile = myExecutionEnvironment.getRunProfile();
		if(runProfile instanceof DotNetConfigurationConsoleTypeProvider)
		{
			ProcessConsoleType consoleType = ((DotNetConfigurationConsoleTypeProvider) runProfile).getConsoleType();

			handler = ProcessHandlerFactory.getInstance().createProcessHandler(commandLineForRun, consoleType);
			enableConsole = consoleType.isConsoleViewSupported();
		}
		else
		{
			handler = patchHandler(ProcessHandlerFactory.getInstance().createProcessHandler(commandLineForRun));
		}

		ProcessTerminatedListener.attach(handler, myExecutionEnvironment.getProject());
		ConsoleView console = null;
		if(enableConsole)
		{
			TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(getExecutionEnvironment().getProject());
			console = builder.getConsole();
			console.attachToProcess(handler);
		}
		return new DefaultExecutionResult(console, handler);
	}
}
