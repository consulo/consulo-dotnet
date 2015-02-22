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

package org.mustbe.consulo.dotnet.module.extension;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.jdom.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.dll.DotNetModuleFileType;
import org.mustbe.consulo.dotnet.externalAttributes.ExternalAttributesRootOrderType;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootLayer;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.types.BinariesOrderRootType;
import com.intellij.openapi.roots.types.DocumentationOrderRootType;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.ArchiveFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Processor;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 22.02.2015
 */
public class BaseDotNetSimpleModuleExtension<S extends BaseDotNetModuleExtension<S>> extends ModuleExtensionImpl<S> implements
		DotNetSimpleModuleExtension<S>
{
	public static final File[] EMPTY_FILE_ARRAY = new File[0];

	protected Sdk myLastSdk;

	protected List<String> myVariables = new ArrayList<String>();
	protected Map<String, Map<OrderRootType, String[]>> myUrlsCache = new HashMap<String, Map<OrderRootType, String[]>>();

	public BaseDotNetSimpleModuleExtension(@NotNull String id, @NotNull ModuleRootLayer moduleRootLayer)
	{
		super(id, moduleRootLayer);
	}

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

	@NotNull
	@Override
	public Map<String, String> getAvailableSystemLibraries()
	{
		Map<String, String> map = new TreeMap<String, String>();
		File[] directoriesForLibraries = getFilesForLibraries();
		for(File childFile : directoriesForLibraries)
		{
			if(!FileUtilRt.getExtension(childFile.getName()).equals("dll"))
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

	@Nullable
	public Sdk getSdk()
	{
		return null;
	}

	@NotNull
	@Override
	public String[] getSystemLibraryUrls(@NotNull String name, @NotNull OrderRootType orderRootType)
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
			myUrlsCache.put(name, orderRootTypeMap = new HashMap<OrderRootType, String[]>());
		}

		String[] urls = orderRootTypeMap.get(orderRootType);
		if(urls == null)
		{
			orderRootTypeMap.put(orderRootType, urls = getSystemLibraryUrlsImpl(sdk, name, orderRootType));
		}

		return urls;
	}

	@Nullable
	private File getLibraryByAssemblyName(@NotNull final String name, @Nullable Ref<Couple<String>> cache)
	{
		File[] filesForLibraries = getFilesForLibraries();
		val nameWithExtension = name + ".dll";
		File singleFile = ContainerUtil.find(filesForLibraries, new Condition<File>()
		{
			@Override
			public boolean value(File file)
			{
				return file.getName().equalsIgnoreCase(nameWithExtension);
			}
		});

		if(singleFile != null && isValidLibrary(singleFile, name, cache))
		{
			return singleFile;
		}

		for(File childFile : filesForLibraries)
		{
			if(isValidLibrary(childFile, name, cache))
			{
				return childFile;
			}
		}
		return null;
	}

	@NotNull
	protected String[] getSystemLibraryUrlsImpl(@NotNull Sdk sdk, String name, OrderRootType orderRootType)
	{
		if(orderRootType == BinariesOrderRootType.getInstance())
		{
			File libraryByAssemblyName = getLibraryByAssemblyName(name, null);
			if(libraryByAssemblyName == null)
			{
				return ArrayUtil.EMPTY_STRING_ARRAY;
			}

			return new String[]{
					VirtualFileManager.constructUrl(DotNetModuleFileType.PROTOCOL, libraryByAssemblyName.getPath()) + ArchiveFileSystem
							.ARCHIVE_SEPARATOR
			};
		}
		else if(orderRootType == DocumentationOrderRootType.getInstance())
		{
			String nameWithoutExtension = FileUtil.getNameWithoutExtension(name);
			return new String[]{sdk.getHomePath() + "/" + nameWithoutExtension + ".xml"};
		}
		else if(orderRootType == ExternalAttributesRootOrderType.getInstance())
		{
			try
			{
				final Ref<Couple<String>> ref = Ref.create();
				File libraryFile = getLibraryByAssemblyName(name, ref);
				if(libraryFile == null)
				{
					return ArrayUtil.EMPTY_STRING_ARRAY;
				}

				assert ref.get() != null;

				PluginClassLoader classLoader = (PluginClassLoader) getClass().getClassLoader();
				IdeaPluginDescriptor plugin = PluginManager.getPlugin(classLoader.getPluginId());
				assert plugin != null;
				File dir = new File(plugin.getPath(), "externalAttributes");

				val urls = new SmartList<String>();
				val requiredFileName = name + ".xml";
				FileUtil.visitFiles(dir, new Processor<File>()
				{
					@Override
					public boolean process(File file)
					{
						if(file.isDirectory())
						{
							return true;
						}
						if(Comparing.equal(requiredFileName, file.getName(), false) && isValidExternalFile(ref.get().getSecond(), file))
						{
							urls.add(VfsUtil.pathToUrl(file.getPath()));
						}
						return true;
					}
				});

				return ArrayUtil.toStringArray(urls);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ArrayUtil.EMPTY_STRING_ARRAY;
	}

	private boolean isValidLibrary(@NotNull File file, @NotNull final String name, @Nullable Ref<Couple<String>> cache)
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

	@NotNull
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
		try
		{
			val moduleParser = DotNetLibraryOpenCache.acquire(f.getPath());
			return Couple.of(moduleParser.getAssemblyInfo().getName(), moduleParser.getAssemblyInfo().getMajorVersion() + "." + moduleParser
					.getAssemblyInfo().getMinorVersion() +
					"." +
					moduleParser.getAssemblyInfo().getBuildNumber() + "." + moduleParser.getAssemblyInfo().getRevisionNumber());
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
