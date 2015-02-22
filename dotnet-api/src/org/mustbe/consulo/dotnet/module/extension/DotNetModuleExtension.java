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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.execution.DebugConnectionInfo;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 20.11.13.
 */
public interface DotNetModuleExtension<T extends DotNetModuleExtension<T>> extends DotNetSimpleModuleExtension<T>
{
	String MODULE_OUTPUT_DIR = "$ModuleProductionOutputDirPath$";

	String CONFIGURATION = "$ModuleProfileName$";

	String MODULE_NAME = "$ModuleName$";

	String OUTPUT_FILE_EXT = "$TargetFileExt$";

	String DEFAULT_FILE_NAME = MODULE_NAME + "." + OUTPUT_FILE_EXT;

	String DEFAULT_OUTPUT_DIR = MODULE_OUTPUT_DIR;


	boolean isAllowSourceRoots();

	@NotNull
	DotNetTarget getTarget();

	boolean isAllowDebugInfo();

	@NotNull
	String getFileName();

	@NotNull
	String getNamespacePrefix();

	@NotNull
	String getOutputDir();

	@Nullable
	String getMainType();

	@NotNull
	GeneralCommandLine createDefaultCommandLine(@NotNull Sdk sdk, @Nullable DebugConnectionInfo debugConnectionInfo) throws ExecutionException;

	@NotNull
	String getDebugFileExtension();

	@NotNull
	@RequiredReadAction
	PsiElement[] getEntryPointElements();
}
