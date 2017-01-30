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

package consulo.msbuild.importProvider.ui;

import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.jetbrains.annotations.Nullable;
import com.intellij.ide.util.newProjectWizard.ProjectNameStep;
import com.intellij.ide.util.newProjectWizard.modes.WizardMode;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.table.ComboBoxTableCellEditor;
import consulo.msbuild.importProvider.MSBuildImportProject;
import consulo.msbuild.importProvider.MSBuildImportTarget;

/**
 * @author VISTALL
 * @since 30-Jan-17
 */
public class MSBuildSetupTargetStep extends ProjectNameStep
{
	private List<MSBuildImportProject> myItems;

	public MSBuildSetupTargetStep(WizardContext wizardContext, @Nullable WizardMode mode, VirtualFile fileByPath)
	{
		super(wizardContext, mode);

		myItems = null; //VisualStudioImportBuilder.loadItems(fileByPath);

		ColumnInfo<MSBuildImportProject, String> nameColumn = new ColumnInfo<MSBuildImportProject, String>("Name")
		{
			@Nullable
			@Override
			public String valueOf(MSBuildImportProject tableItem)
			{
				return tableItem.getProjectInfo().getName();
			}
		};

		ColumnInfo<MSBuildImportProject, MSBuildImportTarget> targetColumn = new ColumnInfo<MSBuildImportProject, MSBuildImportTarget>("Framework")
		{
			@Nullable
			@Override
			public MSBuildImportTarget valueOf(MSBuildImportProject tableItem)
			{
				return tableItem.getTarget();
			}

			@Override
			public boolean isCellEditable(MSBuildImportProject tableItem)
			{
				return true;
			}

			@Override
			public void setValue(MSBuildImportProject tableItem, MSBuildImportTarget value)
			{
				tableItem.setTarget(value);
			}

			@Override
			public int getWidth(JTable table)
			{
				return 100;
			}

			@Nullable
			@Override
			public TableCellEditor getEditor(MSBuildImportProject o)
			{
				return ComboBoxTableCellEditor.INSTANCE;
			}

			@Override
			public Class getColumnClass()
			{
				return MSBuildImportTarget.class;
			}
		};
		ListTableModel<MSBuildImportProject> tableModel = new ListTableModel<>(new ColumnInfo[]{
				nameColumn,
				targetColumn
		}, myItems);
		TableView<MSBuildImportProject> tableItemTableView = new TableView<>(tableModel);

		myAdditionalContentPanel.add(ScrollPaneFactory.createScrollPane(tableItemTableView), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.BOTH, JBUI.emptyInsets(), 0, 0));
	}

	@Override
	public void updateDataModel()
	{
		super.updateDataModel();
		/*VisualStudioImportBuilder projectBuilder = (VisualStudioImportBuilder) myWizardContext.getProjectBuilder();
		assert projectBuilder != null;
		projectBuilder.setImportItems(myItems); */
	}
}