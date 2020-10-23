package consulo.execution.console;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import consulo.platform.Platform;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author VISTALL
 * @since 2020-10-23
 */
public enum ConsoleType
{
	BUILTIN(true)
			{
				@Nonnull
				@Override
				public OSProcessHandler createHandler(@Nonnull GeneralCommandLine commandLine) throws ExecutionException
				{
					return ProcessHandlerFactory.getInstance().createProcessHandler(commandLine);
				}
			},
	EXTERNAL_EMULATION(true)
			{
				@Nonnull
				@Override
				public OSProcessHandler createHandler(@Nonnull GeneralCommandLine commandLine) throws ExecutionException
				{
					PtyCommandLine ptyCommandLine = new PtyCommandLine(commandLine);
					OSProcessHandler handler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(ptyCommandLine);
					handler.setHasPty(true);
					return handler;
				}
			},
	EXTERNAL(false)
			{
				@Nonnull
				@Override
				public OSProcessHandler createHandler(@Nonnull GeneralCommandLine commandLine) throws ExecutionException
				{
					Platform.OperatingSystem os = Platform.current().os();
					if(os.isWindows())
					{
						String exePath = commandLine.getExePath();

						commandLine.setExePath("cmd.exe");

						List<String> newArgs = List.of("/c", "start", exePath);

						String[] oldArgs = commandLine.getParametersList().getArray();
						ParametersList parametersList = commandLine.getParametersList();
						parametersList.clearAll();

						commandLine.addParameters(newArgs);
						commandLine.addParameters(oldArgs);

						return ProcessHandlerFactory.getInstance().createProcessHandler(commandLine);
					}
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean isConsoleViewSupported()
				{
					return false;
				}
			};

	private final boolean myAvaliable;

	ConsoleType(boolean avaliable)
	{
		myAvaliable = avaliable;
	}

	public boolean isAvaliable()
	{
		return myAvaliable;
	}

	public boolean isConsoleViewSupported()
	{
		return true;
	}

	@Nonnull
	public abstract OSProcessHandler createHandler(@Nonnull GeneralCommandLine commandLine) throws ExecutionException;
}
