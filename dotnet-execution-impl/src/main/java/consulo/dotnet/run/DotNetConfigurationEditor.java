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

package consulo.dotnet.run;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.components.BorderLayoutPanel;
import consulo.ui.annotation.RequiredUIAccess;

import javax.annotation.Nonnull;
import javax.swing.*;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetConfigurationEditor extends SettingsEditor<DotNetConfiguration>
{
	private final Project myProject;

	private DotNetProgramParametersPanel myProgramParametersPanel;

	public DotNetConfigurationEditor(Project project)
	{
		myProject = project;
	}

	@Override
	protected void resetEditorFrom(DotNetConfiguration runConfiguration)
	{
		myProgramParametersPanel.reset(runConfiguration);
	}

	@Override
	protected void applyEditorTo(DotNetConfiguration runConfiguration) throws ConfigurationException
	{
		myProgramParametersPanel.applyTo(runConfiguration);
	}

	@Nonnull
	@Override
	@RequiredUIAccess
	protected JComponent createEditor()
	{
		myProgramParametersPanel = new DotNetProgramParametersPanel(myProject);
		return new BorderLayoutPanel().addToTop(myProgramParametersPanel);
	}
}
