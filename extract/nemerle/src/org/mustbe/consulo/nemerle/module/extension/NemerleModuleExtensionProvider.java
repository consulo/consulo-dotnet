/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.nemerle.module.extension;

import javax.swing.Icon;

import org.consulo.module.extension.ModuleExtensionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.nemerle.NemerleIcons;
import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 25.12.13.
 */
public class NemerleModuleExtensionProvider implements ModuleExtensionProvider<NemerleModuleExtension, NemerleMutableModuleExtension>
{
	@Nullable
	@Override
	public Icon getIcon()
	{
		return NemerleIcons.Nemerle;
	}

	@NotNull
	@Override
	public String getName()
	{
		return "Nemerle";
	}

	@NotNull
	@Override
	public NemerleModuleExtension createImmutable(@NotNull String s, @NotNull Module module)
	{
		return new NemerleModuleExtension(s, module);
	}

	@NotNull
	@Override
	public NemerleMutableModuleExtension createMutable(@NotNull String s, @NotNull Module module)
	{
		return new NemerleMutableModuleExtension(s, module);
	}
}
