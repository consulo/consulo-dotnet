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

package consulo.dotnet.run.impl;

import consulo.configurable.ConfigurationException;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;

/**
 * @author VISTALL
 * @since 26.11.13.
 */
public class DotNetConfigurationEditor extends SettingsEditor<DotNetConfiguration> {
    private final DotNetProgramParametersPanel myProgramParametersPanel;

    public DotNetConfigurationEditor(Project project) {
        myProgramParametersPanel = new DotNetProgramParametersPanel(project);
    }

    @Override
    @RequiredUIAccess
    protected void resetEditorFrom(DotNetConfiguration runConfiguration) {
        myProgramParametersPanel.reset(runConfiguration);
    }

    @Override
    @RequiredUIAccess
    protected void applyEditorTo(DotNetConfiguration runConfiguration) throws ConfigurationException {
        myProgramParametersPanel.apply(runConfiguration);
    }

    @Override
    @RequiredUIAccess
    protected Component createUIComponent() {
        myProgramParametersPanel.build();
        return myProgramParametersPanel.getComponent();
    }
}
