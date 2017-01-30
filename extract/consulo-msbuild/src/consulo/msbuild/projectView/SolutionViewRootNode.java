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

import gnu.trove.THashMap;
import gnu.trove.THashSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.annotations.RequiredDispatchThread;
import consulo.annotations.RequiredReadAction;
import consulo.msbuild.MSBuildGUID;
import consulo.msbuild.MSBuildIcons;
import consulo.msbuild.solution.model.WProject;
import consulo.msbuild.solution.model.WSolution;
import consulo.msbuild.solution.reader.SlnSection;

/**
 * @author VISTALL
 * @since 28-Jan-17
 */
public class SolutionViewRootNode extends ProjectViewNode<Project>
{
	private WSolution myWSolution;
	private VirtualFile mySolutionFile;

	@RequiredReadAction
	public SolutionViewRootNode(Project project, VirtualFile solutionFile, ViewSettings viewSettings)
	{
		super(project, project, viewSettings);
		mySolutionFile = solutionFile;

		myWSolution = WSolution.build(myProject, solutionFile);
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
		Collection<WProject> projects = myWSolution.getProjects();

		Map<String, AbstractTreeNode> nodes = new THashMap<>(projects.size());
		for(WProject wProject : myWSolution.getProjects())
		{
			AbstractTreeNode node;
			if(MSBuildGUID.SolutionFolder.equals(wProject.getTypeGUID()))
			{
				node = new SolutionViewProjectFolderNode(myProject, wProject, getSettings());
			}
			else
			{
				consulo.msbuild.dom.Project domProject = wProject.getDomProject();
				if(domProject == null)
				{
					node = new SolutionViewInvalidProjectNode(myProject, wProject, getSettings());
				}
				else
				{
					node = new SolutionViewProjectNode(myProject, wProject, getSettings());
				}
			}

			nodes.put(wProject.getId(), node);
		}

		SlnSection section = myWSolution.getSection(WSolution.SECTION_NESTED_PROJECTS);
		if(section != null)
		{
			Set<String> toRemove = new THashSet<>();
			for(Map.Entry<String, String> entry : section.getProperties().getValues().entrySet())
			{
				AbstractTreeNode left = nodes.get(entry.getKey());
				AbstractTreeNode right = nodes.get(entry.getValue());

				if(right == null)
				{
					continue;
				}

				((SolutionViewProjectFolderNode) right).addChildren(left);

				toRemove.add(((SolutionViewProjectNodeBase) left).getProjectId());
			}

			for(String id : toRemove)
			{
				nodes.remove(id);
			}
		}
		return nodes.values();
	}

	@Override
	protected void update(PresentationData presentation)
	{
		presentation.setIcon(MSBuildIcons.VisualStudio);
		presentation.setPresentableText("Solution");

		String solName = String.format("Solution '%s' (%s project(s))", mySolutionFile.getNameWithoutExtension(), myWSolution.getProjects().size());

		presentation.setPresentableText(solName);
	}
}
