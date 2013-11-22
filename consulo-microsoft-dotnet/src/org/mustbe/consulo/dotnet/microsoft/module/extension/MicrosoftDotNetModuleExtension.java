package org.mustbe.consulo.dotnet.microsoft.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MicrosoftDotNetModuleExtension extends ModuleExtensionImpl<MicrosoftDotNetModuleExtension>
{
	public MicrosoftDotNetModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}
}
