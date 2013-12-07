package org.mustbe.consulo.microsoft.csharp.module.extension;

import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.CSharpFileType;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerOptionsBuilder;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleLangExtension;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class MicrosoftCSharpModuleExtension extends ModuleExtensionImpl<MicrosoftCSharpModuleExtension> implements
		DotNetModuleLangExtension<MicrosoftCSharpModuleExtension>

{
	public MicrosoftCSharpModuleExtension(@NotNull String id, @NotNull Module module)
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
		optionsBuilder.addArgument("/utf8output"); // utf8 output
		optionsBuilder.setExecutableFromSdk("csc.exe");
	}
}
