/*
 * Copyright 2013-2020 consulo.io
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

package consulo.dotnet.impl.assembly;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.assembly.AssemblyModule;
import consulo.dotnet.module.extension.DotNetModuleLangExtension;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.util.lang.ObjectUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 14-Jun-17
 */
class ConsuloModuleAsAssemblyModule implements AssemblyModule
{
	@Nonnull
	private final Module myModule;
	@Nullable
	private final DotNetModuleLangExtension<?> myLangExtension;

	@RequiredReadAction
	ConsuloModuleAsAssemblyModule(@Nonnull Module module)
	{
		myModule = module;
		myLangExtension = ModuleUtilCore.getExtension(module, DotNetModuleLangExtension.class);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public String getName()
	{
		String assemblyTitle = myLangExtension == null ? null : myLangExtension.getAssemblyTitle();
		return ObjectUtil.notNull(assemblyTitle, myModule.getName());
	}

	@RequiredReadAction
	@Override
	public boolean isAllowedAssembly(@Nonnull String assemblyName)
	{
		DotNetModuleLangExtension<?> langExtension = myLangExtension;
		return langExtension == null || langExtension.isInternalsVisibleTo(assemblyName);
	}

	@Override
	public boolean equals(@Nonnull AssemblyModule module)
	{
		return module instanceof ConsuloModuleAsAssemblyModule && myModule.equals(((ConsuloModuleAsAssemblyModule) module).myModule);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ConsuloModuleAsAssemblyModule && ((ConsuloModuleAsAssemblyModule) obj).myModule.equals(myModule);
	}
}
