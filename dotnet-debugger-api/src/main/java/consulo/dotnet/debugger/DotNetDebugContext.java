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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
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
	private final XBreakpoint<?> myBreakpoint;
	private final NotNullLazyValue<GlobalSearchScope> myScopeValue = new NotNullLazyValue<GlobalSearchScope>()
	{
		@Nonnull
		@Override
		protected GlobalSearchScope compute()
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
	};

	public DotNetDebugContext(@Nonnull Project project,
			@Nonnull DotNetVirtualMachineProxy virtualMachine,
			@Nonnull RunProfile runProfile,
			@Nonnull XDebugSession session,
			@Nullable XBreakpoint<?> breakpoint)
	{
		myProject = project;
		myVirtualMachine = virtualMachine;
		myRunProfile = runProfile;
		mySession = session;
		myBreakpoint = breakpoint;
	}

	@Nonnull
	public GlobalSearchScope getResolveScope()
	{
		return myScopeValue.getValue();
	}

	@Nullable
	public XBreakpoint<?> getBreakpoint()
	{
		return myBreakpoint;
	}

	@Nonnull
	public Project getProject()
	{
		return myProject;
	}

	@Nonnull
	public DotNetVirtualMachineProxy getVirtualMachine()
	{
		return myVirtualMachine;
	}

	@Nonnull
	public RunProfile getRunProfile()
	{
		return myRunProfile;
	}

	@Nonnull
	public XDebugSession getSession()
	{
		return mySession;
	}

	public void invoke(@Nonnull Runnable runnable)
	{
		myVirtualMachine.invoke(runnable);
	}
}
