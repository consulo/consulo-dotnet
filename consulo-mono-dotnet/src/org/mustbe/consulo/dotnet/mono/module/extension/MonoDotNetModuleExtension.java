package org.mustbe.consulo.dotnet.mono.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionWithSdkImpl;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.DotNetVersion;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.mono.sdk.MonoSdkType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.SdkType;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MonoDotNetModuleExtension extends ModuleExtensionWithSdkImpl<MonoDotNetModuleExtension> implements
		DotNetModuleExtension<MonoDotNetModuleExtension>
{
	public MonoDotNetModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}

	@Override
	protected Class<? extends SdkType> getSdkTypeClass()
	{
		return MonoSdkType.class;
	}

	@NotNull
	@Override
	public DotNetVersion getVersion()
	{
		return DotNetVersion.LAST;
	}
}
