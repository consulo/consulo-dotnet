package consulo.msil.impl.representation.fileSystem;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.AccessRule;
import consulo.language.file.FileTypeManager;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.representation.MsilFileRepresentationProvider;
import consulo.project.Project;
import consulo.project.ProjectLocator;
import consulo.virtualFileSystem.BaseVirtualFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 31/12/2020
 */
@ExtensionImpl
public class MsilFileRepresentationVirtualFileSystem extends BaseVirtualFileSystem
{
	@Nonnull
	public static MsilFileRepresentationVirtualFileSystem getInstance()
	{
		return (MsilFileRepresentationVirtualFileSystem) VirtualFileManager.getInstance().getFileSystem(PROTOCOL);
	}

	public static final String PROTOCOL = "msil";
	public static final String SEPARATOR = "|";

	@Nonnull
	@Override
	public String getProtocol()
	{
		return PROTOCOL;
	}

	@Nullable
	@Override
	public VirtualFile findFileByPath(@Nonnull String path)
	{
		if(path.startsWith(PROTOCOL))
		{
			return null;
		}

		int i = path.indexOf(SEPARATOR);
		if(i == -1)
		{
			return null;
		}

		String fileUrl = path.substring(0, i);
		String fileTypeId = path.substring(i + 1, path.length());

		VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
		if(file == null)
		{
			return null;
		}

		FileType type = FileTypeManager.getInstance().getStdFileType(fileTypeId);

		Project project = ProjectLocator.getInstance().guessProjectForFile(file);

		if(project == null)
		{
			return null;
		}

		PsiFile msilFile = AccessRule.read(() -> PsiManager.getInstance(project).findFile(file));

		if(msilFile == null)
		{
			return null;
		}

		MsilFileRepresentationProvider msilProvider = null;
		for(MsilFileRepresentationProvider provider : MsilFileRepresentationProvider.EP_NAME.getExtensionList())
		{
			if(provider.getFileType() == type)
			{
				msilProvider = provider;
				break;
			}
		}

		if(msilProvider == null)
		{
			return null;
		}

		String fileName = msilProvider.getRepresentFileName((MsilFile) msilFile);
		return new MsilFileRepresentationVirtualFile(fileName, path, type, file, msilProvider);
	}

	@Override
	public void refresh(boolean b)
	{

	}

	@Nullable
	@Override
	public VirtualFile refreshAndFindFileByPath(@Nonnull String path)
	{
		return findFileByPath(path);
	}
}
