package consulo.dotnet.run;

import consulo.execution.ExecutionResult;
import consulo.execution.configuration.RunProfileState;
import consulo.execution.executor.Executor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.runner.ProgramRunner;
import consulo.process.ExecutionException;
import consulo.process.ProcessHandler;
import consulo.process.cmd.GeneralCommandLine;
import consulo.util.dataholder.UserDataHolderBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author VISTALL
 * @since 11.01.15
 */
public abstract class PatchableRunProfileState extends UserDataHolderBase implements RunProfileState
{
	protected final ExecutionEnvironment myExecutionEnvironment;
	private final GeneralCommandLine myOriginalCommandLine;

	private GeneralCommandLine myCommandLineForRun;
	@SuppressWarnings("unchecked")
	private Consumer<ProcessHandler> myProcessHandlerConsumer = processHandler -> {
	};

	public PatchableRunProfileState(@Nonnull ExecutionEnvironment executionEnvironment, @Nonnull GeneralCommandLine runCommandLine)
	{
		myExecutionEnvironment = executionEnvironment;
		myOriginalCommandLine = runCommandLine;

		myCommandLineForRun = runCommandLine;
	}

	public void modifyCommandLine(@Nonnull Function<GeneralCommandLine, GeneralCommandLine> function)
	{
		myCommandLineForRun = function.apply(myOriginalCommandLine);
	}

	@Nonnull
	public GeneralCommandLine getCommandLineForRun()
	{
		return myCommandLineForRun;
	}

	@Nullable
	@Override
	public ExecutionResult execute(Executor executor, @Nonnull ProgramRunner programRunner) throws ExecutionException
	{
		if(!new File(myCommandLineForRun.getExePath()).exists())
		{
			throw new ExecutionException(myCommandLineForRun.getExePath() + " is not exists");
		}

		return executeImpl(executor, programRunner);
	}

	@Nonnull
	public <T extends ProcessHandler> T patchHandler(@Nonnull T handler)
	{
		myProcessHandlerConsumer.accept(handler);
		return handler;
	}

	@Nullable
	public abstract ExecutionResult executeImpl(Executor executor, @Nonnull ProgramRunner programRunner) throws ExecutionException;

	public void setProcessHandlerConsumer(@Nonnull Consumer<ProcessHandler> processHandlerConsumer)
	{
		myProcessHandlerConsumer = processHandlerConsumer;
	}

	@Nonnull
	public ExecutionEnvironment getExecutionEnvironment()
	{
		return myExecutionEnvironment;
	}
}
