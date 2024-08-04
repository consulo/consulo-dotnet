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

package consulo.dotnet.module.extension;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.compiler.DotNetCompileFailedException;
import consulo.dotnet.compiler.DotNetCompilerOptionsBuilder;
import consulo.language.file.LanguageFileType;
import consulo.language.psi.PsiElement;
import consulo.module.extension.ModuleExtension;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public interface DotNetModuleLangExtension<T extends DotNetModuleLangExtension<T>> extends ModuleExtension<T>
{
	@Nonnull
	@RequiredReadAction
	PsiElement[] getEntryPointElements();

	@Nonnull
	LanguageFileType getFileType();

	@Nullable
	@RequiredReadAction
	String getAssemblyTitle();

	@RequiredReadAction
	default boolean isInternalsVisibleTo(@Nonnull String assemblyName)
	{
		return getInternalsVisibleToAssemblies().contains(assemblyName);
	}

	@Nonnull
	@RequiredReadAction
	default Set<String> getInternalsVisibleToAssemblies()
	{
		return Collections.emptySet();
	}

	@Nonnull
	DotNetCompilerOptionsBuilder createCompilerOptionsBuilder() throws DotNetCompileFailedException;
}
