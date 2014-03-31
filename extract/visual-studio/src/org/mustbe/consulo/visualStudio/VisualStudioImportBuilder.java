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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.plugins.visualstudio.VisualStudioSolution;
import org.sonar.plugins.visualstudio.VisualStudioSolutionParser;
import org.sonar.plugins.visualstudio.VisualStudioSolutionProject;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packaging.artifacts.ModifiableArtifactModel;
import com.intellij.projectImport.ProjectImportBuilder;
import lombok.val;

/**
 * @author VISTALL
 * @since 27.03.14
 */
public class VisualStudioImportBuilder extends ProjectImportBuilder<Object>
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
		return VisualStudioIcons.VisualStudio;
	}

	@Override
	public List<Object> getList()
	{
		return null;
	}

	@Override
	public boolean isMarked(Object element)
	{
		return false;
	}

	@Override
	public void setList(List<Object> list) throws ConfigurationException
	{

	}

	@Override
	public void setOpenProjectSettingsAfter(boolean on)
	{

	}

	@Nullable
	@Override
	public List<Module> commit(Project project, ModifiableModuleModel old, ModulesProvider modulesProvider, ModifiableArtifactModel artifactModel)
	{
		VirtualFile solutionFile = LocalFileSystem.getInstance().findFileByPath(getFileToImport());
		assert solutionFile != null;

		VirtualFile parent = solutionFile.getParent();

		List<Module> modules = new ArrayList<Module>();

		VisualStudioSolution studioSolution = VisualStudioSolutionParser.parse(new File(getFileToImport()));

		val modifiableModuleModel = old == null ? ModuleManager.getInstance(project).getModifiableModel() : old;

		val mainModuleModel = createModuleWithSingleContent(parent.getName(), parent,
				modifiableModuleModel);
		modules.add(mainModuleModel.getModule());
		new WriteAction<Object>()
		{

			@Override
			protected void run(Result<Object> objectResult) throws Throwable
			{
				mainModuleModel.commit();
			}
		}.execute();

		for(VisualStudioSolutionProject o : studioSolution.projects())
		{
			VirtualFile projectFile = parent.findFileByRelativePath(FileUtil.toSystemIndependentName(o.path()));
			if(projectFile == null)
			{
				continue;
			}


			val moduleWithSingleContent = createModuleWithSingleContent(projectFile.getNameWithoutExtension(),
					projectFile.getParent(), modifiableModuleModel);
			modules.add(moduleWithSingleContent.getModule());
			for(VisualStudioProjectProcessor processor : VisualStudioProjectProcessor.EP_NAME.getExtensions())
			{
				if(processor.getFileType() == projectFile.getFileType())
				{
					processor.processFile(projectFile, moduleWithSingleContent);
					break;
				}
			}

			new WriteAction<Object>()
			{

				@Override
				protected void run(Result<Object> objectResult) throws Throwable
				{
					moduleWithSingleContent.commit();
				}
			}.execute();
		}

		if(modifiableModuleModel != old)
		{
			new WriteAction<Object>()
			{
				@Override
				protected void run(Result<Object> objectResult) throws Throwable
				{
					modifiableModuleModel.commit();
				}
			}.execute();
		}
		return modules;
	}

	private ModifiableRootModel createModuleWithSingleContent(String name, VirtualFile dir, ModifiableModuleModel modifiableModuleModel)
	{
		Module module = modifiableModuleModel.newModule(name, dir.getPath());

		ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
		val modifiableModel = moduleRootManager.getModifiableModel();
		modifiableModel.addContentEntry(dir);

		return modifiableModel;
	}
}
