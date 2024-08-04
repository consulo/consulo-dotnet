package consulo.dotnet.impl.roots.orderEntry;

import consulo.content.OrderRootType;
import consulo.content.RootProvider;
import consulo.content.RootProviderBase;
import consulo.dotnet.module.extension.DotNetModuleExtensionWithLibraryProviding;
import consulo.dotnet.module.extension.DotNetSimpleModuleExtension;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.orderEntry.CustomOrderEntryModel;
import consulo.module.extension.ModuleExtension;
import consulo.util.collection.ArrayUtil;
import consulo.util.collection.SmartList;
import consulo.util.lang.Comparing;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.virtualFileSystem.util.VirtualFileUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;

/**
 * @author VISTALL
 * @since 28-May-22
 */
public class DotNetLibraryOrderEntryModel implements CustomOrderEntryModel
{
	private RootProvider myRootProvider = new RootProviderBase()
	{
		@Nonnull
		@Override
		public String[] getUrls(@Nonnull OrderRootType rootType)
		{
			DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
			if(extension == null)
			{
				return ArrayUtil.EMPTY_STRING_ARRAY;
			}

			String[] urls = ArrayUtil.EMPTY_STRING_ARRAY;
			for(ModuleExtension moduleExtension : myModuleRootLayer.getExtensions())
			{
				if(moduleExtension instanceof DotNetModuleExtensionWithLibraryProviding)
				{
					String[] systemLibraryUrls = ((DotNetModuleExtensionWithLibraryProviding) moduleExtension).getSystemLibraryUrls(getPresentableName()
							, rootType);
					urls = ArrayUtil.mergeArrays(urls, systemLibraryUrls);
				}
			}
			return urls;
		}

		@Nonnull
		@Override
		public VirtualFile[] getFiles(@Nonnull OrderRootType rootType)
		{
			DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
			if(extension == null)
			{
				return VirtualFile.EMPTY_ARRAY;
			}

			List<VirtualFile> virtualFiles = new SmartList<>();
			for(ModuleExtension moduleExtension : myModuleRootLayer.getExtensions())
			{
				if(moduleExtension instanceof DotNetModuleExtensionWithLibraryProviding)
				{
					String[] systemLibraryUrls = ((DotNetModuleExtensionWithLibraryProviding) moduleExtension).getSystemLibraryUrls(getPresentableName()
							, rootType);
					for(String systemLibraryUrl : systemLibraryUrls)
					{
						VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(systemLibraryUrl);
						if(fileByUrl != null)
						{
							virtualFiles.add(fileByUrl);
						}
					}
				}
			}
			return VirtualFileUtil.toVirtualFileArray(virtualFiles);
		}
	};

	private ModuleRootLayer myModuleRootLayer;

	private final String myName;

	public DotNetLibraryOrderEntryModel(String name)
	{
		myName = name;
	}

	@Nullable
	@Override
	public Object getEqualObject()
	{
		DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
		if(extension == null)
		{
			return VirtualFile.EMPTY_ARRAY;
		}
		return extension.getSdk();
	}

	@Override
	public boolean isEquivalentTo(@Nonnull CustomOrderEntryModel model)
	{
		return model instanceof DotNetLibraryOrderEntryModel && Comparing.equal(myName, model.getPresentableName());
	}

	@Override
	public boolean isSynthetic()
	{
		return false;
	}

	@Override
	public void bind(@Nonnull ModuleRootLayer moduleRootLayer)
	{
		myModuleRootLayer = moduleRootLayer;
	}

	@Nonnull
	@Override
	public String getPresentableName()
	{
		return myName;
	}

	@Override
	public boolean isValid()
	{
		return myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class) != null;
	}

	@Nonnull
	@Override
	public RootProvider getRootProvider()
	{
		return myRootProvider;
	}

	@Nonnull
	@Override
	public CustomOrderEntryModel clone()
	{
		return new DotNetLibraryOrderEntryModel(myName);
	}
}
