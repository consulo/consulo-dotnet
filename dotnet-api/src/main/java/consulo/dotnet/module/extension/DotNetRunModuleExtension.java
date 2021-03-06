package consulo.dotnet.module.extension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.projectRoots.Sdk;
import consulo.dotnet.DotNetTarget;
import consulo.dotnet.execution.DebugConnectionInfo;
import consulo.module.extension.ModuleExtension;

/**
 * @author VISTALL
 * @since 2018-02-14
 */
public interface DotNetRunModuleExtension<T extends DotNetRunModuleExtension<T>> extends ModuleExtension<T>
{
	@Nonnull
	String getFileName();

	@Nonnull
	String getOutputDir();

	@Nullable
	Sdk getSdk();

	boolean isAllowDebugInfo();

	@Nonnull
	DotNetTarget getTarget();

	@Nonnull
	String getDebugFileExtension();

	@Nonnull
	GeneralCommandLine createDefaultCommandLine(@Nonnull Sdk sdk, @Nullable DebugConnectionInfo debugConnectionInfo) throws ExecutionException;
}
