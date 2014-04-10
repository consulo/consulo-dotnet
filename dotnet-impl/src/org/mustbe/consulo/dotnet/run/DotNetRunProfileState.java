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

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import lombok.val;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public class DotNetRunProfileState implements RunProfileState
{
	private final String myExeFile;
	private final ExecutionEnvironment myExecutionEnvironment;
	private final GeneralCommandLine myRunCommandLine;
	private final DebugConnectionInfo myDebugConnectionInfo;

	public DotNetRunProfileState(
			String exeFile, ExecutionEnvironment executionEnvironment, GeneralCommandLine runCommandLine, DebugConnectionInfo debugConnectionInfo)
	{
		myExeFile = exeFile;
		myExecutionEnvironment = executionEnvironment;
		myRunCommandLine = runCommandLine;
		myDebugConnectionInfo = debugConnectionInfo;
	}

	@Nullable
	@Override
	public ExecutionResult execute(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException
	{
		if(!new File(getExeFile()).exists())
		{
			throw new ExecutionException(getExeFile() + " is not exists");
		}

		val builder = TextConsoleBuilderFactory.getInstance().createBuilder(getExecutionEnvironment().getProject());

		OSProcessHandler osProcessHandler = new OSProcessHandler(getRunCommandLine());
		ConsoleView console = builder.getConsole();
		console.attachToProcess(osProcessHandler);
		return new DefaultExecutionResult(console, osProcessHandler);
	}

	public DebugConnectionInfo getDebugConnectionInfo()
	{
		return myDebugConnectionInfo;
	}

	public String getExeFile()
	{
		return myExeFile;
	}

	public ExecutionEnvironment getExecutionEnvironment()
	{
		return myExecutionEnvironment;
	}

	public GeneralCommandLine getRunCommandLine()
	{
		return myRunCommandLine;
	}
}
