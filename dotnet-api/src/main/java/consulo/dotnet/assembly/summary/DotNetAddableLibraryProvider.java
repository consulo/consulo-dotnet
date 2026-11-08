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
package consulo.dotnet.assembly.summary;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.module.Module;

import java.util.List;

/**
 * The libraries of one project model that a module may add: a Consulo .NET module (the assemblies of its SDK), an MSBuild
 * project, a Unity project… A provider answers only for the modules of its model, and leaves out what the module
 * already references.
 *
 * @author VISTALL
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface DotNetAddableLibraryProvider {
    /**
     * @return the libraries the module does not reference yet; empty if the module is not of the project model of the provider
     */
    @RequiredReadAction
    List<DotNetAddableLibrary> getAddableLibraries(Module module);
}
