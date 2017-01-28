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

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packaging.artifacts.ModifiableArtifactModel;
import com.intellij.projectImport.ProjectImportBuilder;
import consulo.annotations.RequiredReadAction;
import consulo.msbuild.MSBuildIcons;
import consulo.msbuild.MSBuildSolutionManager;

/**
 * @author VISTALL
 * @since 28-Jan-17
 */
public class MSBuildProjectImportBuilder extends ProjectImportBuilder
{
	@NotNull
	@Override
	public String getName()
	{
		return "Visual Studio";
	}

	@Override
	public Icon getIcon()
	{
		return MSBuildIcons.Msbuild;
	}

	@Override
	public List getList()
	{
		return null;
	}

	@Override
	public boolean isMarked(Object element)
	{
		return false;
	}

	@Override
	public void setList(List list) throws ConfigurationException
	{

	}

	@Override
	public void setOpenProjectSettingsAfter(boolean on)
	{

	}

	@Nullable
	@Override
	@RequiredReadAction
	public List<Module> commit(Project project, ModifiableModuleModel old, ModulesProvider modulesProvider, ModifiableArtifactModel artifactModel)
	{
		String fileToImport = getFileToImport();

		VirtualFile solutionFile = LocalFileSystem.getInstance().findFileByPath(fileToImport);
		assert solutionFile != null;
		MSBuildSolutionManager.getInstance(project).setUrl(solutionFile);
		VirtualFile parent = solutionFile.getParent();

		List<Module> modules = new ArrayList<>();

		final ModifiableModuleModel modifiableModuleModel = old == null ? ModuleManager.getInstance(project).getModifiableModel() : old;

		final ModifiableRootModel mainModuleModel = createModuleWithSingleContent(parent.getName() + " (Solution)", parent, modifiableModuleModel);
		modules.add(mainModuleModel.getModule());
		WriteAction.run(mainModuleModel::commit);

		if(modifiableModuleModel != old)
		{
			WriteAction.run(modifiableModuleModel::commit);
		}
		return modules;
	}

	@RequiredReadAction
	private ModifiableRootModel createModuleWithSingleContent(String name, VirtualFile dir, ModifiableModuleModel modifiableModuleModel)
	{
		Module module = modifiableModuleModel.newModule(name, dir.getPath());

		ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
		ModifiableRootModel modifiableModel = moduleRootManager.getModifiableModel();
		modifiableModel.addContentEntry(dir);

		return modifiableModel;
	}
}
