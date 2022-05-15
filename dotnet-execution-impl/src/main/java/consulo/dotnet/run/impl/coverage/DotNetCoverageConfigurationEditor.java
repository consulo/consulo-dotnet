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

package consulo.dotnet.run.impl.coverage;

import consulo.configurable.ConfigurationException;
import consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import consulo.execution.configuration.RunConfigurationBase;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.execution.coverage.CoverageEnabledConfiguration;
import consulo.execution.coverage.CoverageRunner;
import consulo.ui.ex.SimpleTextAttributes;
import consulo.ui.ex.awt.*;

import javax.annotation.Nonnull;
import javax.swing.*;

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
		myPanel.add(LabeledComponent.create(myRunnersBox, "Runner"));
		myRunnersBox.setRenderer(new ColoredListCellRenderer<Object>()
		{
			@Override
			protected void customizeCellRenderer(@Nonnull JList<?> jList, Object value, int i, boolean b, boolean b1)
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

	@Nonnull
	@Override
	protected JComponent createEditor()
	{
		return myPanel;
	}
}
