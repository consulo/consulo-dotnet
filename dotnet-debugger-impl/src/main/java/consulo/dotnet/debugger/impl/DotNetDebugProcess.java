package consulo.dotnet.debugger.impl;

import consulo.dotnet.debugger.DotNetDebugContext;
import consulo.dotnet.debugger.proxy.DotNetVirtualMachineProxy;
import consulo.execution.ExecutionResult;
import consulo.execution.debug.XBreakpointManager;
import consulo.execution.debug.breakpoint.XBreakpoint;
import consulo.execution.debug.breakpoint.XLineBreakpoint;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

/**
 * @author VISTALL
 * @since 2024-12-22
 */
public interface DotNetDebugProcess {
    DotNetDebugContext createDebugContext(DotNetVirtualMachineProxy proxy, @Nullable XBreakpoint<?> breakpoint);

    void start();

    void setExecutionResult(ExecutionResult executionResult);

    Collection<? extends XLineBreakpoint<?>> getLineBreakpoints();

    default void normalizeBreakpoints() {
        for (XLineBreakpoint<?> lineBreakpoint : getLineBreakpoints()) {
            getBreakpointManager().updateBreakpointPresentation(lineBreakpoint, null, null);
        }
    }

    XBreakpointManager getBreakpointManager();
}
