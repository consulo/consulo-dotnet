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

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.ide.SelectInManager;
import com.intellij.ide.SelectInTarget;
import com.intellij.ide.impl.ProjectViewSelectInTarget;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.AbstractProjectViewPSIPane;
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase;
import com.intellij.ide.projectView.impl.ProjectTreeStructure;
import com.intellij.ide.projectView.impl.ProjectViewTree;
import com.intellij.ide.util.treeView.AbstractTreeBuilder;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.AbstractTreeUpdater;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.annotations.RequiredReadAction;
import consulo.msbuild.MSBuildIcons;
import consulo.msbuild.MSBuildSolutionManager;

/**
 * @author VISTALL
 * @since 28-Jan-17
 */
public class SolutionViewPane extends AbstractProjectViewPSIPane
{
	public static final String ID = "SolutionViewPane";

	public SolutionViewPane(Project project)
	{
		super(project);
	}


	@Override
	protected ProjectAbstractTreeStructureBase createStructure()
	{
		return new ProjectTreeStructure(myProject, ID)
		{
			@Override
			@RequiredReadAction
			protected AbstractTreeNode createRoot(Project project, ViewSettings settings)
			{
				MSBuildSolutionManager solutionManager = MSBuildSolutionManager.getInstance(myProject);

				VirtualFile solutionFile = solutionManager.getSolutionFile();

				assert solutionFile != null;

				return new SolutionViewRootNode(project, solutionFile, settings);
			}
		};
	}

	@Override
	protected ProjectViewTree createTree(DefaultTreeModel treeModel)
	{
		return new ProjectViewTree(myProject, treeModel)
		{
			@Override
			public DefaultMutableTreeNode getSelectedNode()
			{
				return SolutionViewPane.this.getSelectedNode();
			}
		};
	}

	@Override
	public JComponent createComponent()
	{
		JComponent component = super.createComponent();
		myTree.setRootVisible(true);
		return component;
	}

	@Override
	protected AbstractTreeUpdater createTreeUpdater(AbstractTreeBuilder treeBuilder)
	{
		return new AbstractTreeUpdater(treeBuilder);
	}

	@Override
	public String getTitle()
	{
		return "Solution Explorer";
	}

	@Override
	public Icon getIcon()
	{
		return MSBuildIcons.Msbuild;
	}

	@NotNull
	@Override
	public String getId()
	{
		return ID;
	}

	@Override
	public int getWeight()
	{
		return 2;
	}

	@Override
	public SelectInTarget createSelectInTarget()
	{
		return new ProjectViewSelectInTarget(myProject)
		{
			@Override
			public String toString()
			{
				return SelectInManager.PROJECT;
			}

			@Nullable
			@Override
			public String getMinorViewId()
			{
				return null;
			}

			@Override
			public float getWeight()
			{
				return 0;
			}
		};
	}
}
