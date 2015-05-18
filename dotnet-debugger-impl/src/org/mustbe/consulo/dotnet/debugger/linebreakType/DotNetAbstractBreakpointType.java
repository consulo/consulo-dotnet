/*
 * Copyright 2013-2014 must-be.org
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

package org.mustbe.consulo.dotnet.debugger.linebreakType;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetVirtualMachine;
import org.mustbe.consulo.dotnet.debugger.TypeMirrorUnloadedException;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import mono.debugger.TypeMirror;
import mono.debugger.VMDisconnectedException;
import mono.debugger.request.EventRequest;

/**
 * @author VISTALL
 * @since 13.04.14
 */
public abstract class DotNetAbstractBreakpointType extends XLineBreakpointTypeBase
{
	public DotNetAbstractBreakpointType(@NonNls @NotNull String id, @Nls @NotNull String title, @Nullable XDebuggerEditorsProvider editorsProvider)
	{
		super(id, title, editorsProvider);
	}

	public boolean createRequest(@NotNull XDebugSession debugSession,
			@NotNull DotNetVirtualMachine virtualMachine,
			@NotNull XLineBreakpoint breakpoint,
			@Nullable TypeMirror typeMirror)
	{
		try
		{
			EventRequest eventRequest = virtualMachine.getRequest(breakpoint);
			if(eventRequest != null)
			{
				eventRequest.disable();
			}
			return createRequestImpl(debugSession.getProject(), virtualMachine, breakpoint, typeMirror);
		}
		catch(VMDisconnectedException ignored)
		{
		}
		catch(TypeMirrorUnloadedException e)
		{
			debugSession.getConsoleView().print(e.getFullName(), ConsoleViewContentType.ERROR_OUTPUT);
			debugSession.getConsoleView().print("You can fix this error - restart debug. If you can repeat this error, " +
					"please report it here 'https://github.com/consulo/consulo-dotnet/issues'", ConsoleViewContentType.ERROR_OUTPUT);
		}
		return false;
	}

	protected abstract boolean createRequestImpl(@NotNull Project project,
			@NotNull DotNetVirtualMachine virtualMachine,
			@NotNull XLineBreakpoint breakpoint,
			@Nullable TypeMirror typeMirror) throws TypeMirrorUnloadedException;
}
