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
import java.util.Collections;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.SimpleTextAttributes;
import consulo.annotations.RequiredDispatchThread;
import consulo.annotations.RequiredReadAction;
import consulo.msbuild.solution.SolutionVirtualBuilder;
import consulo.msbuild.solution.SolutionVirtualDirectory;
import consulo.msbuild.solution.SolutionVirtualFile;
import consulo.msbuild.solution.SolutionVirtualItem;
import consulo.msbuild.solution.model.WProject;

/**
 * @author VISTALL
 * @since 28-Jan-17
 */
public class SolutionViewProjectNode extends ProjectViewNode<WProject> implements SolutionViewProjectNodeBase
{
	public SolutionViewProjectNode(com.intellij.openapi.project.Project project, WProject wProject, ViewSettings viewSettings)
	{
		super(project, wProject, viewSettings);
		myName = getValue().getName();
	}

	@Override
	public boolean contains(@NotNull VirtualFile file)
	{
		return false;
	}

	@Override
	public String getProjectId()
	{
		return getValue().getId();
	}

	@NotNull
	@Override
	@RequiredDispatchThread
	public Collection<? extends AbstractTreeNode> getChildren()
	{
		VirtualFile projectFile = getValue().getVirtualFile();
		VirtualFile parent = projectFile.getParent();
		if(parent == null)
		{
			return Collections.emptyList();
		}

		SolutionVirtualDirectory directory = SolutionVirtualBuilder.build(getValue().getDomProject(), parent);

		Collection<AbstractTreeNode> nodes = buildNodes(myProject, directory::getChildren, getSettings(), false);
		nodes.add(new SolutionViewRefencesNode(myProject, getValue().getDomProject(), getSettings()));
		return nodes;
	}

	@NotNull
	@RequiredReadAction
	public static Collection<AbstractTreeNode> buildNodes(com.intellij.openapi.project.Project project,
			Supplier<Collection<SolutionVirtualItem>> values,
			ViewSettings settings,
			boolean restrictPatcher)
	{
		Collection<SolutionVirtualItem> items = values.get();
		Collection<AbstractTreeNode> list = new ArrayList<>(items.size());
		for(SolutionVirtualItem item : items)
		{
			if(item instanceof SolutionVirtualDirectory)
			{
				list.add(new SolutionViewDirectoryNode(project, (SolutionVirtualDirectory) item, settings));
			}
			else if(item instanceof SolutionVirtualFile)
			{
				VirtualFile virtualFile = ((SolutionVirtualFile) item).getVirtualFile();
				if(virtualFile == null)
				{
					list.add(new SolutionViewInvalidFileNode(project, (SolutionVirtualFile) item, settings));
				}
				else
				{
					PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
					assert file != null;
					list.add(new SolutionViewFileNode(project, file, settings, (SolutionVirtualFile) item, restrictPatcher));
				}
			}
		}
		return list;
	}

	@Override
	protected void update(PresentationData presentation)
	{
		presentation.setIcon(AllIcons.Nodes.Module);
		presentation.addText(getValue().getName(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
	}
}
