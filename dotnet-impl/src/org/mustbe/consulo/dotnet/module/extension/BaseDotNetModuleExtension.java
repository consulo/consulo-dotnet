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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.consulo.module.extension.ModuleExtension;
import org.consulo.module.extension.ModuleInheritableNamedPointer;
import org.consulo.module.extension.impl.ModuleExtensionImpl;
import org.consulo.module.extension.impl.ModuleInheritableNamedPointerImpl;
import org.consulo.module.extension.impl.SdkModuleInheritableNamedPointerImpl;
import org.jdom.Document;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.DotNetTarget;
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
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.ArchiveFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopes;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Processor;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import edu.arizona.cs.mbel.mbel.ModuleParser;
import lombok.val;

/**
 * @author VISTALL
 * @since 10.01.14
 */
public abstract class BaseDotNetModuleExtension<S extends BaseDotNetModuleExtension<S>> extends ModuleExtensionImpl<S> implements
		DotNetModuleExtension<S>
{
	private Map<String, Map<OrderRootType, String[]>> myUrlsCache = new HashMap<String, Map<OrderRootType, String[]>>();
	private Sdk myLastSdk;

	private ModuleInheritableNamedPointerImpl<Sdk> mySdkPointer;
	protected DotNetTarget myTarget = DotNetTarget.EXECUTABLE;
	protected boolean myAllowDebugInfo;
	protected boolean myAllowSourceRoots;
	protected String myMainType;
	protected String myNamespacePrefix;
	protected List<String> myVariables = new ArrayList<String>();
	protected String myFileName = DEFAULT_FILE_NAME;
	protected String myOutputDirectory = DEFAULT_OUTPUT_DIR;

	public BaseDotNetModuleExtension(@NotNull String id, @NotNull ModuleRootLayer rootModel)
	{
		super(id, rootModel);
		mySdkPointer = new SdkModuleInheritableNamedPointerImpl(rootModel.getProject(), id);
	}

	public boolean isModifiedImpl(S ex)
	{
		return myIsEnabled != ex.isEnabled() ||
				!myTarget.equals(ex.myTarget) ||
				myAllowDebugInfo != ex.isAllowDebugInfo() ||
				!mySdkPointer.equals(ex.getInheritableSdk()) ||
				myAllowSourceRoots != ex.isAllowSourceRoots() ||
				!myVariables.equals(ex.getVariables()) ||
				!Comparing.equal(myMainType, ex.myMainType) ||
				!Comparing.equal(myNamespacePrefix, ex.myNamespacePrefix) ||
				!Comparing.equal(getFileName(), ex.getFileName()) ||
				!Comparing.equal(getOutputDir(), ex.getOutputDir());
	}

	@Override
	public void commit(@NotNull S mutableModuleExtension)
	{
		super.commit(mutableModuleExtension);

		mySdkPointer.set(mutableModuleExtension.getInheritableSdk());
		myTarget = mutableModuleExtension.myTarget;
		myAllowDebugInfo = mutableModuleExtension.myAllowDebugInfo;
		myAllowSourceRoots = mutableModuleExtension.myAllowSourceRoots;
		myMainType = mutableModuleExtension.myMainType;
		myFileName = mutableModuleExtension.myFileName;
		myNamespacePrefix = mutableModuleExtension.myNamespacePrefix;
		myOutputDirectory = mutableModuleExtension.myOutputDirectory;
		myVariables.clear();
		myVariables.addAll(mutableModuleExtension.myVariables);
	}

	@Override
	protected void loadStateImpl(@NotNull Element element)
	{
		super.loadStateImpl(element);

		mySdkPointer.fromXml(element);
		myTarget = DotNetTarget.valueOf(element.getAttributeValue("target", DotNetTarget.EXECUTABLE.name()));
		myAllowDebugInfo = Boolean.valueOf(element.getAttributeValue("debug", "false"));
		myAllowSourceRoots = Boolean.valueOf(element.getAttributeValue("allow-source-roots", "false"));
		myFileName = element.getAttributeValue("file-name", DEFAULT_FILE_NAME);
		myOutputDirectory = element.getAttributeValue("output-dir", DEFAULT_OUTPUT_DIR);
		myMainType = element.getAttributeValue("main-type");
		myNamespacePrefix = element.getAttributeValue("namespace-prefix");

		for(Element defineElement : element.getChildren("define"))
		{
			myVariables.add(defineElement.getText());
		}
	}

	@Override
	protected void getStateImpl(@NotNull Element element)
	{
		super.getStateImpl(element);

		mySdkPointer.toXml(element);
		element.setAttribute("target", myTarget.name());
		element.setAttribute("debug", Boolean.toString(myAllowDebugInfo));
		element.setAttribute("allow-source-roots", Boolean.toString(myAllowSourceRoots));
		element.setAttribute("file-name", myFileName);
		element.setAttribute("output-dir", myOutputDirectory);
		element.setAttribute("namespace-prefix", getNamespacePrefix());
		if(myMainType != null)
		{
			{
				element.setAttribute("main-type", myMainType);
			}
		}
		for(String variable : myVariables)
		{
			{
				element.addContent(new Element("define").setText(variable));
			}
		}
	}


	@NotNull
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

	@NotNull
	@Override
	public Map<String, String> getAvailableSystemLibraries()
	{
		Sdk sdk = getSdk();
		if(sdk == null)
		{
			return Collections.emptyMap();
		}

		String homePath = sdk.getHomePath();
		if(homePath == null)
		{
			return Collections.emptyMap();
		}

		File file = new File(homePath);
		File[] files = file.listFiles();
		if(files == null)
		{
			return Collections.emptyMap();
		}

		Map<String, String> map = new TreeMap<String, String>();
		for(File childFile : files)
		{
			try
			{
				if(!FileUtilRt.getExtension(childFile.getName()).equals("dll"))
				{
					continue;
				}
				Couple<String> info = getInfo(childFile);
				map.put(info.getFirst(), info.getSecond());
			}
			catch(Exception e)
			{
			}
		}
		return map;
	}

	@NotNull
	private static Couple<String> getInfo(File f) throws Exception
	{
		ModuleParser moduleParser = new ModuleParser(new FileInputStream(f));
		return Couple.of(moduleParser.getAssemblyInfo().getName(), moduleParser.getAssemblyInfo().getMajorVersion() + "." + moduleParser
				.getAssemblyInfo().getMinorVersion() +
				"." +
				moduleParser.getAssemblyInfo().getBuildNumber() + "." + moduleParser.getAssemblyInfo().getRevisionNumber());
	}

	@NotNull
	@Override
	public final String[] getSystemLibraryUrls(@NotNull String name, @NotNull OrderRootType orderRootType)
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

	@NotNull
	@Override
	public PsiElement[] getEntryPointElements()
	{
		List<PsiElement> list = new ArrayList<PsiElement>();
		for(ModuleExtension moduleExtension : myModuleRootLayer.getExtensions())
		{
			if(moduleExtension instanceof DotNetModuleLangExtension)
			{
				Collections.addAll(list, ((DotNetModuleLangExtension) moduleExtension).getEntryPointElements());
			}
		}
		return ContainerUtil.toArray(list, PsiElement.ARRAY_FACTORY);
	}

	@NotNull
	protected String[] getSystemLibraryUrlsImpl(@NotNull Sdk sdk, String name, OrderRootType orderRootType)
	{
		if(orderRootType == BinariesOrderRootType.getInstance())
		{
			File libraryByAssemblyName = getLibraryByAssemblyName(sdk, name, null);
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
				File libraryFile = getLibraryByAssemblyName(sdk, name, ref);
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

	private static File getLibraryByAssemblyName(Sdk sdk, String name, Ref<Couple<String>> cache)
	{
		File sdkHome = new File(sdk.getHomePath());
		try
		{
			File file = new File(sdkHome, name + ".dll");
			// fast hack
			Couple<String> info = getInfo(file);
			if(Comparing.equal(info.getFirst(), name))
			{
				if(cache != null)
				{
					cache.set(info);
				}
				return file;
			}
		}
		catch(Exception ignored)
		{
		}

		File[] files = sdkHome.listFiles();
		if(files == null)
		{
			return null;
		}

		for(File childFile : files)
		{
			try
			{
				// fast hack
				Couple<String> info = getInfo(childFile);
				if(Comparing.equal(info.getFirst(), name))
				{
					if(cache != null)
					{
						cache.set(info);
					}
					return childFile;
				}
			}
			catch(Exception ignored)
			{

			}
		}
		return null;
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
	public List<String> getVariables()
	{
		return myVariables;
	}

	@Override
	public boolean isAllowDebugInfo()
	{
		return myAllowDebugInfo;
	}

	@Nullable
	@Override
	public String getMainType()
	{
		return myMainType;
	}

	@Override
	public boolean isAllowSourceRoots()
	{
		return myAllowSourceRoots;
	}

	@NotNull
	@Override
	public String getNamespacePrefix()
	{
		return StringUtil.notNullize(myNamespacePrefix);
	}

	@NotNull
	@Override
	public String getFileName()
	{
		return StringUtil.notNullizeIfEmpty(myFileName, DEFAULT_FILE_NAME);
	}

	@NotNull
	@Override
	public String getOutputDir()
	{
		return StringUtil.notNullizeIfEmpty(myOutputDirectory, DEFAULT_OUTPUT_DIR);
	}

	public void setFileName(@NotNull String name)
	{
		myFileName = name;
	}

	public void setNamespacePrefix(@NotNull String namespacePrefix)
	{
		myNamespacePrefix = namespacePrefix;
	}

	public void setOutputDir(@NotNull String dir)
	{
		myOutputDirectory = dir;
	}

	public void setAllowSourceRoots(boolean val)
	{
		myAllowSourceRoots = val;
	}

	public void setAllowDebugInfo(boolean allowDebugInfo)
	{
		myAllowDebugInfo = allowDebugInfo;
	}

	public void setMainType(String type)
	{
		myMainType = type;
	}

	@Override
	@NotNull
	public DotNetTarget getTarget()
	{
		return myTarget;
	}

	public void setTarget(@NotNull DotNetTarget target)
	{
		myTarget = target;
	}
}
