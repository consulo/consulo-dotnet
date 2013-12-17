/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.packageSupport;

import java.util.HashMap;
import java.util.Map;

import org.consulo.lombok.annotations.ProjectService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.QualifiedName;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
@ProjectService
public class PackageManager
{
	@NotNull
	private final Project myProject;
	private Map<QualifiedName, Package> myPackages = new HashMap<QualifiedName, Package>();

	public PackageManager(@NotNull Project project)
	{
		myProject = project;
	}


	@Nullable
	public Package findPackage(@NotNull String name, @NotNull GlobalSearchScope searchScope, @NotNull PackageDescriptor packageDescriptor)
	{
		QualifiedName qualifiedName = packageDescriptor.toQName(name);

		Package p = myPackages.get(qualifiedName);
		if(p != null)
		{
			return p;
		}

		if(packageDescriptor.canCreate(name, myProject, searchScope))
		{
			p = new Package(myProject, qualifiedName);
			myPackages.put(qualifiedName, p);
		}

		return p;
	}
}
