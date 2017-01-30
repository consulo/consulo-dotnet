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

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.projectImport.ProjectImportProvider;
import consulo.msbuild.VisualStudioSolutionFileType;
import consulo.msbuild.importProvider.ui.MSBuildSetupTargetStep;

/**
 * @author VISTALL
 * @since 28-Jan-17
 */
public class MSBuildProjectImportProvider extends ProjectImportProvider
{
	protected MSBuildProjectImportProvider()
	{
		super(new MSBuildProjectImportBuilder());
	}

	@Override
	public ModuleWizardStep[] createSteps(WizardContext context)
	{
		VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(context.getProjectFileDirectory());
		assert fileByPath != null;
		context.setProjectName(fileByPath.getNameWithoutExtension());
		context.setProjectFileDirectory(fileByPath.getParent().getPath());

		return new ModuleWizardStep[]{new MSBuildSetupTargetStep(context, null, fileByPath)};
	}

	@Override
	public boolean canImport(VirtualFile fileOrDirectory, @Nullable Project project)
	{
		return fileOrDirectory.getFileType() == VisualStudioSolutionFileType.INSTANCE;
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
