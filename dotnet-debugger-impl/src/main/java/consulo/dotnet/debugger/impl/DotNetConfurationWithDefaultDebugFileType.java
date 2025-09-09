package consulo.dotnet.debugger.impl;

import consulo.language.file.LanguageFileType;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2025-09-09
 */
public interface DotNetConfurationWithDefaultDebugFileType {
    @Nonnull
    LanguageFileType getDefaultDebuggerFileType();
}
