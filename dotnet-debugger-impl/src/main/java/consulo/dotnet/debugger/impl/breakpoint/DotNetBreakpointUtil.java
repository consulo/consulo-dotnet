/*
 * Copyright 2013-2015 must-be.org
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

import consulo.execution.debug.XBreakpointManager;
import consulo.execution.debug.XDebuggerManager;
import consulo.execution.debug.breakpoint.SuspendPolicy;
import consulo.execution.debug.breakpoint.XLineBreakpoint;
import consulo.execution.debug.icon.ExecutionDebugIconGroup;
import consulo.project.Project;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 19.07.2015
 */
public class DotNetBreakpointUtil {
    public static void updateLineBreakpointIcon(@Nonnull Project project, Boolean result, @Nonnull XLineBreakpoint breakpoint) {
        XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();

        Image icon = null;
        SuspendPolicy suspendPolicy = breakpoint.getSuspendPolicy();
        if (breakpoint.isTemporary()) {
            if (suspendPolicy == SuspendPolicy.NONE) {
                icon = ExecutionDebugIconGroup.breakpointBreakpoint();
            }
            else {
                icon = ExecutionDebugIconGroup.breakpointBreakpointmuted();
            }
        }
        else {
            if (result == null) {
                // cleanup it
            }
            else if (result) {
                if (suspendPolicy == SuspendPolicy.NONE || !breakpoint.isEnabled()) {
                    icon = ExecutionDebugIconGroup.breakpointBreakpointmuted();
                }
                else {
                    icon = ExecutionDebugIconGroup.breakpointBreakpointvalid();
                }
            }
            else {
                if (suspendPolicy == SuspendPolicy.NONE || !breakpoint.isEnabled()) {
                    icon = ExecutionDebugIconGroup.breakpointBreakpointmuteddisabled();
                }
                else {
                    icon = ExecutionDebugIconGroup.breakpointBreakpointinvalid();
                }
            }
        }
        breakpointManager.updateBreakpointPresentation(breakpoint, icon, null);
    }
}
