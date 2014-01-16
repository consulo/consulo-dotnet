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

package org.mustbe.consulo.dotnet.run.filters;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.module.extension.ModuleExtensionHelper;
import com.intellij.execution.filters.ConsoleFilterProviderEx;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author VISTALL
 * @since 16.01.14
 */
public class DotNetConsoleFilterProvider implements ConsoleFilterProviderEx
{
	@Override
	public Filter[] getDefaultFilters(@NotNull Project project, @NotNull GlobalSearchScope searchScope)
	{
		if(!ModuleExtensionHelper.getInstance(project).hasModuleExtension(DotNetModuleExtension.class))
		{
			return Filter.EMPTY_ARRAY;
		}
		/*
Unhandled Exception:
System.IndexOutOfRangeException: Array index is out of range.
  at Program.inner (System.String[] arg) [0x00001] in C:\Users\VISTALL\ConsuloProjects\untitled30\innerModule2\Program.cs:12
  at Program.Main (System.String[] arg) [0x00001] in C:\Users\VISTALL\ConsuloProjects\untitled30\innerModule2\Program.cs:7
[ERROR] FATAL UNHANDLED EXCEPTION: System.IndexOutOfRangeException: Array index is out of range.
  at Program.inner (System.String[] arg) [0x00001] in C:\Users\VISTALL\ConsuloProjects\untitled30\innerModule2\Program.cs:12
  at Program.Main (System.String[] arg) [0x00001] in C:\Users\VISTALL\ConsuloProjects\untitled30\innerModule2\Program.cs:7


Unhandled Exception: System.IndexOutOfRangeException: Index was outside the bounds of the array.
   at Program.inner(String[] arg) in c:\Users\VISTALL\ConsuloProjects\untitled30\innerModule2\Program.cs:line 15
   at Program.Main(String[] arg) in c:\Users\VISTALL\ConsuloProjects\untitled30\innerModule2\Program.cs:line 10

		 */
		return new Filter[0];
	}

	@NotNull
	@Override
	public Filter[] getDefaultFilters(@NotNull Project project)
	{
		return getDefaultFilters(project, GlobalSearchScope.allScope(project));
	}
}
