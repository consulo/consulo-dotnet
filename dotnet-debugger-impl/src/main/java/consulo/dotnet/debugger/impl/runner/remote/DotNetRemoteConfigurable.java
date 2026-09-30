/*
 * Copyright 2013-2016 must-be.org
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
package consulo.dotnet.debugger.impl.runner.remote;

import consulo.configurable.ConfigurationException;
import consulo.dotnet.execution.localize.DotNetExecutionLocalize;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.execution.localize.ExecutionLocalize;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.IntBox;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.util.FormBuilder;

/**
 * @author VISTALL
 * @since 2016-12-27
 */
public class DotNetRemoteConfigurable<C extends DotNetRemoteConfiguration> extends SettingsEditor<C> {
    private static final int MAX_PORT = 65535;

    private final TextBox myHostField;
    private final IntBox myPortField;
    private final ComboBox<Module> myModuleComboBox;
    private final ComboBox<Boolean> myModeBox;

    @RequiredUIAccess
    public DotNetRemoteConfigurable(Project project) {
        myHostField = TextBox.create();
        myPortField = IntBox.create().withRange(0, MAX_PORT);

        myModuleComboBox = ComboBox.create(ModuleManager.getInstance(project).getSortedModules());
        myModuleComboBox.setRender((presentation, item) -> {
            Module module = item.getValue();
            if (module == null) {
                presentation.append(DotNetExecutionLocalize.runConfigurationModuleNone());
            }
            else {
                presentation.withIcon(PlatformIconGroup.nodesModule());
                presentation.append(module.getName());
            }
        });

        myModeBox = ComboBox.<Boolean>builder()
            .add(Boolean.TRUE, ExecutionLocalize.remoteConfigurationAttachRadio())
            .add(Boolean.FALSE, ExecutionLocalize.remoteConfigurationListenRadio())
            .build();
    }

    @Override
    @RequiredUIAccess
    protected Component createUIComponent() {
        FormBuilder formBuilder = FormBuilder.create();
        formBuilder.addLabeled(ExecutionLocalize.remoteConfigurationHostLabel(), myHostField);
        formBuilder.addLabeled(ExecutionLocalize.remoteConfigurationPortLabel(), myPortField);
        formBuilder.addLabeled(DotNetExecutionLocalize.runConfigurationModuleLabel(), myModuleComboBox);
        formBuilder.addLabeled(ExecutionLocalize.remoteConfigurationDebuggerModeLabel(), myModeBox);
        return formBuilder.build();
    }

    @Override
    @RequiredUIAccess
    protected void resetEditorFrom(C remoteConfiguration) {
        myHostField.setValue(remoteConfiguration.HOST);
        myPortField.setValue(remoteConfiguration.PORT);
        myModuleComboBox.setValue(remoteConfiguration.getConfigurationModule().getModule());
        myModeBox.setValue(remoteConfiguration.SERVER_MODE);
    }

    @Override
    @RequiredUIAccess
    protected void applyEditorTo(C remoteConfiguration) throws ConfigurationException {
        remoteConfiguration.HOST = myHostField.getValue();
        remoteConfiguration.PORT = myPortField.getValueOrError();
        remoteConfiguration.SERVER_MODE = myModeBox.getValueOrError();
        remoteConfiguration.getConfigurationModule().setModule(myModuleComboBox.getValue());
    }
}
