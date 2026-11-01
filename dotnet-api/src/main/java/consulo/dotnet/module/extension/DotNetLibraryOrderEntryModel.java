package consulo.dotnet.module.extension;

import consulo.content.OrderRootType;
import consulo.content.RootProvider;
import consulo.content.RootProviderBase;
import consulo.content.bundle.Sdk;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.orderEntry.CustomOrderEntryModel;
import consulo.module.extension.ModuleExtension;
import consulo.util.collection.ArrayUtil;
import consulo.util.collection.SmartList;
import consulo.util.lang.Comparing;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * @author VISTALL
 * @since 28-May-22
 */
public class DotNetLibraryOrderEntryModel implements CustomOrderEntryModel {
    private RootProvider myRootProvider = new RootProviderBase() {
        @Override
        public String[] getUrls(String rootType) {
            DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
            if (extension == null) {
                return ArrayUtil.EMPTY_STRING_ARRAY;
            }

            String[] urls = ArrayUtil.EMPTY_STRING_ARRAY;
            for (ModuleExtension moduleExtension : myModuleRootLayer.getExtensions()) {
                if (moduleExtension instanceof DotNetModuleExtensionWithLibraryProviding) {
                    String[] systemLibraryUrls = ((DotNetModuleExtensionWithLibraryProviding) moduleExtension).getSystemLibraryUrls(getPresentableName()
                        , rootType);
                    urls = ArrayUtil.mergeArrays(urls, systemLibraryUrls);
                }
            }
            return urls;
        }

        @Override
        public VirtualFile[] getFiles(String rootType) {
            DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
            if (extension == null) {
                return VirtualFile.EMPTY_ARRAY;
            }

            List<VirtualFile> virtualFiles = new SmartList<>();
            for (ModuleExtension moduleExtension : myModuleRootLayer.getExtensions()) {
                if (moduleExtension instanceof DotNetModuleExtensionWithLibraryProviding) {
                    String[] systemLibraryUrls = ((DotNetModuleExtensionWithLibraryProviding) moduleExtension).getSystemLibraryUrls(getPresentableName()
                        , rootType);
                    for (String systemLibraryUrl : systemLibraryUrls) {
                        VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(systemLibraryUrl);
                        if (fileByUrl != null) {
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

    public DotNetLibraryOrderEntryModel(String name) {
        myName = name;
    }

    /**
     * Indexing visits one order entry per equal object, so it must name the library - not only the bundle, which every
     * library entry of the module shares (that indexed the first library of a module and skipped the others).
     */
    @Nullable
    @Override
    public Object getEqualObject() {
        DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
        return new LibraryKey(myName, extension == null ? null : extension.getSdk());
    }

    private record LibraryKey(String name, @Nullable Sdk sdk) {
    }

    @Override
    public boolean isEquivalentTo(CustomOrderEntryModel model) {
        return model instanceof DotNetLibraryOrderEntryModel && Comparing.equal(myName, model.getPresentableName());
    }

    @Override
    public boolean isSynthetic() {
        return false;
    }

    @Override
    public void bind(ModuleRootLayer moduleRootLayer) {
        myModuleRootLayer = moduleRootLayer;
    }

    @Override
    public String getPresentableName() {
        return myName;
    }

    @Override
    public boolean isValid() {
        return myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class) != null;
    }

    @Override
    public RootProvider getRootProvider() {
        return myRootProvider;
    }

    @Override
    public CustomOrderEntryModel clone() {
        return new DotNetLibraryOrderEntryModel(myName);
    }
}
