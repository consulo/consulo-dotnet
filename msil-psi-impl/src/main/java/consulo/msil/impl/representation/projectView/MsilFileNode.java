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

import consulo.ide.impl.idea.ide.projectView.impl.nodes.PsiFileNode;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.representation.MsilFileRepresentationManager;
import consulo.project.Project;
import consulo.project.ui.view.tree.AbstractTreeNode;
import consulo.project.ui.view.tree.ViewSettings;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.fileType.FileType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public class MsilFileNode extends PsiFileNode
{
	public MsilFileNode(Project project, MsilFile value, ViewSettings viewSettings)
	{
		super(project, value, viewSettings);
	}

	@Override
	public Collection<AbstractTreeNode> getChildrenImpl()
	{
		List<Pair<String, ? extends FileType>> representFiles = MsilFileRepresentationManager.getInstance(getProject()).getRepresentFileInfos((MsilFile) getValue());
		if(representFiles.isEmpty())
		{
			return Collections.emptyList();
		}

		List<AbstractTreeNode> list = new ArrayList<AbstractTreeNode>(representFiles.size());
		for(Pair<String, ? extends FileType> data : representFiles)
		{
			list.add(new MsilRepresentFileNode(getProject(), (MsilFile) getValue(), data));
		}
		return list;
	}

	@Override
	public boolean expandOnDoubleClick()
	{
		return false;
	}
}
