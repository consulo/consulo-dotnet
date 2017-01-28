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

package consulo.msbuild.projectView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.annotations.RequiredDispatchThread;
import consulo.annotations.RequiredReadAction;
import consulo.msbuild.MSBuildIcons;
import consulo.msbuild.solution.reader.VisualStudioProjectInfo;
import consulo.msbuild.solution.reader.VisualStudioSolutionParser;

/**
 * @author VISTALL
 * @since 28-Jan-17
 */
public class SolutionViewRootNode extends ProjectViewNode<Project>
{
	private List<VisualStudioProjectInfo> myProjects;
	private VirtualFile mySolutionFile;

	@RequiredReadAction
	public SolutionViewRootNode(Project project, VirtualFile solutionFile, ViewSettings viewSettings)
	{
		super(project, project, viewSettings);
		mySolutionFile = solutionFile;

		myProjects = VisualStudioSolutionParser.parse(myProject, solutionFile);
	}

	@Override
	public boolean contains(@NotNull VirtualFile file)
	{
		return false;
	}

	@NotNull
	@Override
	@RequiredDispatchThread
	public Collection<? extends AbstractTreeNode> getChildren()
	{
		List<SolutionProjectViewPane> list = new ArrayList<>(myProjects.size());
		for(VisualStudioProjectInfo projectInfo : myProjects)
		{
			list.add(new SolutionProjectViewPane(myProject, projectInfo.getProject(), projectInfo.getName(), projectInfo.getVirtualFile(), getSettings()));
		}
		return list;
	}

	@Override
	protected void update(PresentationData presentation)
	{
		presentation.setIcon(MSBuildIcons.VisualStudio);
		presentation.setPresentableText("Solution");

		String solName = String.format("Solution '%s' (%s project(s))", mySolutionFile.getNameWithoutExtension(), myProjects.size());

		presentation.setPresentableText(solName);
	}
}
