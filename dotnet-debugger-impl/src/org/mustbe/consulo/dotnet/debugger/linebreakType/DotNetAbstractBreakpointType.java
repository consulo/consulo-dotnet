package org.mustbe.consulo.dotnet.debugger.linebreakType;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import mono.debugger.VirtualMachine;
import mono.debugger.request.EventRequest;

/**
 * @author VISTALL
 * @since 13.04.14
 */
public abstract class DotNetAbstractBreakpointType extends XLineBreakpointTypeBase
{
	public DotNetAbstractBreakpointType(
			@NonNls @NotNull String id, @Nls @NotNull String title, @Nullable XDebuggerEditorsProvider editorsProvider)
	{
		super(id, title, editorsProvider);
	}

	@Nullable
	public abstract EventRequest createEventRequest(
			@NotNull Project project,
			@NotNull VirtualMachine virtualMachine,
			@NotNull XLineBreakpoint breakpoint);
}
