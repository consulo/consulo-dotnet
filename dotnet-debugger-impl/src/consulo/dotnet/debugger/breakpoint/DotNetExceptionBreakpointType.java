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

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.TreeChooser;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointType;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointCustomPropertiesPanel;
import consulo.annotations.RequiredDispatchThread;
import consulo.dotnet.DotNetTypes;
import consulo.dotnet.debugger.breakpoint.properties.DotNetExceptionBreakpointProperties;
import consulo.dotnet.debugger.breakpoint.ui.DotNetExceptionBreakpointPropertiesPanel;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.dotnet.ui.chooser.DotNetTypeChooserFactory;

/**
 * @author VISTALL
 * @since 29.04.2016
 */
public class DotNetExceptionBreakpointType extends XBreakpointType<XBreakpoint<DotNetExceptionBreakpointProperties>, DotNetExceptionBreakpointProperties>
{
	@NotNull
	public static DotNetExceptionBreakpointType getInstance()
	{
		return EXTENSION_POINT_NAME.findExtension(DotNetExceptionBreakpointType.class);
	}

	public DotNetExceptionBreakpointType()
	{
		super("dotnet-exception-breakpoint", ".NET Exception Breakpoints");
	}

	@NotNull
	@Override
	public Icon getEnabledIcon()
	{
		return AllIcons.Debugger.Db_exception_breakpoint;
	}

	@NotNull
	@Override
	public Icon getDisabledIcon()
	{
		return AllIcons.Debugger.Db_disabled_exception_breakpoint;
	}

	@Nullable
	@Override
	public XBreakpointCustomPropertiesPanel<XBreakpoint<DotNetExceptionBreakpointProperties>> createCustomPropertiesPanel(@NotNull Project project)
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
	public XBreakpoint<DotNetExceptionBreakpointProperties> createDefaultBreakpoint(@NotNull XBreakpointCreator<DotNetExceptionBreakpointProperties> creator)
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
	@RequiredDispatchThread
	public XBreakpoint<DotNetExceptionBreakpointProperties> addBreakpoint(final Project project, JComponent parentComponent)
	{
		TreeChooser<DotNetTypeDeclaration> chooser = DotNetTypeChooserFactory.getInstance(project).createInheriableChooser(DotNetTypes.System.Exception, GlobalSearchScope.projectScope(project));

		chooser.showDialog();
		final DotNetTypeDeclaration selectedType = chooser.getSelected();
		final String qName = selectedType == null ? null : selectedType.getVmQName();

		if(!StringUtil.isEmpty(qName))
		{
			return ApplicationManager.getApplication().runWriteAction(new Computable<XBreakpoint<DotNetExceptionBreakpointProperties>>()
			{
				@Override
				public XBreakpoint<DotNetExceptionBreakpointProperties> compute()
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