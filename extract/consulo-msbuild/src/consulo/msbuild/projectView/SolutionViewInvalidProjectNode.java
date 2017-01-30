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

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.SimpleTextAttributes;
import consulo.annotations.RequiredDispatchThread;
import consulo.msbuild.solution.model.WProject;

/**
 * @author VISTALL
 * @since 30-Jan-17
 */
public class SolutionViewInvalidProjectNode extends ProjectViewNode<WProject> implements SolutionViewProjectNodeBase
{
	public SolutionViewInvalidProjectNode(com.intellij.openapi.project.Project project, WProject wProject, ViewSettings viewSettings)
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
		return Collections.emptyList();
	}

	@Override
	protected void update(PresentationData presentation)
	{
		presentation.setIcon(AllIcons.Nodes.Module);
		presentation.addText(getValue().getName(), SimpleTextAttributes.GRAYED_BOLD_ATTRIBUTES);
		presentation.addText(" (", SimpleTextAttributes.GRAY_ATTRIBUTES);
		switch(getValue().getFailReason())
		{
			case not_supported:
				presentation.addText("not supported", SimpleTextAttributes.GRAY_ATTRIBUTES);
				break;
			case project_not_found:
				presentation.addText("project not found", SimpleTextAttributes.GRAY_ATTRIBUTES);
				break;
		}
		presentation.addText(")", SimpleTextAttributes.GRAY_ATTRIBUTES);
	}
}
