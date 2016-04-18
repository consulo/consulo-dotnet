package consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerProvider;
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
	public static DotNetDebuggerProvider findByVirtualFile(@NotNull Project project, @NotNull VirtualFile virtualFile)
	{
		PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
		return file != null ? findByPsiFile(file) : null;
	}

	@Nullable
	public static DotNetDebuggerProvider findByPsiFile(@NotNull PsiFile psiFile)
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
