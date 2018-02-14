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
import javax.swing.Icon;

import javax.annotation.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointCustomPropertiesPanel;
import consulo.dotnet.debugger.breakpoint.properties.DotNetMethodBreakpointProperties;
import consulo.dotnet.debugger.breakpoint.ui.DotNetMethodBreakpointPropertiesPanel;

/**
 * @author VISTALL
 * @since 03.05.2016
 */
public class DotNetMethodBreakpointType extends XLineBreakpointType<DotNetMethodBreakpointProperties>
{
	@Nonnull
	public static DotNetMethodBreakpointType getInstance()
	{
		return EXTENSION_POINT_NAME.findExtension(DotNetMethodBreakpointType.class);
	}

	private DotNetMethodBreakpointType()
	{
		super("dotnet-method-breakpoint", ".NET Method Breakpoints");
	}

	@Nonnull
	@Override
	public Icon getEnabledIcon()
	{
		return AllIcons.Debugger.Db_method_breakpoint;
	}

	@Nonnull
	@Override
	public Icon getDisabledIcon()
	{
		return AllIcons.Debugger.Db_disabled_method_breakpoint;
	}

	@Nullable
	@Override
	public XBreakpointCustomPropertiesPanel<XLineBreakpoint<DotNetMethodBreakpointProperties>> createCustomPropertiesPanel(@Nonnull Project project)
	{
		return new DotNetMethodBreakpointPropertiesPanel();
	}

	@Nullable
	@Override
	public DotNetMethodBreakpointProperties createBreakpointProperties(@Nonnull VirtualFile file, int line)
	{
		return new DotNetMethodBreakpointProperties();
	}

	@Override
	public boolean canBeHitInOtherPlaces()
	{
		return true;
	}
}
