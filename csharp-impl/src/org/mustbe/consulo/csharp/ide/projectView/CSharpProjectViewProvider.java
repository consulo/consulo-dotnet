/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.csharp.ide.projectView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetMemberOwner;
import com.intellij.ide.projectView.SelectableTreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 09.12.13.
 */
public class CSharpProjectViewProvider implements SelectableTreeStructureProvider, DumbAware
{
	@Nullable
	@Override
	public PsiElement getTopLevelElement(PsiElement element)
	{
		return null;
	}

	@Override
	public Collection<AbstractTreeNode> modify(AbstractTreeNode abstractTreeNode, Collection<AbstractTreeNode> abstractTreeNodes, ViewSettings
			settings)
	{
		if(!settings.isShowMembers())
		{
			return abstractTreeNodes;
		}

		List<AbstractTreeNode> nodes = new ArrayList<AbstractTreeNode>(abstractTreeNodes.size());
		for(AbstractTreeNode treeNode : abstractTreeNodes)
		{
			Object element = treeNode.getValue();
			if(element instanceof DotNetMemberOwner)
			{
				nodes.add(new CSharpElementTreeNode((DotNetMemberOwner) element, settings));
			}
			else
			{
				nodes.add(treeNode);
			}
		}
		return nodes;
	}

	@Nullable
	@Override
	public Object getData(Collection<AbstractTreeNode> abstractTreeNodes, String s)
	{
		return null;
	}
}
