package org.mustbe.consulo.dotnet.debugger.linebreakType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XLineBreakpointResolver;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;

/**
 * @author VISTALL
 * @since 19.03.2015
 */
public class DotNetLineBreakpointResolver implements XLineBreakpointResolver
{
	@Nullable
	@Override
	public XLineBreakpointType<?> resolveBreakpointType(@NotNull Project project, @NotNull VirtualFile virtualFile, int line)
	{
		return DotNetLineBreakpointType.getInstance();
	}
}
