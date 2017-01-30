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

package consulo.msbuild.solution.model;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import consulo.annotations.RequiredReadAction;
import consulo.msbuild.dom.Project;
import consulo.msbuild.solution.reader.SlnProject;

/**
 * @author VISTALL
 * @since 30-Jan-17
 */
public class WProject
{
	private SlnProject myProject;

	private Project myDomProject;

	private VirtualFile myFile;

	@RequiredReadAction
	public WProject(com.intellij.openapi.project.Project project, VirtualFile solutionVirtualFile, SlnProject slnProject)
	{
		myProject = slnProject;

		if(!StringUtil.isEmpty(slnProject.FilePath))
		{
			VirtualFile parent = solutionVirtualFile.getParent();

			myFile = parent.findFileByRelativePath(FileUtil.toSystemIndependentName(slnProject.FilePath));
			if(myFile == null)
			{
				return;
			}

			PsiFile psiFile = PsiManager.getInstance(project).findFile(myFile);

			if(psiFile instanceof XmlFile)
			{
				DomFileElement<Project> fileElement = DomManager.getDomManager(project).getFileElement((XmlFile) psiFile, consulo.msbuild.dom.Project.class);
				if(fileElement != null)
				{
					myDomProject = fileElement.getRootElement();
				}
			}
		}
	}

	public Project getDomProject()
	{
		return myDomProject;
	}

	public String getName()
	{
		return myProject.Name;
	}

	@Nullable
	public VirtualFile getVirtualFile()
	{
		return myFile;
	}
}
