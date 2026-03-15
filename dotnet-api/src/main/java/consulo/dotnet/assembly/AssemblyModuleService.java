package consulo.dotnet.assembly;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.ide.ServiceManager;
import consulo.language.psi.PsiElement;
import consulo.project.Project;

/**
 * @author VISTALL
 * @since 2020-08-02
 */
@ServiceAPI(ComponentScope.PROJECT)
public interface AssemblyModuleService
{
	static AssemblyModuleService getInstance(Project project)
	{
		return ServiceManager.getService(project, AssemblyModuleService.class);
	}

	@RequiredReadAction
	AssemblyModule resolve(PsiElement element);
}
