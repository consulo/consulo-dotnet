package consulo.dotnet.microsoft.debugger;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.xdebugger.XDebugSession;
import consulo.dotnet.debugger.DotNetDebugProcessBase;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class MicrosoftDebuggerProcess extends DotNetDebugProcessBase
{
	private MicrosoftDebuggerClient myClient;

	public MicrosoftDebuggerProcess(@NotNull XDebugSession session, @NotNull RunProfile runProfile, DebugConnectionInfo debugConnectionInfo)
	{
		super(session, runProfile);
		myClient = new MicrosoftDebuggerClient(this, debugConnectionInfo);
	}

	@Override
	public void start()
	{
		myClient.connect();
	}

	@Override
	public void stop()
	{
		myClient.disconnect();

		normalizeBreakpoints();
	}
}
