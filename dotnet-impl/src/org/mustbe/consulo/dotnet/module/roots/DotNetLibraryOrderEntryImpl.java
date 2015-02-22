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

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.module.extension.DotNetSimpleModuleExtension;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderEntryWithTracking;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.RootPolicy;
import com.intellij.openapi.roots.impl.ClonableOrderEntry;
import com.intellij.openapi.roots.impl.ModuleRootLayerImpl;
import com.intellij.openapi.roots.impl.OrderEntryBaseImpl;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ArrayUtil;

/**
 * @author VISTALL
 * @since 21.08.14
 */
public class DotNetLibraryOrderEntryImpl extends OrderEntryBaseImpl implements ClonableOrderEntry, OrderEntryWithTracking
{
	private String myName;

	public DotNetLibraryOrderEntryImpl(@NotNull ModuleRootLayerImpl rootLayer, String name)
	{
		super(DotNetLibraryOrderEntryTypeProvider.getInstance(), rootLayer);
		myName = name;
	}

	@NotNull
	@Override
	public VirtualFile[] getFiles(OrderRootType orderRootType)
	{
		DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
		if(extension == null)
		{
			return VirtualFile.EMPTY_ARRAY;
		}

		String[] systemLibrary = extension.getSystemLibraryUrls(getPresentableName(), orderRootType);
		List<VirtualFile> virtualFiles = new ArrayList<VirtualFile>(systemLibrary.length);
		for(String url : systemLibrary)
		{
			VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(url);
			if(fileByUrl != null)
			{
				virtualFiles.add(fileByUrl);
			}
		}
		return VfsUtil.toVirtualFileArray(virtualFiles);
	}

	@NotNull
	@Override
	public String[] getUrls(OrderRootType orderRootType)
	{
		DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
		if(extension == null)
		{
			return ArrayUtil.EMPTY_STRING_ARRAY;
		}

		return extension.getSystemLibraryUrls(getPresentableName(), orderRootType);
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
		return myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class) != null;
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
	public boolean isEquivalentTo(@NotNull OrderEntry entry)
	{
		return entry instanceof DotNetLibraryOrderEntryImpl && Comparing.equal(myName, entry.getPresentableName());
	}

	@Override
	public boolean isSynthetic()
	{
		return false; // allow delete
	}

	@Override
	public OrderEntry cloneEntry(ModuleRootLayerImpl layer)
	{
		return new DotNetLibraryOrderEntryImpl(layer, getPresentableName());
	}
}
