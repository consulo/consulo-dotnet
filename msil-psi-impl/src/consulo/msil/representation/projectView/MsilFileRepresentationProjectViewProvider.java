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

package consulo.msil.representation.projectView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import com.intellij.ide.projectView.SelectableTreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.representation.MsilFileRepresentationManager;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public class MsilFileRepresentationProjectViewProvider implements SelectableTreeStructureProvider
{
	private final Project myProject;

	public MsilFileRepresentationProjectViewProvider(Project project)
	{
		myProject = project;
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
		List<AbstractTreeNode> newList = new ArrayList<AbstractTreeNode>(children.size());

		for(AbstractTreeNode n : children)
		{
			if(n instanceof MsilFileNode)
			{
				newList.add(n);
				continue;
			}

			Object value = n.getValue();
			if(value instanceof MsilFile)
			{
				List<Pair<String, ? extends FileType>> representFiles = MsilFileRepresentationManager.getInstance(myProject).getRepresentFileInfos((MsilFile) value);
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

	@Nullable
	@Override
	public Object getData(Collection<AbstractTreeNode> selected, String dataName)
	{
		return null;
	}
}
