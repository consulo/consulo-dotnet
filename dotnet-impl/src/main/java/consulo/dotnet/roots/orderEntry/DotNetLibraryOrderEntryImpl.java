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

package consulo.dotnet.roots.orderEntry;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.dotnet.module.extension.DotNetModuleExtensionWithLibraryProviding;
import consulo.dotnet.module.extension.DotNetSimpleModuleExtension;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.RootPolicy;
import com.intellij.openapi.roots.RootProvider;
import com.intellij.openapi.roots.impl.ClonableOrderEntry;
import com.intellij.openapi.roots.impl.LibraryOrderEntryBaseImpl;
import com.intellij.openapi.roots.impl.ProjectRootManagerImpl;
import com.intellij.openapi.roots.impl.RootProviderBaseImpl;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ArrayUtil;
import com.intellij.util.SmartList;
import consulo.module.extension.ModuleExtension;
import consulo.roots.OrderEntryWithTracking;
import consulo.roots.impl.ModuleRootLayerImpl;

/**
 * @author VISTALL
 * @since 21.08.14
 */
public class DotNetLibraryOrderEntryImpl extends LibraryOrderEntryBaseImpl implements ClonableOrderEntry, OrderEntryWithTracking
{
	private RootProvider myRootProvider = new RootProviderBaseImpl()
	{
		@Nonnull
		@Override
		public String[] getUrls(@Nonnull OrderRootType rootType)
		{
			DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
			if(extension == null)
			{
				return ArrayUtil.EMPTY_STRING_ARRAY;
			}

			String[] urls = ArrayUtil.EMPTY_STRING_ARRAY;
			for(ModuleExtension moduleExtension : myModuleRootLayer.getExtensions())
			{
				if(moduleExtension instanceof DotNetModuleExtensionWithLibraryProviding)
				{
					String[] systemLibraryUrls = ((DotNetModuleExtensionWithLibraryProviding) moduleExtension).getSystemLibraryUrls(getPresentableName()
							, rootType);
					urls = ArrayUtil.mergeArrays(urls, systemLibraryUrls);
				}
			}
			return urls;
		}

		@Nonnull
		@Override
		public VirtualFile[] getFiles(@Nonnull OrderRootType rootType)
		{
			DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
			if(extension == null)
			{
				return VirtualFile.EMPTY_ARRAY;
			}

			List<VirtualFile> virtualFiles = new SmartList<VirtualFile>();
			for(ModuleExtension moduleExtension : myModuleRootLayer.getExtensions())
			{
				if(moduleExtension instanceof DotNetModuleExtensionWithLibraryProviding)
				{
					String[] systemLibraryUrls = ((DotNetModuleExtensionWithLibraryProviding) moduleExtension).getSystemLibraryUrls(getPresentableName()
							, rootType);
					for(String systemLibraryUrl : systemLibraryUrls)
					{
						VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(systemLibraryUrl);
						if(fileByUrl != null)
						{
							virtualFiles.add(fileByUrl);
						}
					}
				}
			}
			return VfsUtil.toVirtualFileArray(virtualFiles);
		}
	};

	private String myName;

	public DotNetLibraryOrderEntryImpl(@Nonnull ModuleRootLayerImpl rootLayer, String name)
	{
		this(rootLayer, name, true);
	}

	public DotNetLibraryOrderEntryImpl(@Nonnull ModuleRootLayerImpl rootLayer, String name, boolean init)
	{
		super(DotNetLibraryOrderEntryType.getInstance(), rootLayer, ProjectRootManagerImpl.getInstanceImpl(rootLayer.getProject()));
		myName = name;
		if(init)
		{
			init();

			myProjectRootManagerImpl.addOrderWithTracking(this);
		}
	}

	@Nullable
	@Override
	public Object getEqualObject()
	{
		DotNetSimpleModuleExtension extension = myModuleRootLayer.getExtension(DotNetSimpleModuleExtension.class);
		if(extension == null)
		{
			return VirtualFile.EMPTY_ARRAY;
		}
		return extension.getSdk();
	}

	@Nullable
	@Override
	public RootProvider getRootProvider()
	{
		return myRootProvider;
	}

	@Nonnull
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
	public boolean isEquivalentTo(@Nonnull OrderEntry entry)
	{
		return entry instanceof DotNetLibraryOrderEntryImpl && Comparing.equal(myName, entry.getPresentableName());
	}

	@Override
	public boolean isSynthetic()
	{
		return false;
	}

	@Override
	public OrderEntry cloneEntry(ModuleRootLayerImpl layer)
	{
		return new DotNetLibraryOrderEntryImpl(layer, getPresentableName());
	}
}
