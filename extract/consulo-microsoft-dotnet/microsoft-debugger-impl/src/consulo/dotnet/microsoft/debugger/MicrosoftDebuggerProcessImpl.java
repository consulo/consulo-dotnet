package consulo.dotnet.microsoft.debugger;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.xdebugger.XDebugSession;
import consulo.dotnet.debugger.impl.DotNetDebugProcessBase;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public class MicrosoftDebuggerProcessImpl extends DotNetDebugProcessBase
{

	private MicrosoftDebuggerClient myClient;

	public MicrosoftDebuggerProcessImpl(@NotNull XDebugSession session, DebugConnectionInfo debugConnectionInfo)
	{
		super(session);
		myClient = new MicrosoftDebuggerClient(debugConnectionInfo, new MicrosoftDebuggerEventVisitor(this));
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
