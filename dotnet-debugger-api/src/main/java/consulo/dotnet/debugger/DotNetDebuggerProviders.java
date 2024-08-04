package consulo.dotnet.debugger;

import consulo.annotation.access.RequiredReadAction;
import consulo.application.Application;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 06.06.14
 */
public class DotNetDebuggerProviders
{
	@Nullable
	@RequiredReadAction
	public static DotNetDebuggerProvider findByVirtualFile(@Nonnull Project project, @Nonnull VirtualFile virtualFile)
	{
		PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
		return file != null ? findByPsiFile(file) : null;
	}

	@Nullable
	public static DotNetDebuggerProvider findByPsiFile(@Nonnull PsiFile psiFile)
	{
		return Application.get().getExtensionPoint(DotNetDebuggerProvider.class).findFirstSafe(provider -> provider.isSupported(psiFile));
	}
}
