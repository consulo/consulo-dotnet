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

package org.mustbe.consulo.dotnet.module.roots;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.roots.OrderEntryTypeProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.RootPolicy;
import com.intellij.openapi.roots.impl.ClonableOrderEntry;
import com.intellij.openapi.roots.impl.ModuleRootLayerImpl;
import com.intellij.openapi.roots.impl.OrderEntryBaseImpl;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 21.08.14
 */
public class DotNetLibraryOrderEntryImpl extends OrderEntryBaseImpl implements ClonableOrderEntry
{
	private String myName;

	public DotNetLibraryOrderEntryImpl(@NotNull OrderEntryTypeProvider<?> provider, @NotNull ModuleRootLayerImpl rootLayer, String name)
	{
		super(provider, rootLayer);
		myName = name;
	}

	@NotNull
	@Override
	public VirtualFile[] getFiles(OrderRootType orderRootType)
	{
		return new VirtualFile[0];
	}

	@NotNull
	@Override
	public String[] getUrls(OrderRootType orderRootType)
	{
		return new String[0];
	}

	@NotNull
	@Override
	public String getPresentableName()
	{
		return myName;
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	@NotNull
	@Override
	public Module getOwnerModule()
	{
		return getRootModel().getModule();
	}

	@Override
	public <R> R accept(RootPolicy<R> rRootPolicy, @Nullable R r)
	{
		if(rRootPolicy instanceof DotNetRootPolicy)
		{
			return (R) ((DotNetRootPolicy) rRootPolicy).visitDotNetLibrary(this, r);
		}
		else
		{
			return rRootPolicy.visitOrderEntry(this, r);
		}
	}

	@Override
	public boolean isSynthetic()
	{
		return true;
	}

	@Override
	public OrderEntry cloneEntry(ModuleRootLayerImpl layer)
	{
		return new DotNetLibraryOrderEntryImpl(getProvider(), layer, getPresentableName());
	}
}
