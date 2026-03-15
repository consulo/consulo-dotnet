package consulo.dotnet.module.extension;

import consulo.content.bundle.Sdk;
import consulo.dotnet.DotNetTarget;
import consulo.dotnet.util.DebugConnectionInfo;
import consulo.module.extension.ModuleExtension;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;

import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 2018-02-14
 */
public interface DotNetRunModuleExtension<T extends DotNetRunModuleExtension<T>> extends ModuleExtension<T>
{
	String getFileName();

	String getOutputDir();

	@Nullable
	Sdk getSdk();

	boolean isAllowDebugInfo();

	DotNetTarget getTarget();

	String getDebugFileExtension();

	GeneralCommandLine createDefaultCommandLine(Sdk sdk, @Nullable DebugConnectionInfo debugConnectionInfo) throws ExecutionException;
}
