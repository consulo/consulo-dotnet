package consulo.dotnet.debugger;

import consulo.application.ApplicationManager;
import consulo.application.progress.Task;
import consulo.debugger.XDebugProcess;
import consulo.debugger.XDebugSession;
import consulo.debugger.XDebuggerManager;
import consulo.debugger.XSourcePosition;
import consulo.debugger.breakpoint.XBreakpoint;
import consulo.debugger.breakpoint.XLineBreakpoint;
import consulo.debugger.evaluation.XDebuggerEditorsProvider;
import consulo.debugger.frame.XSuspendContext;
import consulo.dotnet.debugger.breakpoint.DotNetExceptionBreakpointType;
import consulo.dotnet.debugger.breakpoint.DotNetLineBreakpointType;
import consulo.dotnet.debugger.breakpoint.DotNetMethodBreakpointType;
import consulo.dotnet.debugger.breakpoint.properties.DotNetExceptionBreakpointProperties;
import consulo.dotnet.debugger.breakpoint.properties.DotNetLineBreakpointProperties;
import consulo.dotnet.debugger.breakpoint.properties.DotNetMethodBreakpointProperties;
import consulo.dotnet.debugger.nodes.logicView.*;
import consulo.dotnet.debugger.proxy.DotNetVirtualMachineProxy;
import consulo.execution.ExecutionResult;
import consulo.execution.configuration.RunProfile;
import consulo.execution.ui.ExecutionConsole;
import consulo.execution.ui.console.TextConsoleBuilderFactory;
import consulo.util.concurrent.AsyncResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public abstract class DotNetDebugProcessBase extends XDebugProcess
{
	public static final String RUN_TO_CURSOR = "runToCursor";

	private ExecutionResult myResult;
	private final RunProfile myRunProfile;
	protected final XDebuggerManager myDebuggerManager;

	private NotNullLazyValue<DotNetLogicValueView[]> myLogicValueViewsLazy;

	public DotNetDebugProcessBase(@Nonnull XDebugSession session, @Nonnull RunProfile runProfile)
	{
		super(session);

		myRunProfile = runProfile;
		myDebuggerManager = XDebuggerManager.getInstance(session.getProject());

		myLogicValueViewsLazy = NotNullLazyValue.createValue(this::createLogicValueViews);
	}

	@Nonnull
	public DotNetDebugContext createDebugContext(@Nonnull DotNetVirtualMachineProxy proxy, @Nullable XBreakpoint<?> breakpoint)
	{
		return new DotNetDebugContext(getSession().getProject(), proxy, myRunProfile, getSession(), breakpoint, myLogicValueViewsLazy.getValue());
	}

	@Nonnull
	protected DotNetLogicValueView[] createLogicValueViews()
	{
		return new DotNetLogicValueView[]{
				new ArrayDotNetLogicValueView(),
				new StringDotNetLogicValueView(),
				new EnumerableDotNetLogicValueView(),
				new DefaultDotNetLogicValueView()
		};
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

	@Nonnull
	@Override
	public ExecutionConsole createConsole()
	{
		ExecutionConsole executionConsole = myResult.getExecutionConsole();
		if(executionConsole == null)
		{
			return TextConsoleBuilderFactory.getInstance().createBuilder(getSession().getProject()).getConsole();
		}
		return executionConsole;
	}

	@Nonnull
	@Override
	public XDebuggerEditorsProvider getEditorsProvider()
	{
		return new DotNetEditorsProvider(getSession());
	}

	@Override
	public void startStepOver(@Nullable XSuspendContext context)
	{

	}

	@Override
	public void startStepInto(@Nullable XSuspendContext context)
	{

	}

	@Override
	public void startStepOut(@Nullable XSuspendContext context)
	{

	}

	@Override
	public void resume(@Nullable XSuspendContext context)
	{

	}

	@Override
	public void runToPosition(@Nonnull XSourcePosition position, @Nullable XSuspendContext context)
	{
	}

	@Nonnull
	@Override
	public AsyncResult<Void> stopAsync()
	{
		AsyncResult<Void> result = AsyncResult.undefined();
		Task.Backgroundable.queue(getSession().getProject(), "Waiting for debugger response...", indicator ->
		{
			stopImpl();
			result.setDone();
		});
		return result;
	}

	protected abstract void stopImpl();

	@Nonnull
	public Collection<? extends XLineBreakpoint<?>> getLineBreakpoints()
	{
		return ApplicationManager.getApplication().runReadAction((Computable<Collection<? extends XLineBreakpoint<DotNetLineBreakpointProperties>>>) () -> myDebuggerManager.getBreakpointManager()
				.getBreakpoints(DotNetLineBreakpointType.getInstance()));
	}

	@Nonnull
	public Collection<? extends XLineBreakpoint<DotNetMethodBreakpointProperties>> getMethodBreakpoints()
	{
		return ApplicationManager.getApplication().runReadAction((Computable<Collection<? extends XLineBreakpoint<DotNetMethodBreakpointProperties>>>) () -> myDebuggerManager.getBreakpointManager()
				.getBreakpoints(DotNetMethodBreakpointType.getInstance()));
	}

	@Nonnull
	public Collection<? extends XBreakpoint<DotNetExceptionBreakpointProperties>> getExceptionBreakpoints()
	{
		return ApplicationManager.getApplication().runReadAction((Computable<Collection<? extends XBreakpoint<DotNetExceptionBreakpointProperties>>>) () -> myDebuggerManager.getBreakpointManager()
				.getBreakpoints(DotNetExceptionBreakpointType.getInstance()));
	}

	public void normalizeBreakpoints()
	{
		for(XLineBreakpoint<?> lineBreakpoint : getLineBreakpoints())
		{
			myDebuggerManager.getBreakpointManager().updateBreakpointPresentation(lineBreakpoint, null, null);
		}
	}
}
