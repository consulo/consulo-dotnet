package org.mustbe.consulo.nunit.run;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.intellij.execution.testframework.ui.AbstractTestTreeBuilder;
import com.intellij.ide.util.treeView.AbstractTreeStructure;
import com.intellij.ide.util.treeView.IndexComparator;

/**
 * @author VISTALL
 * @since 28.03.14
 */
public class NUnitTreeBuilder extends AbstractTestTreeBuilder
{
	public NUnitTreeBuilder(JTree tree, AbstractTreeStructure structure)
	{
		super(tree, new DefaultTreeModel(new DefaultMutableTreeNode(structure.getRootElement())), structure, IndexComparator.INSTANCE);
	}
}
