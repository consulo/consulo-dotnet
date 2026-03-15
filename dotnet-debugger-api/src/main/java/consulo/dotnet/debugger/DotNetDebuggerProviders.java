package consulo.dotnet.debugger;

import consulo.annotation.access.RequiredReadAction;
import consulo.application.Application;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 06.06.14
 */
public class DotNetDebuggerProviders
{
	@Nullable
	@RequiredReadAction
	public static DotNetDebuggerProvider findByVirtualFile(Project project, VirtualFile virtualFile)
	{
		PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
		return file != null ? findByPsiFile(file) : null;
	}

	@Nullable
	public static DotNetDebuggerProvider findByPsiFile(PsiFile psiFile)
	{
		return Application.get().getExtensionPoint(DotNetDebuggerProvider.class).findFirstSafe(provider -> provider.isSupported(psiFile));
	}
}
