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

package org.mustbe.consulo.visualStudio;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.ide.util.newProjectWizard.ProjectNameStep;
import com.intellij.ide.util.newProjectWizard.StepSequence;
import com.intellij.ide.util.newProjectWizard.modes.WizardMode;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportProvider;

/**
 * @author VISTALL
 * @since 27.03.14
 */
public class VisualStudioImporterProvider extends ProjectImportProvider
{
	public VisualStudioImporterProvider()
	{
		super(new VisualStudioImportBuilder());
	}

	@Override
	public ModuleWizardStep[] createSteps(WizardContext context)
	{
		VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(context.getProjectFileDirectory());
		assert fileByPath != null;
		context.setProjectName(fileByPath.getNameWithoutExtension());
		context.setProjectFileDirectory(fileByPath.getParent().getPath());
		return new ModuleWizardStep[]{new ProjectNameStep(context, new WizardMode()
		{
			@NotNull
			@Override
			public String getDisplayName(WizardContext context)
			{
				return "test";
			}

			@NotNull
			@Override
			public String getDescription(WizardContext context)
			{
				return "Project";
			}

			@Override
			public boolean isAvailable(WizardContext context)
			{
				return true;
			}

			@Nullable
			@Override
			protected StepSequence createSteps(
					WizardContext context, @NotNull ModulesProvider modulesProvider)
			{
				return null;
			}

			@Nullable
			@Override
			public ProjectBuilder getModuleBuilder()
			{
				return myBuilder;
			}

			@Override
			public void onChosen(boolean enabled)
			{

			}
		})};
	}

	@Override
	protected boolean canImportFromFile(VirtualFile file)
	{
		return file.getFileType() == VisualStudioSolutionFileType.INSTANCE;
	}

	@Override
	public String getPathToBeImported(VirtualFile file)
	{
		return file.getPath();
	}

	@Override
	@Nullable
	@Language("HTML")
	public String getFileSample()
	{
		return "<b>Visual Studio</b> solution file (*.sln)";
	}
}
