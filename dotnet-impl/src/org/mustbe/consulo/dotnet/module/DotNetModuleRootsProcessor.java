package org.mustbe.consulo.dotnet.module;

import gnu.trove.TObjectIntHashMap;
import gnu.trove.TObjectProcedure;

import org.jetbrains.annotations.NotNull;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import com.google.common.base.Predicate;
import com.intellij.openapi.roots.ModuleRootModel;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import consulo.roots.ContentFolderTypeProvider;
import consulo.roots.impl.ModuleRootsProcessor;
import consulo.roots.impl.ProductionContentFolderTypeProvider;

/**
 * @author VISTALL
 * @since 08.12.14
 */
public class DotNetModuleRootsProcessor extends ModuleRootsProcessor
{
	@Override
	public boolean containsFile(@NotNull TObjectIntHashMap<VirtualFile> roots, @NotNull final VirtualFile virtualFile)
	{
		return !roots.forEachKey(new TObjectProcedure<VirtualFile>()
		{
			@Override
			public boolean execute(VirtualFile object)
			{
				return !VfsUtil.isAncestor(object, virtualFile, false);
			}
		});
	}

	@NotNull
	@Override
	public VirtualFile[] getFiles(@NotNull ModuleRootModel moduleRootModel, @NotNull Predicate<ContentFolderTypeProvider> predicate)
	{
		if(predicate.apply(ProductionContentFolderTypeProvider.getInstance()))
		{
			return moduleRootModel.getContentRoots();
		}
		return VirtualFile.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	public String[] getUrls(@NotNull ModuleRootModel moduleRootModel, @NotNull Predicate<ContentFolderTypeProvider> predicate)
	{
		if(predicate.apply(ProductionContentFolderTypeProvider.getInstance()))
		{
			return moduleRootModel.getContentRootUrls();
		}
		return ArrayUtil.EMPTY_STRING_ARRAY;
	}

	@Override
	public boolean canHandle(@NotNull ModuleRootModel moduleRootModel)
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
