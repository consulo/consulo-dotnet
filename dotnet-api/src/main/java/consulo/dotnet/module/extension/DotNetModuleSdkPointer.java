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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.bundle.SdkUtil;
import consulo.module.extension.ModuleInheritableNamedPointer;
import consulo.module.extension.MutableModuleInheritableNamedPointer;
import consulo.util.pointers.NamedPointer;
import org.jdom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 25.01.15
 */
public class DotNetModuleSdkPointer implements MutableModuleInheritableNamedPointer<Sdk>
{
	private NamedPointer<Module> myModulePointer;
	private NamedPointer<Sdk> myTargetPointer;
	private final Project myProject;
	private final String myXmlPrefix = "sdk";
	private final String myModuleExtensionId;

	public DotNetModuleSdkPointer(Project project, String moduleExtensionId)
	{
		myProject = project;
		myModuleExtensionId = moduleExtensionId;
	}

	@Nullable
	public String getItemNameFromModule(@Nonnull Module module)
	{
		final DotNetSimpleModuleExtension<?> extension = (DotNetSimpleModuleExtension) ModuleUtilCore.getExtension(module, myModuleExtensionId);
		if(extension != null)
		{
			return extension.getInheritableSdk().getName();
		}
		return null;
	}

	@Nullable
	public Sdk getItemFromModule(@Nonnull Module module)
	{
		final DotNetSimpleModuleExtension<?> extension = (DotNetSimpleModuleExtension) ModuleUtilCore.getExtension(module, myModuleExtensionId);
		if(extension != null)
		{
			return extension.getInheritableSdk().get();
		}
		return null;
	}

	@Nonnull
	public NamedPointer<Sdk> getPointer(@Nonnull Project project, @Nonnull String name)
	{
		return SdkUtil.createPointer(name);
	}

	@Nullable
	@Override
	public Sdk get()
	{
		if(myModulePointer != null)
		{
			final Module module = myModulePointer.get();
			if(module == null)
			{
				return getDefaultValue();
			}
			return getItemFromModule(module);
		}
		return myTargetPointer == null ? getDefaultValue() : myTargetPointer.get();
	}

	@Nullable
	@Override
	public String getName()
	{
		if(myModulePointer != null)
		{
			final Module module = myModulePointer.get();
			if(module == null)
			{
				return null;
			}
			return getItemNameFromModule(module);
		}
		return myTargetPointer == null ? null : myTargetPointer.getName();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof DotNetModuleSdkPointer)
		{
			final DotNetModuleSdkPointer another = (DotNetModuleSdkPointer) obj;
			if(!Comparing.equal(myModulePointer, another.myModulePointer))
			{
				return false;
			}
			if(!Comparing.equal(myTargetPointer, another.myTargetPointer))
			{
				return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void set(ModuleInheritableNamedPointer<Sdk> anotherItem)
	{
		if(anotherItem.isNull())
		{
			myModulePointer = null;
			myTargetPointer = null;
		}
		else
		{
			final String moduleName = anotherItem.getModuleName();
			myModulePointer = moduleName == null ? null : ModuleUtilCore.createPointer(myProject, moduleName);

			if(myModulePointer == null)
			{
				final String targetName = anotherItem.getName();
				myTargetPointer = getPointer(myProject, targetName);
			}
		}
	}

	@Override
	public void set(@Nullable String moduleName, @Nullable String name)
	{
		myModulePointer = moduleName == null ? null : ModuleUtilCore.createPointer(myProject, moduleName);
		myTargetPointer = name == null ? null : getPointer(myProject, name);
	}

	@Override
	public void set(@Nullable Module module, @Nullable Sdk named)
	{
		myModulePointer = module == null ? null : ModuleUtilCore.createPointer(module);
		myTargetPointer = named == null ? null : getPointer(myProject, named.getName());
	}

	public void toXml(Element element)
	{
		element.setAttribute(myXmlPrefix + "-module-name", StringUtil.notNullize(myModulePointer == null ? null : myModulePointer.getName()));
		element.setAttribute(myXmlPrefix + "-name", StringUtil.notNullize(myTargetPointer == null ? null : myTargetPointer.getName()));
	}

	@RequiredReadAction
	public void fromXml(Element element)
	{
		final String moduleName = StringUtil.nullize(element.getAttributeValue(myXmlPrefix + "-module-name"));
		if(moduleName != null)
		{
			myModulePointer = ModuleUtilCore.createPointer(myProject, moduleName);
		}
		final String itemName = StringUtil.nullize(element.getAttributeValue(myXmlPrefix + "-name"));
		if(itemName != null)
		{
			myTargetPointer = getPointer(myProject, itemName);
		}
	}

	@Nullable
	public Sdk getDefaultValue()
	{
		return null;
	}

	@Nullable
	@Override
	public Module getModule()
	{
		if(myModulePointer == null)
		{
			return null;
		}
		return myModulePointer.get();
	}

	@Nullable
	@Override
	public String getModuleName()
	{
		if(myModulePointer == null)
		{
			return null;
		}
		return myModulePointer.getName();
	}

	@Override
	public boolean isNull()
	{
		return myModulePointer == null && myTargetPointer == null;
	}
}

