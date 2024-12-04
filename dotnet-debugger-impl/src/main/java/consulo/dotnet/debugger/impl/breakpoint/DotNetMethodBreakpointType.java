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

package consulo.dotnet.debugger.impl.breakpoint;

import consulo.annotation.component.ExtensionImpl;
import consulo.dotnet.debugger.impl.breakpoint.properties.DotNetMethodBreakpointProperties;
import consulo.dotnet.debugger.impl.breakpoint.ui.DotNetMethodBreakpointPropertiesPanel;
import consulo.execution.debug.breakpoint.XLineBreakpoint;
import consulo.execution.debug.breakpoint.XLineBreakpointType;
import consulo.execution.debug.breakpoint.ui.XBreakpointCustomPropertiesPanel;
import consulo.execution.debug.icon.ExecutionDebugIconGroup;
import consulo.project.Project;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;

/**
 * @author VISTALL
 * @since 03.05.2016
 */
@ExtensionImpl
public class DotNetMethodBreakpointType extends XLineBreakpointType<DotNetMethodBreakpointProperties> {
    @Nonnull
    public static DotNetMethodBreakpointType getInstance() {
        return EXTENSION_POINT_NAME.findExtensionOrFail(DotNetMethodBreakpointType.class);
    }

    @Inject
    DotNetMethodBreakpointType() {
        super("dotnet-method-breakpoint", ".NET Method Breakpoints");
    }

    @Nonnull
    @Override
    public Image getEnabledIcon() {
        return ExecutionDebugIconGroup.breakpointBreakpointmethod();
    }

    @Nonnull
    @Override
    public Image getDisabledIcon() {
        return ExecutionDebugIconGroup.breakpointBreakpointmethoddisabled();
    }

    @Nullable
    @Override
    public XBreakpointCustomPropertiesPanel<XLineBreakpoint<DotNetMethodBreakpointProperties>> createCustomPropertiesPanel(@Nonnull Project project) {
        return new DotNetMethodBreakpointPropertiesPanel();
    }

    @Nullable
    @Override
    public DotNetMethodBreakpointProperties createBreakpointProperties(@Nonnull VirtualFile file, int line) {
        return new DotNetMethodBreakpointProperties();
    }

    @Override
    public boolean canBeHitInOtherPlaces() {
        return true;
    }
}
