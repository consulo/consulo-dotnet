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
import org.mustbe.consulo.RequiredReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import mono.debugger.TypeMirror;
import mono.debugger.VirtualMachine;

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

	@RequiredReadAction
	public abstract boolean createRequest(@NotNull Project project,
			@NotNull VirtualMachine virtualMachine,
			@NotNull XLineBreakpoint breakpoint,
			@NotNull TypeMirror typeMirror);
}
