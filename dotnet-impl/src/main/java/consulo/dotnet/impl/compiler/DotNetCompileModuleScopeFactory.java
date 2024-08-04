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
import consulo.compiler.scope.CompileModuleScopeFactory;
import consulo.compiler.scope.FileIndexCompileScope;
import consulo.compiler.scope.ModuleRootCompileScope;
import consulo.dotnet.module.extension.DotNetModuleExtension;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import jakarta.annotation.Nonnull;

import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 20.12.13.
 */
@ExtensionImpl
public class DotNetCompileModuleScopeFactory implements CompileModuleScopeFactory
{
	@Nullable
	@Override
	public FileIndexCompileScope createScope(@Nonnull Module module, boolean includeDependentModules, boolean includeTestScope)
	{
		DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
		if(extension != null && !extension.isAllowSourceRoots())
		{
			return new ModuleRootCompileScope(module, includeDependentModules);
		}
		return null;
	}
}
