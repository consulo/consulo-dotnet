package consulo.dotnet.debugger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.annotations.RequiredReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

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
		for(DotNetDebuggerProvider provider : DotNetDebuggerProvider.EP_NAME.getExtensions())
		{
			if(provider.isSupported(psiFile))
			{
				return provider;
			}
		}
		return null;
	}
}
