package org.mustbe.consulo.dotnet.module.extension;

import org.consulo.module.extension.ModuleExtensionWithSdk;
import org.mustbe.consulo.dotnet.DotNetVersion;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public interface DotNetModuleExtension<T extends ModuleExtensionWithSdk<T>> extends ModuleExtensionWithSdk<T>
{
	DotNetVersion getVersion();
}
