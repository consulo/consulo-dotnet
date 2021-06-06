package consulo.dotnet.module;

import com.google.common.base.Predicate;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.roots.ContentFolderTypeProvider;
import consulo.roots.impl.ModuleRootsProcessor;
import consulo.roots.impl.ProductionContentFolderTypeProvider;
import consulo.util.collection.primitive.objects.ObjectIntMap;

import javax.annotation.Nonnull;

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
		if(predicate.apply(ProductionContentFolderTypeProvider.getInstance()))
		{
			return moduleRootModel.getContentRoots();
		}
		return VirtualFile.EMPTY_ARRAY;
	}

	@Nonnull
	@Override
	public String[] getUrls(@Nonnull ModuleRootModel moduleRootModel, @Nonnull Predicate<ContentFolderTypeProvider> predicate)
	{
		if(predicate.apply(ProductionContentFolderTypeProvider.getInstance()))
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
