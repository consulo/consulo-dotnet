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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointCustomPropertiesPanel;
import consulo.dotnet.debugger.breakpoint.properties.DotNetExceptionBreakpointProperties;

/**
 * @author VISTALL
 * @since 29.04.2016
 */
public class DotNetExceptionBreakpointPropertiesPanel extends XBreakpointCustomPropertiesPanel<XBreakpoint<DotNetExceptionBreakpointProperties>>
{
	private JCheckBox myNotifyCaughtCheckBox;
	private JCheckBox myNotifyUncaughtCheckBox;

	@NotNull
	@Override
	public JComponent getComponent()
	{
		myNotifyCaughtCheckBox = new JCheckBox("Caught exception");
		myNotifyUncaughtCheckBox = new JCheckBox("Uncaught exception");

		Box notificationsBox = Box.createVerticalBox();
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(myNotifyCaughtCheckBox, BorderLayout.NORTH);
		notificationsBox.add(panel);
		panel = new JPanel(new BorderLayout());
		panel.add(myNotifyUncaughtCheckBox, BorderLayout.NORTH);
		notificationsBox.add(panel);

		panel = new JPanel(new BorderLayout());
		JPanel notifyPanel = new JPanel(new BorderLayout());
		notifyPanel.add(notificationsBox, BorderLayout.CENTER);
		notifyPanel.add(Box.createHorizontalStrut(3), BorderLayout.WEST);
		notifyPanel.add(Box.createHorizontalStrut(3), BorderLayout.EAST);
		panel.add(notifyPanel, BorderLayout.NORTH);
		panel.setBorder(IdeBorderFactory.createTitledBorder("Notifications", true));

		ActionListener listener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(!myNotifyCaughtCheckBox.isSelected() && !myNotifyUncaughtCheckBox.isSelected())
				{
					Object source = e.getSource();
					JCheckBox toCheck = null;
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
						toCheck.setSelected(true);
					}
				}
			}
		};
		myNotifyCaughtCheckBox.addActionListener(listener);
		myNotifyUncaughtCheckBox.addActionListener(listener);
		return panel;
	}

	@Override
	public void loadFrom(@NotNull XBreakpoint<DotNetExceptionBreakpointProperties> breakpoint)
	{
		myNotifyCaughtCheckBox.setSelected(breakpoint.getProperties().NOTIFY_CAUGHT);
		myNotifyUncaughtCheckBox.setSelected(breakpoint.getProperties().NOTIFY_UNCAUGHT);
	}

	@Override
	public void saveTo(@NotNull XBreakpoint<DotNetExceptionBreakpointProperties> breakpoint)
	{
		breakpoint.getProperties().NOTIFY_CAUGHT = myNotifyCaughtCheckBox.isSelected();
		breakpoint.getProperties().NOTIFY_UNCAUGHT = myNotifyUncaughtCheckBox.isSelected();
	}
}