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

import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayer;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayerImpl;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import org.mustbe.consulo.module.extension.LayeredModuleExtension;
import org.mustbe.consulo.module.extension.LayeredModuleExtensionImpl;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public abstract class DotNetModuleExtensionImpl<S extends DotNetModuleExtensionImpl<S>> extends LayeredModuleExtensionImpl<S> implements
		DotNetModuleExtension<S>
{
	public DotNetModuleExtensionImpl(@NotNull String id, @NotNull ModifiableRootModel module)
	{
		super(id, module);
	}

	@Override
	protected void init(boolean debug, @NotNull ConfigurationLayer layer)
	{
		if(debug)
		{
			MainConfigurationLayerImpl d = (MainConfigurationLayerImpl) layer;
			d.getVariables().add("DEBUG");
			d.setAllowDebugInfo(true);
		}
	}

	@NotNull
	@Override
	public GlobalSearchScope getScopeForResolving(boolean test)
	{
		if(isAllowSourceRoots())
		{
			return GlobalSearchScope.moduleRuntimeScope(getModule(), test);
		}
		else
		{
			return GlobalSearchScopes.directoryScope(getProject(), getModule().getModuleDir(), true);
		}
	}

	@Override
	public boolean isAllowSourceRoots()
	{
		MainConfigurationLayer currentProfileEx = (MainConfigurationLayer) getCurrentLayer();
		return currentProfileEx.isAllowSourceRoots();
	}

	@NotNull
	@Override
	protected ConfigurationLayer createLayer()
	{
		return new MainConfigurationLayerImpl(this);
	}

	@NotNull
	@Override
	public Class<? extends LayeredModuleExtension> getHeadClass()
	{
		return DotNetModuleExtension.class;
	}

	@NotNull
	@Override
	public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		MainConfigurationLayer currentProfileEx = (MainConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk();
	}

	@Nullable
	@Override
	public Sdk getSdk()
	{
		MainConfigurationLayer currentProfileEx = (MainConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk().get();
	}

	@Nullable
	@Override
	public String getSdkName()
	{
		MainConfigurationLayer currentProfileEx = (MainConfigurationLayer) getCurrentLayer();
		return currentProfileEx.getInheritableSdk().getName();
	}
}
