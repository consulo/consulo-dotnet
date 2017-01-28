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
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.SmartList;
import consulo.msbuild.solution.SolutionVirtualFile;

/**
 * @author VISTALL
 * @since 29-Jan-17
 */
public class SolutionViewTreePatcher implements TreeStructureProvider
{
	@Override
	public Collection<AbstractTreeNode> modify(AbstractTreeNode parent, Collection<AbstractTreeNode> children, ViewSettings settings)
	{
		List<AbstractTreeNode> nodes = new ArrayList<>(children.size());

		List<SolutionViewGroupNode> groups = new SmartList<>();

		// in first iteration we need generate group nodes
		for(AbstractTreeNode child : children)
		{
			if(child instanceof SolutionViewFileNode && !((SolutionViewFileNode) child).isRestrictPatcher())
			{
				SolutionVirtualFile solutionVirtualFile = ((SolutionViewFileNode) child).getSolutionVirtualFile();
				if(solutionVirtualFile.getSubType() != null)
				{
					SolutionViewGroupNode groupNode = new SolutionViewGroupNode(parent.getProject(), solutionVirtualFile, settings);

					// add self
					groupNode.addChildren(solutionVirtualFile);

					nodes.add(groupNode);
					groups.add(groupNode);
				}
				else
				{
					nodes.add(child);
				}
			}
			else
			{
				nodes.add(child);
			}
		}

		Iterator<AbstractTreeNode> iterator = nodes.iterator();
		while(iterator.hasNext())
		{
			AbstractTreeNode next = iterator.next();

			if(next instanceof SolutionViewFileNode && !((SolutionViewFileNode) next).isRestrictPatcher())
			{
				SolutionVirtualFile solutionVirtualFile = ((SolutionViewFileNode) next).getSolutionVirtualFile();

				VirtualFile vFile = solutionVirtualFile.getVirtualFile();

				String dependentUpon = solutionVirtualFile.getDependentUpon();
				if(dependentUpon != null)
				{
					// we need find correct child in same parent
					for(SolutionViewGroupNode group : groups)
					{
						SolutionVirtualFile value = group.getValue();

						VirtualFile groupVFile = value.getVirtualFile();

						if(Comparing.equal(groupVFile.getParent(), solutionVirtualFile.getVirtualFile().getParent()) && dependentUpon.equals(groupVFile.getName()))
						{
							group.addChildren(solutionVirtualFile);

							iterator.remove();

							break;
						}
					}
				}
			}
		}
		return nodes;
	}

	@Nullable
	@Override
	public Object getData(Collection<AbstractTreeNode> selected, String dataName)
	{
		return null;
	}
}
