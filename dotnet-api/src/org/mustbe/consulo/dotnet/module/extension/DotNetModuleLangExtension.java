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

package org.mustbe.consulo.dotnet.module.extension;

import org.consulo.module.extension.ModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.compiler.DotNetCompilerOptionsBuilder;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public interface DotNetModuleLangExtension<T extends DotNetModuleLangExtension<T>> extends ModuleExtension<T>
{
	@NotNull
	LanguageFileType getFileType();

	@NotNull
	DotNetCompilerOptionsBuilder createCompilerOptionsBuilder(@NotNull Sdk dotNetSdk);
}
