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

package consulo.dotnet.impl.roots.orderEntry;

import consulo.content.OrderRootType;
import consulo.content.RootProvider;
import consulo.content.impl.internal.RootProviderBaseImpl;
import consulo.dotnet.module.extension.DotNetModuleExtensionWithLibraryProviding;
import consulo.dotnet.module.extension.DotNetSimpleModuleExtension;
import consulo.ide.impl.idea.openapi.vfs.VfsUtil;
import consulo.module.content.layer.orderEntry.OrderEntry;
import consulo.module.content.layer.orderEntry.OrderEntryWithTracking;
import consulo.module.content.layer.orderEntry.RootPolicy;
import consulo.module.extension.ModuleExtension;
import consulo.module.impl.internal.ProjectRootManagerImpl;
import consulo.module.impl.internal.layer.ModuleRootLayerImpl;
import consulo.module.impl.internal.layer.orderEntry.ClonableOrderEntry;
import consulo.module.impl.internal.layer.orderEntry.LibraryOrderEntryBaseImpl;
import consulo.util.collection.ArrayUtil;
import consulo.util.collection.SmartList;
import consulo.util.lang.Comparing;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
