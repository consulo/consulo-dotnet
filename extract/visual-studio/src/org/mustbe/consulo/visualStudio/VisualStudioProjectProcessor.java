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

package org.mustbe.consulo.visualStudio;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 27.03.14
 */
public interface VisualStudioProjectProcessor
{
	ExtensionPointName<VisualStudioProjectProcessor> EP_NAME = ExtensionPointName.create("org.mustbe.consulo.visualStudio.projectProcessor");

	@NotNull
	FileType getFileType();

	void processFile(@NotNull VirtualFile projectFile, @NotNull ModifiableRootModel moduleWithSingleContent);
}
