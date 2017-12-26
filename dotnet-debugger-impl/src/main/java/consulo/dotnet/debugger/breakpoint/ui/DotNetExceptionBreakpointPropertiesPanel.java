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

package consulo.dotnet.debugger.breakpoint.ui;

import org.jetbrains.annotations.NotNull;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointCustomPropertiesPanel;
import consulo.dotnet.debugger.breakpoint.properties.DotNetExceptionBreakpointProperties;
import consulo.ui.CheckBox;
import consulo.ui.Component;
import consulo.ui.LabeledLayout;
import consulo.ui.RequiredUIAccess;
import consulo.ui.ValueComponent;
import consulo.ui.VerticalLayout;

/**
 * @author VISTALL
 * @since 29.04.2016
 */
public class DotNetExceptionBreakpointPropertiesPanel extends XBreakpointCustomPropertiesPanel<XBreakpoint<DotNetExceptionBreakpointProperties>>
{
	private CheckBox myNotifyCaughtCheckBox;
	private CheckBox myNotifyUncaughtCheckBox;

	@NotNull
	@Override
	@RequiredUIAccess
	public Component getUIComponent()
	{
		myNotifyCaughtCheckBox = CheckBox.create("Caught exception");
		myNotifyUncaughtCheckBox = CheckBox.create("Uncaught exception");

		VerticalLayout notificationLayout = VerticalLayout.create();
		notificationLayout.add(myNotifyCaughtCheckBox);
		notificationLayout.add(myNotifyUncaughtCheckBox);

		ValueComponent.ValueListener<Boolean> listener = valueEvent ->
		{
			Component source = valueEvent.getComponent();
			if(!myNotifyCaughtCheckBox.getValue() && !myNotifyUncaughtCheckBox.getValue())
			{
				CheckBox toCheck = null;
				if(myNotifyCaughtCheckBox.equals(source))
				{
					toCheck = myNotifyUncaughtCheckBox;
				}
				else if(myNotifyUncaughtCheckBox.equals(source))
				{
					toCheck = myNotifyCaughtCheckBox;
				}
				if(toCheck != null)
				{
					toCheck.setValue(true);
				}
			}
		};
		myNotifyCaughtCheckBox.addValueListener(listener);
		myNotifyUncaughtCheckBox.addValueListener(listener);
		return LabeledLayout.create("Notifications", notificationLayout);
	}

	@Override
	@RequiredUIAccess
	public void loadFrom(@NotNull XBreakpoint<DotNetExceptionBreakpointProperties> breakpoint)
	{
		myNotifyCaughtCheckBox.setValue(breakpoint.getProperties().NOTIFY_CAUGHT);
		myNotifyUncaughtCheckBox.setValue(breakpoint.getProperties().NOTIFY_UNCAUGHT);
	}

	@Override
	@RequiredUIAccess
	public void saveTo(@NotNull XBreakpoint<DotNetExceptionBreakpointProperties> breakpoint)
	{
		breakpoint.getProperties().NOTIFY_CAUGHT = myNotifyCaughtCheckBox.getValue();
		breakpoint.getProperties().NOTIFY_UNCAUGHT = myNotifyUncaughtCheckBox.getValue();
	}
}