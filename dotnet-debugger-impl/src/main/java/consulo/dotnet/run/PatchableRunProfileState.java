package consulo.dotnet.run;

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
import consulo.util.dataholder.UserDataHolderBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

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
	private Consumer<ProcessHandler> myProcessHandlerConsumer = Consumer.EMPTY_CONSUMER;

	public PatchableRunProfileState(@Nonnull ExecutionEnvironment executionEnvironment, @Nonnull GeneralCommandLine runCommandLine)
	{
		myExecutionEnvironment = executionEnvironment;
		myOriginalCommandLine = runCommandLine;

		myCommandLineForRun = runCommandLine;
	}

	public void modifyCommandLine(@Nonnull NotNullFunction<GeneralCommandLine, GeneralCommandLine> function)
	{
		myCommandLineForRun = function.fun(myOriginalCommandLine);
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
		myProcessHandlerConsumer.consume(handler);
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
