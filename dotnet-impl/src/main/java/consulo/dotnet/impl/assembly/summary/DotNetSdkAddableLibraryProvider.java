/*
 * Copyright 2013-2026 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package consulo.dotnet.impl.assembly.summary;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.access.RequiredWriteAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.content.base.BinariesOrderRootType;
import consulo.dotnet.assembly.summary.DotNetAddableLibrary;
import consulo.dotnet.assembly.summary.DotNetAddableLibraryProvider;
import consulo.dotnet.impl.roots.orderEntry.DotNetLibraryOrderEntryType;
import consulo.dotnet.module.extension.DotNetLibraryOrderEntryModel;
import consulo.dotnet.module.extension.DotNetModuleExtensionWithLibraryProviding;
import consulo.module.Module;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.module.content.layer.orderEntry.CustomOrderEntry;
import consulo.module.content.layer.orderEntry.OrderEntry;
import consulo.module.extension.ModuleExtension;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

/**
 * The project model of a Consulo .NET module: the libraries of its SDK (Mono, .NET Framework, Unity…) are added as
 * {@link DotNetLibraryOrderEntryModel} order entries by assembly name
 *
 * @author VISTALL
 */
@ExtensionImpl
public class DotNetSdkAddableLibraryProvider implements DotNetAddableLibraryProvider {
    private record SdkLibrary(String name, Path file) implements DotNetAddableLibrary {
        @Override
        public String getName() {
            return name;
        }

        @Override
        public Path getFile() {
            return file;
        }

        @RequiredWriteAction
        @Override
        public void addTo(Module module) {
            ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
            model.addCustomOderEntry(DotNetLibraryOrderEntryType.getInstance(), new DotNetLibraryOrderEntryModel(name));
            model.commit();
        }
    }

    @RequiredReadAction
    @Override
    public List<DotNetAddableLibrary> getAddableLibraries(Module module) {
        ModuleRootManager rootManager = ModuleRootManager.getInstance(module);

        Set<String> added = new HashSet<>();
        for (OrderEntry orderEntry : rootManager.getOrderEntries()) {
            if (orderEntry instanceof CustomOrderEntry<?> custom && custom.getModel() instanceof DotNetLibraryOrderEntryModel model) {
                added.add(model.getPresentableName().toLowerCase(Locale.ROOT));
            }
        }

        List<DotNetAddableLibrary> libraries = new ArrayList<>();
        for (ModuleExtension<?> extension : rootManager.getExtensions()) {
            if (!(extension instanceof DotNetModuleExtensionWithLibraryProviding<?> providing)) {
                continue;
            }
            for (String name : providing.getAvailableSystemLibraries().keySet()) {
                if (added.contains(name.toLowerCase(Locale.ROOT))) {
                    continue;
                }
                Path file = toPath(providing.getSystemLibraryUrls(name, BinariesOrderRootType.ID));
                if (file != null) {
                    libraries.add(new SdkLibrary(name, file));
                }
            }
        }
        return libraries;
    }

    /**
     * @param urls the urls of a library: {@code dll://<path>!/}
     */
    @Nullable
    private static Path toPath(String[] urls) {
        if (urls.length == 0) {
            return null;
        }
        String url = urls[0];
        int start = url.indexOf("://");
        int end = url.lastIndexOf("!/");
        if (start < 0) {
            return null;
        }
        return Path.of(url.substring(start + 3, end > start ? end : url.length()));
    }
}
