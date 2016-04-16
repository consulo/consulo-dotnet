package consulo.dotnet.debugger.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetEditorsProvider;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public abstract class DotNetDebugProcessBase extends XDebugProcess
{
	private ExecutionResult myResult;

	public DotNetDebugProcessBase(@NotNull XDebugSession session)
	{
		super(session);
	}

	public abstract void start();

	public void setExecutionResult(ExecutionResult executionResult)
	{
		myResult = executionResult;
	}

	@Override
	public boolean checkCanInitBreakpoints()
	{
		return false;
	}

	@Nullable
	@Override
	protected ProcessHandler doGetProcessHandler()
	{
		return myResult.getProcessHandler();
	}

	@NotNull
	@Override
	public ExecutionConsole createConsole()
	{
		return myResult.getExecutionConsole();
	}

	@NotNull
	@Override
	public XDebuggerEditorsProvider getEditorsProvider()
	{
		return new DotNetEditorsProvider(getSession());
	}

	@Override
	public void startStepOver()
	{

	}

	@Override
	public void startStepInto()
	{

	}

	@Override
	public void startStepOut()
	{

	}

	@Override
	public void resume()
	{

	}

	@Override
	public void runToPosition(@NotNull XSourcePosition position)
	{

	}
}
