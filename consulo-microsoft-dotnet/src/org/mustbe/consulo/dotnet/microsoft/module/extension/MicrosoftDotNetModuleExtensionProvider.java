package org.mustbe.consulo.dotnet.microsoft.module.extension;

import javax.swing.Icon;

import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.microsoft.MicrosoftDotNetIcons;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public class MicrosoftDotNetModuleExtensionProvider implements ModuleExtensionProvider<MicrosoftDotNetModuleExtension, MicrosoftDotNetMutableModuleExtension>
{
	@Nullable
	@Override
	public Icon getIcon()
	{
		return MicrosoftDotNetIcons.DotNet;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "Microsoft .NET";
	}

	@NotNull
	@Override
	public Class<MicrosoftDotNetModuleExtension> getImmutableClass()
	{
		return MicrosoftDotNetModuleExtension.class;
	}

	@NotNull
	@Override
	public MicrosoftDotNetModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new MicrosoftDotNetModuleExtension(s, module);
	}

	@NotNull
	@Override
	public MicrosoftDotNetMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module, @NotNull MicrosoftDotNetModuleExtension
			dotNetModuleExtension)
	{
		return new MicrosoftDotNetMutableModuleExtension(s, module, dotNetModuleExtension);
	}
}
