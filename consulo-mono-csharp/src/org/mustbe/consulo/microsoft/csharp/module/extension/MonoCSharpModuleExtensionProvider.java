package org.mustbe.consulo.microsoft.csharp.module.extension;

import javax.swing.Icon;

import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.mono.MonoDotNetIcons;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class MonoCSharpModuleExtensionProvider implements ModuleExtensionProvider<MonoCSharpModuleExtension,MonoCSharpMutableModuleExtension>
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
		return "C#";
	}

	@NotNull
	@Override
	public Class<MonoCSharpModuleExtension> getImmutableClass()
	{
		return MonoCSharpModuleExtension.class;
	}

	@NotNull
	@Override
	public MonoCSharpModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new MonoCSharpModuleExtension(s, module);
	}

	@NotNull
	@Override
	public MonoCSharpMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module, @NotNull MonoCSharpModuleExtension monoCSharpModuleExtension)
	{
		return new MonoCSharpMutableModuleExtension(s, module, monoCSharpModuleExtension);
	}
}
