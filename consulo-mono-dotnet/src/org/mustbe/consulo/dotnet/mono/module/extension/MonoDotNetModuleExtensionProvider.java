package org.mustbe.consulo.dotnet.mono.module.extension;

import javax.swing.Icon;

import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.mono.MonoDotNetIcons;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MonoDotNetModuleExtensionProvider implements ModuleExtensionProvider<MonoDotNetModuleExtension, MonoDotNetMutableModuleExtension>
{
	@Nullable
	@Override
	public Icon getIcon()
	{
		return MonoDotNetIcons.Mono;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "Mono .NET";
	}

	@NotNull
	@Override
	public Class<MonoDotNetModuleExtension> getImmutableClass()
	{
		return MonoDotNetModuleExtension.class;
	}

	@NotNull
	@Override
	public MonoDotNetModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new MonoDotNetModuleExtension(s, module);
	}

	@NotNull
	@Override
	public MonoDotNetMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module, @NotNull MonoDotNetModuleExtension
			dotNetModuleExtension)
	{
		return new MonoDotNetMutableModuleExtension(s, module, dotNetModuleExtension);
	}
}
