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

package consulo.dotnet.debugger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import consulo.dotnet.debugger.proxy.DotNetVirtualMachineProxy;

/**
 * @author VISTALL
 * @since 19.04.14
 */
public class DotNetDebugContext
{
	private final Project myProject;
	private final DotNetVirtualMachineProxy myVirtualMachine;
	private final RunProfile myRunProfile;
	private final XDebugSession mySession;
	private final XLineBreakpoint<?> myBreakpoint;

	public DotNetDebugContext(@NotNull Project project,
			@NotNull DotNetVirtualMachineProxy virtualMachine,
			@NotNull RunProfile runProfile,
			@NotNull XDebugSession session,
			@Nullable XLineBreakpoint<?> breakpoint)
	{
		myProject = project;
		myVirtualMachine = virtualMachine;
		myRunProfile = runProfile;
		mySession = session;
		myBreakpoint = breakpoint;
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
				globalSearchScope = globalSearchScope.union(GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, true));
			}
			return globalSearchScope;
		}
		else
		{
			return GlobalSearchScope.allScope(getProject());
		}
	}

	@Nullable
	public XLineBreakpoint<?> getBreakpoint()
	{
		return myBreakpoint;
	}

	@NotNull
	public Project getProject()
	{
		return myProject;
	}

	@NotNull
	public DotNetVirtualMachineProxy getVirtualMachine()
	{
		return myVirtualMachine;
	}

	@NotNull
	public RunProfile getRunProfile()
	{
		return myRunProfile;
	}

	@NotNull
	public XDebugSession getSession()
	{
		return mySession;
	}
}
