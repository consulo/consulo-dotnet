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

import consulo.compiler.FileProcessingCompiler;
import consulo.compiler.TimestampValidityState;
import consulo.compiler.ValidityState;
import consulo.compiler.util.CompilerUtil;
import consulo.dotnet.module.extension.DotNetRunModuleExtension;

import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetProcessingItem implements FileProcessingCompiler.ProcessingItem {
    private final Path myFile;
    private final @Nullable DotNetRunModuleExtension<?> myExtension;

    public DotNetProcessingItem(Path file, @Nullable DotNetRunModuleExtension<?> dotNetModuleExtension) {
        myFile = file;
        myExtension = dotNetModuleExtension;
    }

    @Override
    public Path getFile() {
        return myFile;
    }

    @Override
    public @Nullable ValidityState getValidityState() {
        return new TimestampValidityState(CompilerUtil.lastModified(myFile));
    }

    public @Nullable DotNetRunModuleExtension<?> getExtension() {
        return myExtension;
    }
}
