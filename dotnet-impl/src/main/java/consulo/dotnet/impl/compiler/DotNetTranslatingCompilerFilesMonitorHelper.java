/*
 * Copyright 2013-2014 must-be.org
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

package consulo.dotnet.impl.compiler;

import consulo.annotation.component.ExtensionImpl;
import consulo.compiler.TranslatingCompilerFilesMonitorHelper;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.content.ModuleRootManager;
import consulo.module.extension.ModuleExtension;
import consulo.virtualFileSystem.VirtualFile;

import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

/**
 * @author VISTALL
 * @since 16.01.14
 */
@ExtensionImpl
public class DotNetTranslatingCompilerFilesMonitorHelper implements TranslatingCompilerFilesMonitorHelper {
    @Override
    public Path @Nullable [] getRootsForModule(Module module) {
        DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
        if (extension == null || extension.isAllowSourceRoots()) {
            return null;
        }
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        Path[] roots = new Path[contentRoots.length];
        for (int i = 0; i < contentRoots.length; i++) {
            roots[i] = contentRoots[i].toNioPath();
        }
        return roots;
    }

    @Override
    public boolean isModuleExtensionAffectToCompilation(ModuleExtension<?> extension) {
        return extension instanceof DotNetModuleExtension;
    }
}
