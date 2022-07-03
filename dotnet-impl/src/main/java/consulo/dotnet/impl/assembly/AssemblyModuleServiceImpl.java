package consulo.dotnet.impl.assembly;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ServiceImpl;
import consulo.dotnet.assembly.AssemblyModule;
import consulo.dotnet.assembly.AssemblyModuleService;
import consulo.dotnet.dll.DotNetModuleFileType;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiUtilCore;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.archive.ArchiveVfsUtil;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2020-08-02
 */
@Singleton
@ServiceImpl
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
