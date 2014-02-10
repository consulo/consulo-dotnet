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

package org.mustbe.consulo.nunit.module.extension;

import javax.swing.Icon;

import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.nunit.NUnitIcons;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 10.02.14
 */
public class NUnitModuleExtensionProvider implements ModuleExtensionProvider<NUnitModuleExtension, NUnitMutableModuleExtension>
{
	@Nullable
	@Override
	public Icon getIcon()
	{
		return NUnitIcons.NUnit;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "NUnit";
	}

	@NotNull
	@Override
	public NUnitModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new NUnitModuleExtension(s, module);
	}

	@NotNull
	@Override
	public NUnitMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module)
	{
		return new NUnitMutableModuleExtension(s, module);
	}
}
