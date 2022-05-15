package consulo.dotnet.impl.module;

import consulo.content.ContentFolderTypeProvider;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.ide.impl.idea.openapi.vfs.VfsUtil;
import consulo.language.content.ProductionContentFolderTypeProvider;
import consulo.module.content.layer.ModuleRootModel;
import consulo.module.impl.internal.layer.ModuleRootsProcessor;
import consulo.util.collection.ArrayUtil;
import consulo.util.collection.primitive.objects.ObjectIntMap;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * @author VISTALL
 * @since 08.12.14
 */
public class DotNetModuleRootsProcessor extends ModuleRootsProcessor
{
	@Override
	public boolean containsFile(@Nonnull ObjectIntMap<VirtualFile> roots, @Nonnull final VirtualFile virtualFile)
	{
		for(VirtualFile object : roots.keySet())
		{
			if(VfsUtil.isAncestor(object, virtualFile, false))
			{
				return true;
			}
		}
		return false;
	}

	@Nonnull
	@Override
	public VirtualFile[] getFiles(@Nonnull ModuleRootModel moduleRootModel, @Nonnull Predicate<ContentFolderTypeProvider> predicate)
	{
		if(predicate.test(ProductionContentFolderTypeProvider.getInstance()))
		{
			return moduleRootModel.getContentRoots();
		}
		return VirtualFile.EMPTY_ARRAY;
	}

	@Nonnull
	@Override
	public String[] getUrls(@Nonnull ModuleRootModel moduleRootModel, @Nonnull Predicate<ContentFolderTypeProvider> predicate)
	{
		if(predicate.test(ProductionContentFolderTypeProvider.getInstance()))
		{
			return moduleRootModel.getContentRootUrls();
		}
		return ArrayUtil.EMPTY_STRING_ARRAY;
	}

	@Override
	public boolean canHandle(@Nonnull ModuleRootModel moduleRootModel)
	{
		String moduleDirUrl = moduleRootModel.getModule().getModuleDirUrl();
		// if moduleDirUrl - need process by NullModuleDirModuleRootsProcessor
		if(moduleDirUrl == null)
		{
			return false;
		}
		DotNetModuleExtension extension = moduleRootModel.getExtension(DotNetModuleExtension.class);
		return extension != null && !extension.isAllowSourceRoots();
	}
}
