/*
 * Copyright 2013-2026 consulo.io
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

import consulo.disposer.Disposable;
import consulo.dotnet.execution.localize.DotNetExecutionLocalize;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.execution.localize.ExecutionLocalize;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.ComboBox;
import consulo.ui.IntBox;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;

/**
 * Host, port and mode of a {@link DotNetRemoteConfiguration}.
 *
 * @author VISTALL
 * @since 2026-10-24
 */
class DotNetRemoteConnectionFragment implements RunConfigurationFragment<DotNetRemoteConfiguration> {
    private static final int MAX_PORT = 65535;

    @Override
    public String getId() {
        return "dotnetRemoteConnection";
    }

    @Override
    public LocalizeValue getName() {
        return DotNetExecutionLocalize.remoteConfigurationFragmentConnection();
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @RequiredUIAccess
    @Override
    public RunConfigurationFragmentEditor<DotNetRemoteConfiguration> createEditor(Project project, Disposable parent) {
        TextBox hostField = TextBox.create();
        IntBox portField = IntBox.create().withRange(0, MAX_PORT);
        ComboBox<Boolean> modeBox = ComboBox.<Boolean>builder()
            .add(Boolean.TRUE, ExecutionLocalize.remoteConfigurationAttachRadio())
            .add(Boolean.FALSE, ExecutionLocalize.remoteConfigurationListenRadio())
            .build();

        return new RunConfigurationFragmentEditor<>() {
            @RequiredUIAccess
            @Override
            public void addRows(RunConfigurationFragmentLayout layout) {
                layout.addLabeled(ExecutionLocalize.remoteConfigurationHostLabel(), hostField);
                layout.addLabeled(ExecutionLocalize.remoteConfigurationPortLabel(), portField);
                layout.addLabeled(ExecutionLocalize.remoteConfigurationDebuggerModeLabel(), modeBox);
            }

            @RequiredUIAccess
            @Override
            public void reset(DotNetRemoteConfiguration target) {
                DotNetRemoteConfigurationOptions options = target.getOptions();
                String host = options.getHost();
                hostField.setValue(host == null ? "" : host);
                portField.setValue(options.getPort());
                modeBox.setValue(options.isServerMode());
            }

            @RequiredUIAccess
            @Override
            public void apply(DotNetRemoteConfiguration target) {
                DotNetRemoteConfigurationOptions options = target.getOptions();
                options.setHost(hostField.getValue());
                options.setPort(portField.getValueOrError());
                options.setServerMode(modeBox.getValueOrError());
            }
        };
    }
}
