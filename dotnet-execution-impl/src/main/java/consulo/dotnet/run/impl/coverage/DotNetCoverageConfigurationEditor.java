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
import consulo.dotnet.execution.localize.DotNetExecutionLocalize;
import consulo.dotnet.run.coverage.DotNetConfigurationWithCoverage;
import consulo.execution.configuration.RunConfigurationBase;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.execution.coverage.CoverageEnabledConfiguration;
import consulo.execution.coverage.CoverageRunner;
import consulo.ui.CheckBox;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.TextAttribute;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.ui.util.FormBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 10.01.15
 */
public class DotNetCoverageConfigurationEditor extends SettingsEditor<DotNetConfigurationWithCoverage>
{
	private static final Object NO_RUNNER = new Object();

	private final MutableFlatDataModel<Object> myRunnersModel = FlatDataModel.of(List.of(NO_RUNNER));
	private final ComboBox<Object> myRunnersBox;
	private final CheckBox myEnabledCheckBox;

	@RequiredUIAccess
	public DotNetCoverageConfigurationEditor()
	{
		myEnabledCheckBox = CheckBox.create(DotNetExecutionLocalize.coverageEnabledCheckbox());

		myRunnersBox = ComboBox.create(myRunnersModel);
		myRunnersBox.setRender((presentation, item) ->
		{
			Object value = item.getValue();
			if(value == null || value == NO_RUNNER)
			{
				presentation.append(DotNetExecutionLocalize.coverageNoRunner());
			}
			else if(value instanceof String runnerId)
			{
				presentation.append(runnerId, TextAttribute.ERROR);
			}
			else if(value instanceof CoverageRunner coverageRunner)
			{
				presentation.append(coverageRunner.getPresentableName());
			}
		});
	}

	@Override
	@RequiredUIAccess
	protected void resetEditorFrom(DotNetConfigurationWithCoverage s)
	{
		CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.getOrCreate((RunConfigurationBase) s);

		myEnabledCheckBox.setValue(coverageEnabledConfiguration.isCoverageEnabled());

		List<Object> runners = new ArrayList<>();
		runners.add(NO_RUNNER);
		runners.addAll(DotNetCoverageRunner.findAvailableRunners(s));

		Object selected = NO_RUNNER;
		CoverageRunner coverageRunner = coverageEnabledConfiguration.getCoverageRunner();
		if(coverageRunner != null)
		{
			selected = coverageRunner;
		}
		else if(coverageEnabledConfiguration.getRunnerId() != null)
		{
			selected = coverageEnabledConfiguration.getRunnerId();
			runners.add(selected);
		}

		myRunnersModel.replaceAll(runners);
		myRunnersBox.setValue(selected);
	}

	@Override
	@RequiredUIAccess
	protected void applyEditorTo(DotNetConfigurationWithCoverage s) throws ConfigurationException
	{
		CoverageEnabledConfiguration coverageEnabledConfiguration = DotNetCoverageEnabledConfiguration.getOrCreate((RunConfigurationBase) s);

		coverageEnabledConfiguration.setCoverageEnabled(Boolean.TRUE.equals(myEnabledCheckBox.getValue()));

		Object selectedItem = myRunnersBox.getValue();
		if(selectedItem instanceof CoverageRunner coverageRunner)
		{
			coverageEnabledConfiguration.setCoverageRunner(coverageRunner);
		}
		else if(selectedItem == null || selectedItem == NO_RUNNER)
		{
			coverageEnabledConfiguration.setCoverageRunner(null);
		}
	}

	@Override
	@RequiredUIAccess
	protected Component createUIComponent()
	{
		VerticalLayout layout = VerticalLayout.create();
		layout.add(myEnabledCheckBox);
		layout.add(FormBuilder.create().addLabeled(DotNetExecutionLocalize.coverageRunnerLabel(), myRunnersBox).build());
		return layout;
	}
}
