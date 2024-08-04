/*
 * Copyright 2013-2015 must-be.org
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

package consulo.dotnet.module.extension;

import consulo.annotation.access.RequiredReadAction;
import consulo.container.plugin.PluginManager;
import consulo.content.OrderRootType;
import consulo.content.base.BinariesOrderRootType;
import consulo.content.base.DocumentationOrderRootType;
import consulo.content.bundle.Sdk;
import consulo.dotnet.dll.DotNetModuleFileType;
import consulo.dotnet.externalAttributes.ExternalAttributesRootOrderType;
import consulo.dotnet.module.DotNetNamespaceGeneratePolicy;
import consulo.internal.dotnet.asm.mbel.AssemblyInfo;
import consulo.logging.Logger;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionBase;
import consulo.module.extension.ModuleInheritableNamedPointer;
import consulo.util.collection.ArrayUtil;
import consulo.util.collection.ContainerUtil;
import consulo.util.collection.SmartList;
import consulo.util.io.FileUtil;
import consulo.util.io.URLUtil;
import consulo.util.jdom.JDOMUtil;
import consulo.util.lang.Comparing;
import consulo.util.lang.Couple;
import consulo.util.lang.ref.SimpleReference;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.virtualFileSystem.archive.ArchiveVfsUtil;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.jdom.Document;
import org.jdom.Element;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author VISTALL
 * @since 22.02.2015
 */
public abstract class BaseDotNetSimpleModuleExtension<S extends BaseDotNetSimpleModuleExtension<S>> extends ModuleExtensionBase<S> implements DotNetSimpleModuleExtension<S>
{
	public static final Logger LOGGER = Logger.getInstance(BaseDotNetSimpleModuleExtension.class);

	public static final File[] EMPTY_FILE_ARRAY = new File[0];

	protected Sdk myLastSdk;

	protected List<String> myVariables = new ArrayList<>();
	protected Map<String, Map<OrderRootType, String[]>> myUrlsCache = new HashMap<>();
	protected DotNetModuleSdkPointer mySdkPointer;

	public BaseDotNetSimpleModuleExtension(@Nonnull String id, @Nonnull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
		mySdkPointer = new DotNetModuleSdkPointer(moduleRootLayer.getProject(), id);
	}

	@Nonnull
	@Override
	public List<String> getVariables()
	{
		return myVariables;
	}

	@Override
	public boolean isSupportCompilation()
	{
		return false;
	}

	@Nonnull
	@Override
	public DotNetNamespaceGeneratePolicy getNamespaceGeneratePolicy()
	{
		return DotNetNamespaceGeneratePolicy.DEFAULT;
	}

	@Nonnull
	@Override
	public Map<String, String> getAvailableSystemLibraries()
	{
		Map<String, String> map = new TreeMap<>();
		File[] directoriesForLibraries = getFilesForLibraries();
		for(File childFile : directoriesForLibraries)
		{
			if(!DotNetModuleFileType.isDllFile(childFile.getName()))
			{
				continue;
			}
			Couple<String> info = parseLibrary(childFile);
			if(info == null)
			{
				continue;
			}
			map.put(info.getFirst(), info.getSecond());
		}
		return map;
	}

	@Nonnull
	@Override
	public ModuleInheritableNamedPointer<Sdk> getInheritableSdk()
	{
		return mySdkPointer;
	}

	@Nullable
	@Override
	public Sdk getSdk()
	{
		return getInheritableSdk().get();
	}

	@Nullable
	@Override
	public String getSdkName()
	{
		return getInheritableSdk().getName();
	}

	@Nonnull
	@Override
	public String[] getSystemLibraryUrls(@Nonnull String name, @Nonnull OrderRootType orderRootType)
	{
		Sdk sdk = getSdk();
		if(sdk == null)
		{
			return ArrayUtil.EMPTY_STRING_ARRAY;
		}

		if(!Comparing.equal(myLastSdk, sdk))
		{
			myLastSdk = sdk;
			myUrlsCache.clear();
		}

		Map<OrderRootType, String[]> orderRootTypeMap = myUrlsCache.get(name);
		if(orderRootTypeMap == null)
		{
			myUrlsCache.put(name, orderRootTypeMap = new HashMap<>());
		}

		String[] urls = orderRootTypeMap.get(orderRootType);
		if(urls == null)
		{
			orderRootTypeMap.put(orderRootType, urls = getSystemLibraryUrlsImpl(sdk, name, orderRootType));
		}

		return urls;
	}

	@RequiredReadAction
	@Override
	protected void loadStateImpl(@Nonnull Element element)
	{
		mySdkPointer.fromXml(element);

		for(Element defineElement : element.getChildren("define"))
		{
			myVariables.add(defineElement.getText());
		}
	}

	@Override
	protected void getStateImpl(@Nonnull Element element)
	{
		mySdkPointer.toXml(element);
		for(String variable : myVariables)
		{
			element.addContent(new Element("define").setText(variable));
		}
	}

	@RequiredReadAction
	@Override
	public void commit(@Nonnull S mutableModuleExtension)
	{
		super.commit(mutableModuleExtension);

		mySdkPointer.set(mutableModuleExtension.getInheritableSdk());
		myVariables.clear();
		myVariables.addAll(mutableModuleExtension.myVariables);
	}

	public boolean isModifiedImpl(S ex)
	{
		return myIsEnabled != ex.isEnabled() ||
				!mySdkPointer.equals(ex.getInheritableSdk()) ||
				!myVariables.equals(ex.getVariables());
	}

	@Nullable
	private File getLibraryByAssemblyName(@Nonnull final String name, @Nullable SimpleReference<Couple<String>> cache)
	{
		File[] filesForLibraries = getFilesForLibraries();
		String nameWithExtension = name + ".dll";
		File singleFile = ContainerUtil.find(filesForLibraries, file -> file.getName().equalsIgnoreCase(nameWithExtension));

		if(singleFile != null && isValidLibrary(singleFile, name, cache))
		{
			return singleFile;
		}

		for(File childFile : filesForLibraries)
		{
			if(DotNetModuleFileType.isDllFile(childFile.getName()) && isValidLibrary(childFile, name, cache))
			{
				return childFile;
			}
		}
		return null;
	}

	@Nonnull
	protected String[] getSystemLibraryUrlsImpl(@Nonnull Sdk sdk, String name, OrderRootType orderRootType)
	{
		if(orderRootType == BinariesOrderRootType.getInstance())
		{
			File libraryByAssemblyName = getLibraryByAssemblyName(name, null);
			if(libraryByAssemblyName == null)
			{
				return ArrayUtil.EMPTY_STRING_ARRAY;
			}

			return new String[]{
					VirtualFileManager.constructUrl(DotNetModuleFileType.PROTOCOL, libraryByAssemblyName.getPath()) + URLUtil.ARCHIVE_SEPARATOR
			};
		}
		else if(orderRootType == DocumentationOrderRootType.getInstance())
		{
			String[] systemLibraryUrls = getSystemLibraryUrls(name, BinariesOrderRootType.getInstance());
			if(systemLibraryUrls.length != 1)
			{
				return ArrayUtil.EMPTY_STRING_ARRAY;
			}
			VirtualFile libraryFile = VirtualFileManager.getInstance().findFileByUrl(systemLibraryUrls[0]);
			if(libraryFile == null)
			{
				return ArrayUtil.EMPTY_STRING_ARRAY;
			}
			VirtualFile localFile = ArchiveVfsUtil.getVirtualFileForArchive(libraryFile);
			if(localFile == null)
			{
				return ArrayUtil.EMPTY_STRING_ARRAY;
			}
			VirtualFile docFile = localFile.getParent().findChild(localFile.getNameWithoutExtension() + ".xml");
			if(docFile != null)
			{
				return new String[]{docFile.getUrl()};
			}
			return ArrayUtil.EMPTY_STRING_ARRAY;
		}
		else if(orderRootType == ExternalAttributesRootOrderType.getInstance())
		{
			try
			{
				final SimpleReference<Couple<String>> ref = SimpleReference.create();
				File libraryFile = getLibraryByAssemblyName(name, ref);
				if(libraryFile == null)
				{
					return ArrayUtil.EMPTY_STRING_ARRAY;
				}

				assert ref.get() != null;

				File dir = new File(PluginManager.getPluginPath(BaseDotNetSimpleModuleExtension.class), "externalAttributes");

				List<String> urls = new SmartList<>();
				String requiredFileName = name + ".xml";
				FileUtil.visitFiles(dir, file ->
				{
					if(file.isDirectory())
					{
						return true;
					}
					if(Comparing.equal(requiredFileName, file.getName(), false) && isValidExternalFile(ref.get().getSecond(), file))
					{
						urls.add(VirtualFileUtil.pathToUrl(file.getPath()));
					}
					return true;
				});

				return ArrayUtil.toStringArray(urls);
			}
			catch(Exception e)
			{
				LOGGER.error(e);
			}
		}
		return ArrayUtil.EMPTY_STRING_ARRAY;
	}

	private boolean isValidLibrary(@Nonnull File file, @Nonnull final String name, @Nullable SimpleReference<Couple<String>> cache)
	{
		Couple<String> info = parseLibrary(file);
		if(info == null)
		{
			return false;
		}
		if(Comparing.equal(info.getFirst(), name))
		{
			if(cache != null)
			{
				cache.set(info);
			}
			return true;
		}
		return false;
	}

	private static boolean isValidExternalFile(String version, File toCheck)
	{
		try
		{
			Document document = JDOMUtil.loadDocument(toCheck);
			String versionPattern = document.getRootElement().getChildText("version");
			return Pattern.compile(versionPattern).matcher(version).find();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Nonnull
	public File[] getFilesForLibraries()
	{
		Sdk sdk = getSdk();
		if(sdk == null)
		{
			return EMPTY_FILE_ARRAY;
		}

		String homePath = sdk.getHomePath();
		if(homePath == null)
		{
			return EMPTY_FILE_ARRAY;
		}
		File[] files = new File(homePath).listFiles();
		return files == null ? EMPTY_FILE_ARRAY : files;
	}

	@Nullable
	private static Couple<String> parseLibrary(File f)
	{
		AssemblyInfo assemblyInfo = AssemblyInfoCacheService.getInstance().getAssemblyInfo(f);
		if(assemblyInfo == null)
		{
			return null;
		}
		String ver = assemblyInfo.getMajorVersion() + "." + assemblyInfo.getMinorVersion() + "." + assemblyInfo.getBuildNumber() + "." + assemblyInfo.getRevisionNumber();
		return Couple.of(assemblyInfo.getName(), ver);
	}
}
