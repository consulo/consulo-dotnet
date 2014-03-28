package org.mustbe.consulo.dotnet.module;

import java.util.List;

import org.consulo.annotations.InheritImmutable;
import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * @author VISTALL
 * @since 03.02.14
 */
public interface MainConfigurationLayer extends ConfigurationLayer
{
	@NotNull
	DotNetTarget getTarget();

	boolean isAllowDebugInfo();

	@NotNull
	String getFileName();

	@NotNull
	String getOutputDir();

	@InheritImmutable
	List<String> getVariables();

	@NotNull
	MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk();
}
