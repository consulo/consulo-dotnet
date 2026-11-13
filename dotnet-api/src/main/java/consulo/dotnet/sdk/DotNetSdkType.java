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

package consulo.dotnet.sdk;

import consulo.container.plugin.PluginManager;
import consulo.content.bundle.PlatformAwareSdkType;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkModificator;
import consulo.dotnet.externalAttributes.ExternalAttributesRootOrderType;
import consulo.dotnet.module.extension.BaseDotNetSimpleModuleExtension;
import consulo.localize.LocalizeValue;
import consulo.logging.Logger;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.util.VirtualFileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * A .NET bundle of any machine.
 *
 * @author VISTALL
 * @since 20.12.13.
 */
public abstract class DotNetSdkType extends PlatformAwareSdkType {
    private static final Logger LOG = Logger.getInstance(DotNetSdkType.class);

    protected DotNetSdkType(String id, LocalizeValue displayName, Image icon) {
        super(id, displayName, icon);
    }

    @Override
    public boolean isRootTypeApplicable(String type) {
        return ExternalAttributesRootOrderType.ID.equals(type);
    }

    /**
     * The external attributes are files of the IDE - the bundle of any machine gets them.
     */
    @Override
    public void setupSdkPaths(Sdk sdk) {
        SdkModificator sdkModificator = sdk.getSdkModificator();

        Path dir = PluginManager.getPluginPath(BaseDotNetSimpleModuleExtension.class).toPath().resolve("externalAttributes");

        try (Stream<Path> files = Files.walk(dir)) {
            files.filter(file -> Files.isRegularFile(file) && file.getFileName().toString().endsWith(".xml"))
                .forEach(file -> sdkModificator.addRoot(VirtualFileUtil.pathToUrl(file.toString()), ExternalAttributesRootOrderType.ID));
        }
        catch (IOException e) {
            LOG.warn(e);
        }

        sdkModificator.commitChanges();
    }

    public Path getLoaderFile(Sdk sdk) {
        return getLoaderFile(getClass(), "loader.exe");
    }

    protected static Path getLoaderFile(Class<?> clazz, String fileName) {
        return PluginManager.getPluginPath(clazz).toPath().resolve("loader").resolve(fileName);
    }
}
