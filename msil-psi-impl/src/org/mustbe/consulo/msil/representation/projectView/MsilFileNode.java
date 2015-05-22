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

package org.mustbe.consulo.msil.representation.projectView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mustbe.consulo.msil.lang.psi.MsilFile;
import org.mustbe.consulo.msil.representation.MsilFileRepresentationManager;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import lombok.val;

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
		val representFiles = MsilFileRepresentationManager.getInstance(getProject()).getRepresentFileInfos((MsilFile) getValue());
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
