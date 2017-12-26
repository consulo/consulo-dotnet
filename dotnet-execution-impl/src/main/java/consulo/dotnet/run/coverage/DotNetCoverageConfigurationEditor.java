/*
 * Copyright 2013-2015 must-be.org
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

package consulo.dotnet.run.coverage;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;
import com.intellij.coverage.CoverageRunner;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.ColoredListCellRendererWrapper;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBCheckBox;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class DotNetCoverageConfigurationEditor extends SettingsEditor<DotNetConfigurationWithCoverage>
{
	private JPanel myPanel = new JPanel(new VerticalFlowLayout());
	private ComboBox myRunnersBox = new ComboBox();
	private JBCheckBox myEnabledCheckBox = new JBCheckBox("Enabled?");

	public DotNetCoverageConfigurationEditor()
	{
		myPanel.add(myEnabledCheckBox);
		myPanel.add(LabeledComponent.left(myRunnersBox, "Runner"));
		myRunnersBox.setRenderer(new ColoredListCellRendererWrapper<Object>()
		{
			@Override
			protected void doCustomize(JList list, Object value, int index, boolean selected, boolean hasFocus)
			{
				if(value == null)
				{
					append("<none>");
				}
				else if(value instanceof String)
				{
					append((String) value, SimpleTextAttributes.ERROR_ATTRIBUTES);
				}
				else if(value instanceof CoverageRunner)
				{
					append(((CoverageRunner) value).getPresentableName());
				}
			}
		});
	}

	@Override
	protected void resetEditorFrom(DotNetConfigurationWithCoverage s)
	{
		CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.getOrCreate((RunConfigurationBase) s);

		myEnabledCheckBox.setSelected(coverageEnabledConfiguration.isCoverageEnabled());
		myRunnersBox.removeAllItems();

		for(DotNetCoverageRunner coverageRunner : DotNetCoverageRunner.findAvailableRunners(s))
		{
			myRunnersBox.addItem(coverageRunner);
		}

		CoverageRunner coverageRunner = coverageEnabledConfiguration.getCoverageRunner();
		if(coverageRunner != null)
		{
			myRunnersBox.setSelectedItem(coverageRunner);
		}
		else if(coverageEnabledConfiguration.getRunnerId() != null)
		{
			myRunnersBox.setSelectedItem(coverageEnabledConfiguration.getRunnerId());
		}
		else
		{
			myRunnersBox.setSelectedItem(null);
		}
	}

	@Override
	protected void applyEditorTo(DotNetConfigurationWithCoverage s) throws ConfigurationException
	{
		CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.getOrCreate((RunConfigurationBase) s);

		coverageEnabledConfiguration.setCoverageEnabled(myEnabledCheckBox.isSelected());

		Object selectedItem = myRunnersBox.getSelectedItem();
		if(selectedItem instanceof CoverageRunner)
		{
			coverageEnabledConfiguration.setCoverageRunner((CoverageRunner) selectedItem);
		}
		else if(selectedItem == null) // we dont interest string value, due it already set to configuration
		{
			coverageEnabledConfiguration.setCoverageRunner(null);
		}
	}

	@NotNull
	@Override
	protected JComponent createEditor()
	{
		return myPanel;
	}
}
