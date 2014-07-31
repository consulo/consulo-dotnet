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

package org.mustbe.consulo.dotnet.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.consulo.module.extension.ModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleLangExtension;
import com.intellij.ProjectTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ModuleRootLayerListener;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.FileContentUtil;
import com.intellij.util.containers.hash.HashSet;
import lombok.val;

/**
 * @author VISTALL
 * @since 21.02.14
 */
public class ModuleTopicsRegister extends AbstractProjectComponent
{
	public ModuleTopicsRegister(Project project)
	{
		super(project);
	}

	@Override
	public void projectOpened()
	{
		myProject.getMessageBus().connect().subscribe(ProjectTopics.MODULE_LAYERS, new ModuleRootLayerListener.Adapter()
		{
			@Override
			public void currentLayerChanged(@NotNull Module module, @NotNull String oldName, @NotNull String newName)
			{
				reParseFiles(module);
			}
		});
	}

	private void reParseFiles(final Module module)
	{
		Task.Backgroundable task = new Task.Backgroundable(myProject, "Reparsing files", false)
		{
			@Override
			public void run(@NotNull ProgressIndicator indicator)
			{
				final Collection<VirtualFile> files = new ArrayList<VirtualFile>();

				val moduleRootManager = ModuleRootManager.getInstance(module);
				val fileIndex = moduleRootManager.getFileIndex();
				fileIndex.iterateContentUnderDirectory(module.getModuleDir(), new ContentIterator()
				{
					@Override
					public boolean processFile(VirtualFile virtualFile)
					{
						Set<FileType> types = new HashSet<FileType>();
						ModuleExtension[] extensions = moduleRootManager.getExtensions();
						for(ModuleExtension extension : extensions)
						{
							if(extension instanceof DotNetModuleLangExtension)
							{
								types.add(((DotNetModuleLangExtension) extension).getFileType());
							}
						}

						if(types.contains(virtualFile.getFileType()))
						{
							files.add(virtualFile);
						}

						return true;
					}
				});

				ApplicationManager.getApplication().invokeAndWait(new Runnable()
				{
					@Override
					public void run()
					{
						FileContentUtil.reparseFiles(myProject, files, true);
					}
				}, ModalityState.NON_MODAL);
			}
		};
		ProgressManager.getInstance().run(task);
	}
}
