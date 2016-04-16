package org.mustbe.consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import com.intellij.xdebugger.XDebugProcess;

/**
 * @author VISTALL
 * @since 16.04.2016
 */
public interface DotNetModuleExtensionWithDebug
{
	@NotNull
	XDebugProcess createDebuggerProcess();
}
