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

package org.mustbe.consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import mono.debugger.VirtualMachine;

/**
 * @author VISTALL
 * @since 19.04.14
 */
public class DotNetDebugContext
{
	private final Project myProject;
	private final VirtualMachine myVirtualMachine;
	private final RunProfile myRunProfile;

	public DotNetDebugContext(@NotNull Project project, @NotNull VirtualMachine virtualMachine, @NotNull RunProfile runProfile)
	{
		myProject = project;
		myVirtualMachine = virtualMachine;
		myRunProfile = runProfile;
	}

	@NotNull
	public GlobalSearchScope getResolveScope()
	{
		if(myRunProfile instanceof ModuleRunProfile)
		{
			Module[] modules = ((ModuleRunProfile) myRunProfile).getModules();
			assert modules.length != 0;
			GlobalSearchScope globalSearchScope = GlobalSearchScope.EMPTY_SCOPE;
			for(Module module : modules)
			{
				DotNetModuleExtension extension = ModuleUtilCore.getExtension(module, DotNetModuleExtension.class);
				if(extension != null)
				{
					globalSearchScope = globalSearchScope.union(extension.getScopeForResolving(true));
				}
				globalSearchScope = globalSearchScope.union(GlobalSearchScope.moduleRuntimeScope(module, true));
			}
			return globalSearchScope;
		}
		else
		{
			return GlobalSearchScope.allScope(getProject());
		}
	}

	@NotNull
	public Project getProject()
	{
		return myProject;
	}

	@NotNull
	public VirtualMachine getVirtualMachine()
	{
		return myVirtualMachine;
	}

	@NotNull
	public RunProfile getRunProfile()
	{
		return myRunProfile;
	}
}
