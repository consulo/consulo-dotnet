package org.mustbe.consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiFile;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public interface DotNetDebuggerProvider
{
	ExtensionPointName<DotNetDebuggerProvider> EP_NAME = ExtensionPointName.create("org.mustbe.consulo.dotnet.core.debugger.provider");

	public boolean isSupported(@NotNull PsiFile psiFile);
}
