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

package consulo.msil.impl.representation.projectView;

import consulo.application.progress.ProgressManager;
import consulo.language.psi.PsiElement;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.representation.MsilFileRepresentationManager;
import consulo.project.Project;
import consulo.project.ui.view.tree.AbstractTreeNode;
import consulo.project.ui.view.tree.SelectableTreeStructureProvider;
import consulo.project.ui.view.tree.ViewSettings;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public class MsilFileRepresentationProjectViewProvider implements SelectableTreeStructureProvider
{
	private final Project myProject;

	private Provider<MsilFileRepresentationManager> myManagerProvider;

	@Inject
	public MsilFileRepresentationProjectViewProvider(Project project, Provider<MsilFileRepresentationManager> managerProvider)
	{
		myProject = project;
		myManagerProvider = managerProvider;
	}

	@Nullable
	@Override
	public PsiElement getTopLevelElement(PsiElement element)
	{
		return null;
	}

	@Override
	public Collection<AbstractTreeNode> modify(AbstractTreeNode parent, Collection<AbstractTreeNode> children, ViewSettings settings)
	{
		List<AbstractTreeNode> newList = new ArrayList<>(children.size());

		for(AbstractTreeNode n : children)
		{
			ProgressManager.checkCanceled();

			if(n instanceof MsilFileNode)
			{
				newList.add(n);
				continue;
			}

			Object value = n.getValue();
			if(value instanceof MsilFile)
			{
				List<Pair<String, ? extends FileType>> representFiles = myManagerProvider.get().getRepresentFileInfos((MsilFile) value);
				if(representFiles.isEmpty())
				{
					newList.add(n);
				}
				else
				{
					newList.add(new MsilFileNode(myProject, (MsilFile) value, settings));
				}
			}
			else
			{
				newList.add(n);
			}
		}
		return newList;
	}
}
