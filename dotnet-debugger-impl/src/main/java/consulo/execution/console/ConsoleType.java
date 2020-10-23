package consulo.execution.console;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.openapi.util.SystemInfo;
import consulo.container.boot.ContainerPathManager;
import consulo.localize.LocalizeValue;
import consulo.platform.Platform;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;

/**
 * @author VISTALL
 * @since 2020-10-23
 */
public enum ConsoleType
{
	BUILTIN("Builtin", true)
			{
				@Nonnull
				@Override
				public ProcessHandler createHandler(@Nonnull GeneralCommandLine commandLine) throws ExecutionException
				{
					return ProcessHandlerFactory.getInstance().createProcessHandler(commandLine);
				}
			},
	EXTERNAL_EMULATION("Builtin with external emulation", true)
			{
				@Nonnull
				@Override
				public ProcessHandler createHandler(@Nonnull GeneralCommandLine commandLine) throws ExecutionException
				{
					PtyCommandLine ptyCommandLine = new PtyCommandLine(commandLine);
					OSProcessHandler handler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(ptyCommandLine);
					handler.setHasPty(true);
					return handler;
				}
			},
	EXTERNAL("External", Platform.current().os().isWindows())
			{
				@Nonnull
				@Override
				public ProcessHandler createHandler(@Nonnull GeneralCommandLine commandLine) throws ExecutionException
				{
					Platform.OperatingSystem os = Platform.current().os();
					if(os.isWindows())
					{
						String exePath = commandLine.getExePath();

						commandLine.setExePath(getRunnerPath());

						List<String> newArgs = List.of("/c", exePath);

						String[] oldArgs = commandLine.getParametersList().getArray();
						ParametersList parametersList = commandLine.getParametersList();
						parametersList.clearAll();

						commandLine.addParameters(newArgs);
						commandLine.addParameters(oldArgs);

						return ProcessHandlerFactory.getInstance().createProcessHandler(commandLine);
					}
					throw new UnsupportedOperationException();
				}

				@Nonnull
				private String getRunnerPath()
				{
					if(!SystemInfo.isWindows)
					{
						throw new IllegalStateException("There is no need of runner under unix based OS");
					}

					File runnerw = new File(ContainerPathManager.get().getBinPath(), SystemInfo.is64Bit ? "runnerw64.exe" : "runnerw.exe");
					return runnerw.getPath();
				}

				@Override
				public boolean isConsoleViewSupported()
				{
					return false;
				}
			};

	private final LocalizeValue myDisplayName;
	private final boolean myAvaliable;

	ConsoleType(String displayName, boolean avaliable)
	{
		myDisplayName = LocalizeValue.of(displayName);
		myAvaliable = avaliable;
	}

	public LocalizeValue getDisplayName()
	{
		return myDisplayName;
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
	public abstract ProcessHandler createHandler(@Nonnull GeneralCommandLine commandLine) throws ExecutionException;
}
