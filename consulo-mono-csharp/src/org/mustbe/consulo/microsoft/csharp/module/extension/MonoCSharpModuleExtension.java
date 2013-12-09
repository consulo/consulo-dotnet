package org.mustbe.consulo.microsoft.csharp.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.CSharpFileType;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerOptionsBuilder;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleLangExtension;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class MonoCSharpModuleExtension extends ModuleExtensionImpl<MonoCSharpModuleExtension> implements
		DotNetModuleLangExtension<MonoCSharpModuleExtension>

{
	public MonoCSharpModuleExtension(@NotNull String id, @NotNull Module module)
	{
		super(id, module);
	}

	@Override
	public LanguageFileType getFileType()
	{
		return CSharpFileType.INSTANCE;
	}

	@Override
	public void setupCompilerOptions(DotNetCompilerOptionsBuilder optionsBuilder)
	{
		optionsBuilder.setExecutableFromSdk("mcs.exe");
	}
}
