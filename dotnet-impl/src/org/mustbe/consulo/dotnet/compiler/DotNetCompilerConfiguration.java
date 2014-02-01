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

package org.mustbe.consulo.dotnet.compiler;

import org.consulo.lombok.annotations.ProjectService;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.components.StorageScheme;

/**
 * @author VISTALL
 * @since 27.11.13.
 */
@State(
		name = "DotNetCompilerConfiguration",
		storages = {
				@Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/compiler.xml", scheme = StorageScheme.DIRECTORY_BASED)
		})
@ProjectService
public class DotNetCompilerConfiguration implements PersistentStateComponent<Element>
{
	@NotNull
	public String getOutputFile()
	{
		return getOutputDir() + "/" + DotNetMacros.MODULE_NAME + "." + DotNetMacros.OUTPUT_FILE_EXT;
	}

	@NotNull
	public String getOutputDir()
	{
		return DotNetMacros.MODULE_OUTPUT_DIR + "/" + DotNetMacros.CONFIGURATION;
	}

	@Nullable
	@Override
	public Element getState()
	{
		return null;
	}

	@Override
	public void loadState(Element element)
	{

	}
}
