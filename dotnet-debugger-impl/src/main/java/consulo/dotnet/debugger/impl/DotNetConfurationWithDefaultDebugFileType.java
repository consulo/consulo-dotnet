package consulo.dotnet.debugger.impl;

import consulo.language.file.LanguageFileType;

/**
 * @author VISTALL
 * @since 2025-09-09
 */
public interface DotNetConfurationWithDefaultDebugFileType {
    LanguageFileType getDefaultDebuggerFileType();
}
