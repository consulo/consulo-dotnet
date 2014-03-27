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

package org.mustbe.consulo.visualStudio.csproj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.consulo.lombok.annotations.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.module.CSharpConfigurationLayer;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayerImpl;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.microsoft.csharp.module.extension.MicrosoftCSharpMutableModuleExtension;
import org.mustbe.consulo.microsoft.dotnet.module.extension.MicrosoftDotNetMutableModuleExtension;
import org.mustbe.consulo.microsoft.dotnet.sdk.MicrosoftDotNetSdkType;
import org.mustbe.consulo.module.extension.ModuleExtensionLayerUtil;
import org.mustbe.consulo.visualStudio.VisualStudioProjectProcessor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTable;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 27.03.14
 */
@Logger
public class CsProjProcessor implements VisualStudioProjectProcessor
{
	private static final String ourParentName = CsProjProcessor.class.getName();
	private static final String OutputType = "OutputType";
	private static final String TargetFrameworkVersion = "TargetFrameworkVersion";
	private static final String DefineConstants = "DefineConstants";
	private static final String DebugSymbols = "DebugSymbols";
	private static final String AllowUnsafeBlocks = "AllowUnsafeBlocks";


	@NotNull
	@Override
	public FileType getFileType()
	{
		return CsProjFileType.INSTANCE;
	}

	@Override
	public void processFile(@NotNull VirtualFile projectFile, @NotNull ModifiableRootModel moduleWithSingleContent)
	{
		try
		{
			List<Sdk> sdks = SdkTable.getInstance().getSdksOfType(MicrosoftDotNetSdkType.getInstance());

			Document document = JDOMUtil.loadDocument(projectFile.getInputStream());

			MicrosoftDotNetMutableModuleExtension e = moduleWithSingleContent.getExtensionWithoutCheck
					(MicrosoftDotNetMutableModuleExtension.class);
			e.setEnabled(true);

			moduleWithSingleContent.addModuleExtensionSdkEntry(e);

			MicrosoftCSharpMutableModuleExtension c = moduleWithSingleContent.getExtensionWithoutCheck
					(MicrosoftCSharpMutableModuleExtension.class);

			c.setEnabled(true);

			Map<String, PropertyGroup> groupMap = new LinkedHashMap<String, PropertyGroup>();
			Element rootElement = document.getRootElement();

			for(Element propertyGroup : rootElement.getChildren())
			{
				if(!"PropertyGroup".equals(propertyGroup.getName()))
				{
					continue;
				}
				String cond = propertyGroup.getAttributeValue("Condition");
				if(cond == null)
				{
					cond = ourParentName;
				}
				else
				{
					cond = cond.substring(" '$(Configuration)|$(Platform)' == '".length(), cond.lastIndexOf('|'));
				}

				PropertyGroup value = new PropertyGroup();
				groupMap.put(cond, value);

				String outputType = propertyGroup.getChildText(OutputType);
				if("WinExe".equals(outputType))
				{
					value.put(OutputType, DotNetTarget.EXECUTABLE);
				}
				else if("Library".equals(outputType))
				{
					value.put(OutputType, DotNetTarget.LIBRARY);
				}

				String targetFrameworkVersion = propertyGroup.getChildText(TargetFrameworkVersion);
				if(targetFrameworkVersion != null)
				{
					value.put(TargetFrameworkVersion, targetFrameworkVersion);
				}

				String defineConstants = propertyGroup.getChildText(DefineConstants);
				if(defineConstants != null)
				{
					String[] split = defineConstants.split(";");
					value.put(DefineConstants, Arrays.asList(split));
				}

				String debugSymbols = propertyGroup.getChildText(DebugSymbols);
				if(debugSymbols != null)
				{
					value.put(DebugSymbols, Boolean.parseBoolean(debugSymbols));
				}

				String unsafeBlocks = propertyGroup.getChildText(AllowUnsafeBlocks);
				if(unsafeBlocks != null)
				{
					value.put(AllowUnsafeBlocks, Boolean.parseBoolean(unsafeBlocks));
				}
			}

			PropertyGroup own = groupMap.remove(ourParentName);

			for(PropertyGroup propertyGroup : groupMap.values())
			{
				propertyGroup.putAll(own.get());
			}

			List<String> newList = new ArrayList<String>(e.getLayersList());

			for(Map.Entry<String, PropertyGroup> groupEntry : groupMap.entrySet())
			{
				String key = groupEntry.getKey();
				PropertyGroup value = groupEntry.getValue();


				if(newList.contains(key))
				{
					newList.remove(key);
				}
				else
				{
					ModuleExtensionLayerUtil.addLayerNoCommit(moduleWithSingleContent, key, DotNetModuleExtension.class);
				}

				MainConfigurationLayerImpl main = (MainConfigurationLayerImpl) e.getLayer(key);
				CSharpConfigurationLayer csharp = (CSharpConfigurationLayer) c.getLayer(key);

				main.setTarget(value.get(OutputType, DotNetTarget.EXECUTABLE));
				main.setAllowDebugInfo(value.get(DebugSymbols, Boolean.TRUE));
				csharp.setAllowUnsafeCode(value.get(AllowUnsafeBlocks, Boolean.TRUE));

				List<String> list = value.get(DefineConstants, Collections.<String>emptyList());
				main.getVariables().clear();
				main.getVariables().addAll(list);

				String version = MicrosoftDotNetSdkType.removeFirstCharIfIsV(value.get(TargetFrameworkVersion, "v2"));

				for(Sdk sdk : sdks)
				{
					if(sdk.getVersionString().startsWith(version))
					{
						main.getInheritableSdk().set(null, sdk);
						break;
					}
				}
			}

			for(String key : newList)
			{
				ModuleExtensionLayerUtil.removeLayerNoCommit(moduleWithSingleContent, key, DotNetModuleExtension.class);
			}

			ModuleExtensionLayerUtil.setCurrentLayerNoCommit(moduleWithSingleContent, ContainerUtil.getFirstItem(groupMap.keySet()),
					DotNetModuleExtension.class);
		}
		catch(JDOMException e)
		{
			LOGGER.error(e);
		}
		catch(IOException e)
		{
			LOGGER.error(e);
		}
	}
}
