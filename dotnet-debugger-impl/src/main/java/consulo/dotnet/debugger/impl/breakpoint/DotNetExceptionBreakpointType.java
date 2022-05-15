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

import consulo.application.AllIcons;
import consulo.application.ApplicationManager;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.debugger.impl.breakpoint.properties.DotNetExceptionBreakpointProperties;
import consulo.dotnet.debugger.impl.breakpoint.ui.DotNetExceptionBreakpointPropertiesPanel;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.psi.ui.chooser.DotNetTypeChooserFactory;
import consulo.execution.debug.XDebuggerManager;
import consulo.execution.debug.breakpoint.XBreakpoint;
import consulo.execution.debug.breakpoint.XBreakpointType;
import consulo.execution.debug.breakpoint.ui.XBreakpointCustomPropertiesPanel;
import consulo.language.editor.ui.TreeChooser;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.image.Image;
import consulo.util.lang.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.util.function.Supplier;

/**
 * @author VISTALL
 * @since 29.04.2016
 */
public class DotNetExceptionBreakpointType extends XBreakpointType<XBreakpoint<DotNetExceptionBreakpointProperties>, DotNetExceptionBreakpointProperties>
{
	@Nonnull
	public static DotNetExceptionBreakpointType getInstance()
	{
		return EXTENSION_POINT_NAME.findExtensionOrFail(DotNetExceptionBreakpointType.class);
	}

	public DotNetExceptionBreakpointType()
	{
		super("dotnet-exception-breakpoint", ".NET Exception Breakpoints");
	}

	@Nonnull
	@Override
	public Image getEnabledIcon()
	{
		return AllIcons.Debugger.Db_exception_breakpoint;
	}

	@Nonnull
	@Override
	public Image getDisabledIcon()
	{
		return AllIcons.Debugger.Db_disabled_exception_breakpoint;
	}

	@Nullable
	@Override
	public XBreakpointCustomPropertiesPanel<XBreakpoint<DotNetExceptionBreakpointProperties>> createCustomPropertiesPanel(@Nonnull Project project)
	{
		return new DotNetExceptionBreakpointPropertiesPanel();
	}

	@Override
	public String getDisplayText(XBreakpoint<DotNetExceptionBreakpointProperties> breakpoint)
	{
		String name = breakpoint.getProperties().VM_QNAME;
		if(name != null)
		{
			return name;
		}
		else
		{
			return "Any exception";
		}
	}

	@Nullable
	@Override
	public DotNetExceptionBreakpointProperties createProperties()
	{
		return new DotNetExceptionBreakpointProperties();
	}

	@Nullable
	@Override
	public XBreakpoint<DotNetExceptionBreakpointProperties> createDefaultBreakpoint(@Nonnull XBreakpointCreator<DotNetExceptionBreakpointProperties> creator)
	{
		return creator.createBreakpoint(new DotNetExceptionBreakpointProperties());
	}

	@Override
	public boolean isAddBreakpointButtonVisible()
	{
		return true;
	}

	@Nullable
	@Override
	@RequiredUIAccess
	public XBreakpoint<DotNetExceptionBreakpointProperties> addBreakpoint(final Project project, JComponent parentComponent)
	{
		TreeChooser<DotNetTypeDeclaration> chooser = DotNetTypeChooserFactory.getInstance(project).createInheriableChooser(DotNetTypes.System.Exception, GlobalSearchScope.projectScope(project));

		chooser.showDialog();
		final DotNetTypeDeclaration selectedType = chooser.getSelected();
		final String qName = selectedType == null ? null : selectedType.getVmQName();

		if(!StringUtil.isEmpty(qName))
		{
			return ApplicationManager.getApplication().runWriteAction(new Supplier<XBreakpoint<DotNetExceptionBreakpointProperties>>()
			{
				@Override
				public XBreakpoint<DotNetExceptionBreakpointProperties> get()
				{
					DotNetExceptionBreakpointProperties properties = new DotNetExceptionBreakpointProperties();
					properties.VM_QNAME = qName;
					return XDebuggerManager.getInstance(project).getBreakpointManager().addBreakpoint(DotNetExceptionBreakpointType.this, properties);
				}
			});
		}
		return null;
	}
}