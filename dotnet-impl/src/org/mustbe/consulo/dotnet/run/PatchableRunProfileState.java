package org.mustbe.consulo.dotnet.run;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.util.Consumer;
import com.intellij.util.NotNullFunction;

/**
 * @author VISTALL
 * @since 11.01.15
 */
public abstract class PatchableRunProfileState implements RunProfileState
{
	protected final ExecutionEnvironment myExecutionEnvironment;
	private final GeneralCommandLine myOriginalCommandLine;

	private GeneralCommandLine myCommandLineForRun;
	@SuppressWarnings("unchecked")
	private Consumer<ProcessHandler> myProcessHandlerConsumer = Consumer.EMPTY_CONSUMER;

	public PatchableRunProfileState(@NotNull ExecutionEnvironment executionEnvironment, @NotNull GeneralCommandLine runCommandLine)
	{
		myExecutionEnvironment = executionEnvironment;
		myOriginalCommandLine = runCommandLine;

		myCommandLineForRun = runCommandLine;
	}

	public void modifyCommandLine(@NotNull NotNullFunction<GeneralCommandLine, GeneralCommandLine> function)
	{
		myCommandLineForRun = function.fun(myOriginalCommandLine);
	}

	@NotNull
	public GeneralCommandLine getCommandLineForRun()
	{
		return myCommandLineForRun;
	}

	@Nullable
	@Override
	public ExecutionResult execute(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException
	{
		if(!new File(myCommandLineForRun.getExePath()).exists())
		{
			throw new ExecutionException(myCommandLineForRun.getExePath() + " is not exists");
		}

		return executeImpl(executor, programRunner);
	}

	@NotNull
	public <T extends ProcessHandler> T patchHandler(@NotNull T handler)
	{
		myProcessHandlerConsumer.consume(handler);
		return handler;
	}

	@Nullable
	public abstract ExecutionResult executeImpl(Executor executor, @NotNull ProgramRunner programRunner) throws ExecutionException;

	public void setProcessHandlerConsumer(@NotNull Consumer<ProcessHandler> processHandlerConsumer)
	{
		myProcessHandlerConsumer = processHandlerConsumer;
	}

	@NotNull
	public ExecutionEnvironment getExecutionEnvironment()
	{
		return myExecutionEnvironment;
	}
}
