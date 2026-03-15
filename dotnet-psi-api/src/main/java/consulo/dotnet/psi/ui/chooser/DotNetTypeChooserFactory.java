/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.psi.ui.chooser;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.language.editor.ui.TreeChooser;
import consulo.project.Project;
import consulo.project.content.scope.ProjectAwareSearchScope;


/**
 * @author VISTALL
 * @since 29.04.2016
 */
@ServiceAPI(ComponentScope.PROJECT)
public abstract class DotNetTypeChooserFactory
{
	public static DotNetTypeChooserFactory getInstance(Project project)
	{
		return project.getInstance(DotNetTypeChooserFactory.class);
	}

	public abstract TreeChooser<DotNetTypeDeclaration> createChooser(ProjectAwareSearchScope scope);

	public abstract TreeChooser<DotNetTypeDeclaration> createInheriableChooser(String baseVmQName, ProjectAwareSearchScope scope);
}
