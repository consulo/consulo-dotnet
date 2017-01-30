/*
 * Copyright 2013-2017 consulo.io
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

package consulo.msbuild.importProvider;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.util.ArrayFactory;
import com.intellij.util.containers.ContainerUtil;
import consulo.module.extension.ModuleExtensionProviderEP;
import consulo.module.extension.impl.ModuleExtensionProviders;

/**
 * @author VISTALL
 * @since 30-Jan-17
 */
public enum MSBuildImportTarget
{
	_NET("microsoft-dotnet"), Mono("mono-dotnet");

	public static final MSBuildImportTarget[] EMPTY_ARRAY = new MSBuildImportTarget[0];

	public static ArrayFactory<MSBuildImportTarget> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new MSBuildImportTarget[count];

	private final String myPresentableName;
	private final String myFrameworkExtensionId;

	MSBuildImportTarget(String frameworkExtensionId)
	{
		myFrameworkExtensionId = frameworkExtensionId;
		myPresentableName = name().replace("_", ".");
	}

	@NotNull
	public static MSBuildImportTarget[] getAvailableTargets()
	{
		MSBuildImportTarget[] values = values();
		List<MSBuildImportTarget> list = new ArrayList<>(values.length);
		for(MSBuildImportTarget visualStudioImportTarget : values)
		{
			ModuleExtensionProviderEP providerEP = ModuleExtensionProviders.findProvider(visualStudioImportTarget.getFrameworkExtensionId());
			if(providerEP != null)
			{
				list.add(visualStudioImportTarget);
			}
		}
		return ContainerUtil.toArray(list, MSBuildImportTarget.ARRAY_FACTORY);
	}

	public String getFrameworkExtensionId()
	{
		return myFrameworkExtensionId;
	}

	@Override
	public String toString()
	{
		return myPresentableName;
	}
}
