package consulo.dotnet.debugger;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import consulo.dotnet.debugger.breakpoint.DotNetExceptionBreakpointType;
import consulo.dotnet.debugger.breakpoint.DotNetLineBreakpointType;
import consulo.dotnet.debugger.breakpoint.properties.DotNetExceptionBreakpointProperties;
import consulo.dotnet.debugger.breakpoint.properties.DotNetLineBreakpointProperties;
import consulo.dotnet.debugger.proxy.DotNetVirtualMachineProxy;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public abstract class DotNetDebugProcessBase extends XDebugProcess
{
	private ExecutionResult myResult;
	private final RunProfile myRunProfile;
	protected final XDebuggerManager myDebuggerManager;

	public DotNetDebugProcessBase(@NotNull XDebugSession session, @NotNull RunProfile runProfile)
	{
		super(session);

		myRunProfile = runProfile;
		myDebuggerManager = XDebuggerManager.getInstance(session.getProject());
	}

	@NotNull
	public DotNetDebugContext createDebugContext(@NotNull DotNetVirtualMachineProxy proxy, @Nullable XBreakpoint<?> breakpoint)
	{
		return new DotNetDebugContext(getSession().getProject(), proxy, myRunProfile, getSession(), breakpoint);
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

	@Override
	public abstract void stop();

	@NotNull
	public Collection<? extends XLineBreakpoint<?>> getLineBreakpoints()
	{
		return ApplicationManager.getApplication().runReadAction(new Computable<Collection<? extends XLineBreakpoint<DotNetLineBreakpointProperties>>>()
		{
			@Override
			public Collection<? extends XLineBreakpoint<DotNetLineBreakpointProperties>> compute()
			{
				return myDebuggerManager.getBreakpointManager().getBreakpoints(DotNetLineBreakpointType.class);
			}
		});
	}

	@NotNull
	public Collection<? extends XBreakpoint<DotNetExceptionBreakpointProperties>> getExceptionBreakpoints()
	{
		return ApplicationManager.getApplication().runReadAction(new Computable<Collection<? extends XBreakpoint<DotNetExceptionBreakpointProperties>>>()
		{
			@Override
			public Collection<? extends XBreakpoint<DotNetExceptionBreakpointProperties>> compute()
			{
				return myDebuggerManager.getBreakpointManager().getBreakpoints(DotNetExceptionBreakpointType.class);
			}
		});
	}

	public void normalizeBreakpoints()
	{
		for(XLineBreakpoint<?> lineBreakpoint : getLineBreakpoints())
		{
			myDebuggerManager.getBreakpointManager().updateBreakpointPresentation(lineBreakpoint, null, null);
		}
	}
}
