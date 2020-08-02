package consulo.dotnet.assembly;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import consulo.annotation.access.RequiredReadAction;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2020-08-02
 */
public interface AssemblyModuleService
{
	@Nonnull
	static AssemblyModuleService getInstance(@Nonnull Project project)
	{
		return ServiceManager.getService(project, AssemblyModuleService.class);
	}

	@Nonnull
	@RequiredReadAction
	AssemblyModule resolve(@Nonnull PsiElement element);
}
