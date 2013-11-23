package org.mustbe.consulo.dotnet.module.extension;

import org.consulo.module.extension.ModuleExtension;
import org.mustbe.consulo.dotnet.DotNetVersion;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public interface DotNetModuleExtension<T extends ModuleExtension<T>> extends ModuleExtension<T>
{
	DotNetVersion getVersion();
}
