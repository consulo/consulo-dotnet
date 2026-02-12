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
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.util.FormBuilder;

import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 2016-12-27
 */
public class DotNetRemoteConfigurable<C extends DotNetRemoteConfiguration> extends SettingsEditor<C> {
    private final Project myProject;

    private TextBox myHostField;
    private TextBox myPortField;
    private ComboBox<Module> myModuleComboBox;
    private ComboBox<Boolean> myModeBox;

    public DotNetRemoteConfigurable(Project project) {
        myProject = project;
    }

    @Nullable
    @Override
    @RequiredUIAccess
    protected Component createUIComponent() {
        FormBuilder formBuilder = FormBuilder.create();

        formBuilder.addLabeled("Host", myHostField = TextBox.create());
        formBuilder.addLabeled("Port", myPortField = TextBox.create());
        formBuilder.addLabeled("Module", myModuleComboBox = ComboBox.create(ModuleManager.getInstance(myProject).getSortedModules()));

        ComboBox.Builder<Boolean> modeBuilder = ComboBox.builder();
        modeBuilder.add(Boolean.TRUE, "attach");
        modeBuilder.add(Boolean.FALSE, "listen");
        myModeBox = modeBuilder.build();

        formBuilder.addLabeled("Mode", myModeBox);

        myModuleComboBox.setRenderer((presentation, i, module) -> {
            if (module == null) {
                presentation.append("<none>");
            }
            else {
                presentation.withIcon(PlatformIconGroup.nodesModule());
                presentation.append(module.getName());
            }
        });
        return formBuilder.build();
    }

    @Override
    @RequiredUIAccess
    protected void resetEditorFrom(C remoteConfiguration) {
        myHostField.setValue(remoteConfiguration.HOST);
        myPortField.setValue(String.valueOf(remoteConfiguration.PORT));
        Module module = remoteConfiguration.getConfigurationModule().getModule();
        myModuleComboBox.setValue(module != null ? module : null);
        myModeBox.setValue(remoteConfiguration.SERVER_MODE);
    }

    @Override
    @RequiredUIAccess
    protected void applyEditorTo(C remoteConfiguration) throws ConfigurationException {
        remoteConfiguration.HOST = myHostField.getValue();
        try {
            remoteConfiguration.PORT = Integer.parseInt(myPortField.getValue());
        }
        catch (NumberFormatException e) {
            //
        }
        remoteConfiguration.SERVER_MODE = myModeBox.getValue();
        remoteConfiguration.getConfigurationModule().setModule(myModuleComboBox.getValue());
    }
}
