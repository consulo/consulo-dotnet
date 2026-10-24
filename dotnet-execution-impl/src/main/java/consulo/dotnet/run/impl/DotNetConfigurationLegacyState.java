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

import consulo.process.ProcessConsoleType;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The bean properties a .NET configuration wrote before the fragment model ({@code <option name value>}, the environment as
 * a map).
 *
 * @author VISTALL
 * @since 2026-10-24
 */
public class DotNetConfigurationLegacyState {
    private @Nullable String myProgramParameters;
    private @Nullable String myWorkingDirectory;
    private Map<String, String> myEnvs = new LinkedHashMap<>();
    private boolean myPassParentEnvs = true;
    private @Nullable ProcessConsoleType myConsoleType;

    public @Nullable String getProgramParameters() {
        return myProgramParameters;
    }

    public void setProgramParameters(@Nullable String programParameters) {
        myProgramParameters = programParameters;
    }

    public @Nullable String getWorkingDirectory() {
        return myWorkingDirectory;
    }

    public void setWorkingDirectory(@Nullable String workingDirectory) {
        myWorkingDirectory = workingDirectory;
    }

    public Map<String, String> getEnvs() {
        return myEnvs;
    }

    public void setEnvs(Map<String, String> envs) {
        myEnvs = envs;
    }

    public boolean isPassParentEnvs() {
        return myPassParentEnvs;
    }

    public void setPassParentEnvs(boolean passParentEnvs) {
        myPassParentEnvs = passParentEnvs;
    }

    public @Nullable ProcessConsoleType getConsoleType() {
        return myConsoleType;
    }

    public void setConsoleType(@Nullable ProcessConsoleType consoleType) {
        myConsoleType = consoleType;
    }
}
