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

package consulo.msil.representation;

import consulo.component.extension.ExtensionPointName;
import consulo.msil.lang.psi.MsilFile;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public interface MsilFileRepresentationProvider
{
	ExtensionPointName<MsilFileRepresentationProvider> EP_NAME = ExtensionPointName.create("consulo.dotnet.msilFileRepresentation");

	@Nullable
	String getRepresentFileName(@Nonnull MsilFile msilFile);

	@Nonnull
	CharSequence buildContent(String fileName, @Nonnull final MsilFile msilFile);

	@Nonnull
	FileType getFileType();
}
