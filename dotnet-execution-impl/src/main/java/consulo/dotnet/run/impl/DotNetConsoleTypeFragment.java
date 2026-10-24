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
package consulo.dotnet.run.impl;

import consulo.disposer.Disposable;
import consulo.dotnet.execution.localize.DotNetExecutionLocalize;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.localize.LocalizeValue;
import consulo.process.ProcessConsoleType;
import consulo.project.Project;
import consulo.ui.ComboBox;
import consulo.ui.annotation.RequiredUIAccess;

/**
 * The console a {@link DotNetConfiguration} runs in.
 *
 * @author VISTALL
 * @since 2026-10-24
 */
class DotNetConsoleTypeFragment implements RunConfigurationFragment<DotNetConfiguration> {
    @Override
    public String getId() {
        return "dotnetConsoleType";
    }

    @Override
    public LocalizeValue getName() {
        return DotNetExecutionLocalize.runConfigurationFragmentConsole();
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @RequiredUIAccess
    @Override
    public RunConfigurationFragmentEditor<DotNetConfiguration> createEditor(Project project, Disposable parent) {
        ComboBox<ProcessConsoleType> consoleTypeBox = ComboBox.create(ProcessConsoleType.listSupported());
        consoleTypeBox.setRenderer((presentation, item) -> {
            ProcessConsoleType consoleType = item.getValue();
            if (consoleType != null) {
                presentation.append(consoleType.getDisplayName());
            }
        });

        return new RunConfigurationFragmentEditor<>() {
            @RequiredUIAccess
            @Override
            public void addRows(RunConfigurationFragmentLayout layout) {
                layout.addLabeled(DotNetExecutionLocalize.runConfigurationConsoleLabel(), consoleTypeBox);
            }

            @RequiredUIAccess
            @Override
            public void reset(DotNetConfiguration target) {
                consoleTypeBox.setValue(target.getConsoleType());
            }

            @RequiredUIAccess
            @Override
            public void apply(DotNetConfiguration target) {
                target.setConsoleType(consoleTypeBox.getValue());
            }
        };
    }
}
