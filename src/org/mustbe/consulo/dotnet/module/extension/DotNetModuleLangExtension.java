package org.mustbe.consulo.dotnet.module.extension;

import org.consulo.module.extension.ModuleExtension;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerOptionsBuilder;
import com.intellij.openapi.fileTypes.LanguageFileType;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public interface DotNetModuleLangExtension<T extends DotNetModuleLangExtension<T>> extends ModuleExtension<T>
{
	LanguageFileType getFileType();

	void setupCompilerOptions(DotNetCompilerOptionsBuilder optionsBuilder);
}
