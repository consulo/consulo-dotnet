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

package consulo.dotnet.debugger.breakpoint;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import consulo.dotnet.debugger.breakpoint.properties.DotNetMethodBreakpointProperties;

/**
 * @author VISTALL
 * @since 03.05.2016
 */
@Singleton
public class DotNetBreakpointListenerComponent
{
	@Inject
	public DotNetBreakpointListenerComponent(Project project, StartupManager startupManager)
	{
		startupManager.registerPostStartupActivity(uiAccess ->
		{
			XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();

			breakpointManager.addBreakpointListener(DotNetMethodBreakpointType.getInstance(), new XBreakpointListener<XLineBreakpoint<DotNetMethodBreakpointProperties>>()
			{
				@Override
				public void breakpointAdded(@Nonnull XLineBreakpoint<DotNetMethodBreakpointProperties> breakpoint)
				{
					XDebugSessionImpl.NOTIFICATION_GROUP.createNotification("Method breakpoints may dramatically slow down debugging", MessageType.WARNING).notify(project);
				}
			});
		});
	}
}
