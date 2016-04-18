package consulo.dotnet.debugger.impl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.xdebugger.XDebugSession;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public interface DotNetModuleExtensionWithDebug
{
	@NotNull
	DotNetDebugProcessBase createDebuggerProcess(@NotNull XDebugSession session, @NotNull RunProfile runProfile, @NotNull DebugConnectionInfo debugConnectionInfo);
}
