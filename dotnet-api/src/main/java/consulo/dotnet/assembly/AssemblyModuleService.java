package consulo.dotnet.assembly;

import consulo.annotation.access.RequiredReadAction;
import consulo.ide.ServiceManager;
import consulo.language.psi.PsiElement;
import consulo.project.Project;

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
