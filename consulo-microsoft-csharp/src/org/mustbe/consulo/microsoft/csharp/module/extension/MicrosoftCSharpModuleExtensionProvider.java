package org.mustbe.consulo.microsoft.csharp.module.extension;

import javax.swing.Icon;

import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.microsoft.dotnet.MicrosoftDotNetIcons;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class MicrosoftCSharpModuleExtensionProvider implements ModuleExtensionProvider<MicrosoftCSharpModuleExtension,MicrosoftCSharpMutableModuleExtension>
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
		return "C#";
	}

	@NotNull
	@Override
	public Class<MicrosoftCSharpModuleExtension> getImmutableClass()
	{
		return MicrosoftCSharpModuleExtension.class;
	}

	@NotNull
	@Override
	public MicrosoftCSharpModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new MicrosoftCSharpModuleExtension(s, module);
	}

	@NotNull
	@Override
	public MicrosoftCSharpMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module, @NotNull MicrosoftCSharpModuleExtension
			microsoftCSharpModuleExtension)
	{
		return new MicrosoftCSharpMutableModuleExtension(s, module, microsoftCSharpModuleExtension);
	}
}
