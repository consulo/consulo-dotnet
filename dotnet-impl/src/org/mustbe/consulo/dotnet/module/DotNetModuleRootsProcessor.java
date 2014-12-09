package org.mustbe.consulo.dotnet.module;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.roots.impl.ModuleRootsProcessorFromModuleDir;

/**
 * @author VISTALL
 * @since 08.12.14
 */
public class DotNetModuleRootsProcessor extends ModuleRootsProcessorFromModuleDir
{
	@Override
	public boolean canHandle(@NotNull ModuleRootModel moduleRootModel)
	{
		DotNetModuleExtension extension = moduleRootModel.getExtension(DotNetModuleExtension.class);
		return extension != null && !extension.isAllowSourceRoots();
	}
}
