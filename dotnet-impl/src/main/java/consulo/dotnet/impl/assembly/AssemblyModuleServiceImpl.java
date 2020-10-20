package consulo.dotnet.impl.assembly;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtilCore;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.assembly.AssemblyModule;
import consulo.dotnet.assembly.AssemblyModuleService;
import consulo.dotnet.dll.DotNetModuleFileType;
import consulo.vfs.util.ArchiveVfsUtil;

import javax.annotation.Nonnull;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * @author VISTALL
 * @since 2020-08-02
 */
@Singleton
public class AssemblyModuleServiceImpl implements AssemblyModuleService
{
	private final Project myProject;

	@Inject
	public AssemblyModuleServiceImpl(Project project)
	{
		myProject = project;
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public AssemblyModule resolve(@Nonnull PsiElement element)
	{
		Module module = ModuleUtilCore.findModuleForPsiElement(element);
		if(module != null)
		{
			return new ConsuloModuleAsAssemblyModule(module);
		}

		VirtualFile virtualFile = PsiUtilCore.getVirtualFile(element);
		if(virtualFile != null)
		{
			VirtualFile rootFile = ArchiveVfsUtil.getVirtualFileForArchive(virtualFile);
			if(rootFile != null && rootFile.getFileType() == DotNetModuleFileType.INSTANCE)
			{
				return new DotNetModuleAsAssemblyModule(myProject, rootFile);
			}
		}
		return UnknownAssemblyModule.INSTANCE;
	}
}
