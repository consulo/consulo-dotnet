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
import consulo.application.dumb.DumbAware;
import consulo.dotnet.debugger.impl.breakpoint.properties.DotNetMethodBreakpointProperties;
import consulo.execution.debug.XBreakpointManager;
import consulo.execution.debug.XDebuggerManager;
import consulo.execution.debug.breakpoint.XLineBreakpoint;
import consulo.execution.debug.event.XBreakpointListener;
import consulo.execution.debug.ui.XDebuggerUIConstants;
import consulo.project.Project;
import consulo.project.startup.PostStartupActivity;
import consulo.project.ui.notification.NotificationType;
import consulo.ui.UIAccess;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 03.05.2016
 */
@ExtensionImpl
public class DotNetBreakpointListenerComponent implements PostStartupActivity, DumbAware {
    @Override
    public void runActivity(@Nonnull Project project, @Nonnull UIAccess uiAccess) {
        XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();

        breakpointManager.addBreakpointListener(DotNetMethodBreakpointType.getInstance(), new XBreakpointListener<>() {
            @Override
            public void breakpointAdded(@Nonnull XLineBreakpoint<DotNetMethodBreakpointProperties> breakpoint) {
                XDebuggerUIConstants.NOTIFICATION_GROUP.createNotification("Method breakpoints may dramatically slow down debugging", NotificationType.WARNING).notify(project);
            }
        });
    }
}
