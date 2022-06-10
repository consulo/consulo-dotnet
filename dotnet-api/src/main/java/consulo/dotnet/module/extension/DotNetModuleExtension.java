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

import consulo.annotation.DeprecationInfo;
import consulo.annotation.access.RequiredReadAction;
import consulo.content.bundle.Sdk;
import consulo.language.psi.PsiElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public interface DotNetModuleExtension<T extends DotNetModuleExtension<T>> extends DotNetSimpleModuleExtension<T>, DotNetRunModuleExtension<T>
{
	String MODULE_NAME = "$ModuleName$";

	String OUTPUT_FILE_EXT = "$TargetFileExt$";

	String DEFAULT_FILE_NAME = MODULE_NAME + "." + OUTPUT_FILE_EXT;

	String DEFAULT_OUTPUT_DIR = "";


	@Nullable
	Sdk getSdk();

	@Deprecated
	@DeprecationInfo("Dead - always return false")
	default boolean isAllowSourceRoots()
	{
		return false;
	}

	@Nonnull
	String getNamespacePrefix();

	@Nullable
	String getMainType();

	@Nonnull
	@RequiredReadAction
	PsiElement[] getEntryPointElements();
}
