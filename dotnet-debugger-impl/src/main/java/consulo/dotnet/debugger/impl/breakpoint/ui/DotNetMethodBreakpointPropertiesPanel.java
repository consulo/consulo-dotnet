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

package consulo.dotnet.debugger.impl.breakpoint.ui;

import consulo.dotnet.debugger.impl.breakpoint.properties.DotNetMethodBreakpointProperties;
import consulo.execution.debug.breakpoint.XLineBreakpoint;
import consulo.execution.debug.breakpoint.ui.XBreakpointCustomPropertiesPanel;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.awt.IdeBorderFactory;
import consulo.ui.ex.awt.util.DialogUtil;

import jakarta.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author VISTALL
 * @since 03.05.2016
 */
public class DotNetMethodBreakpointPropertiesPanel extends XBreakpointCustomPropertiesPanel<XLineBreakpoint<DotNetMethodBreakpointProperties>>
{
	private JCheckBox myWatchEntryCheckBox;
	private JCheckBox myWatchExitCheckBox;

	@Nonnull
	@Override
	public JComponent getComponent()
	{
		JPanel panel, comboBoxPanel;

		myWatchEntryCheckBox = new JCheckBox("Method entry");
		myWatchExitCheckBox = new JCheckBox("Method exit");
		DialogUtil.registerMnemonic(myWatchEntryCheckBox);
		DialogUtil.registerMnemonic(myWatchExitCheckBox);


		Box watchBox = Box.createVerticalBox();
		panel = new JPanel(new BorderLayout());
		panel.add(myWatchEntryCheckBox, BorderLayout.NORTH);
		watchBox.add(panel);
		panel = new JPanel(new BorderLayout());
		panel.add(myWatchExitCheckBox, BorderLayout.NORTH);
		watchBox.add(panel);

		panel = new JPanel(new BorderLayout());
		comboBoxPanel = new JPanel(new BorderLayout());
		comboBoxPanel.add(watchBox, BorderLayout.CENTER);
		comboBoxPanel.add(Box.createHorizontalStrut(3), BorderLayout.WEST);
		comboBoxPanel.add(Box.createHorizontalStrut(3), BorderLayout.EAST);
		panel.add(comboBoxPanel, BorderLayout.NORTH);
		panel.setBorder(IdeBorderFactory.createTitledBorder("Watch events", true));

		ActionListener listener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JCheckBox toCheck = null;
				if(!myWatchEntryCheckBox.isSelected() && !myWatchExitCheckBox.isSelected())
				{
					Object source = e.getSource();
					if(myWatchEntryCheckBox.equals(source))
					{
						toCheck = myWatchExitCheckBox;
					}
					else if(myWatchExitCheckBox.equals(source))
					{
						toCheck = myWatchEntryCheckBox;
					}
					if(toCheck != null)
					{
						toCheck.setSelected(true);
					}
				}
			}
		};
		myWatchEntryCheckBox.addActionListener(listener);
		myWatchExitCheckBox.addActionListener(listener);

		return panel;
	}

	@RequiredUIAccess
	@Override
	public void loadFrom(@Nonnull XLineBreakpoint<DotNetMethodBreakpointProperties> breakpoint)
	{
		DotNetMethodBreakpointProperties properties = breakpoint.getProperties();
		if(properties == null)
		{
			return;
		}

		myWatchEntryCheckBox.setSelected(properties.METHOD_ENTRY);
		myWatchExitCheckBox.setSelected(properties.METHOD_EXIT);
	}

	@RequiredUIAccess
	@Override
	public void saveTo(@Nonnull XLineBreakpoint<DotNetMethodBreakpointProperties> breakpoint)
	{
		DotNetMethodBreakpointProperties properties = breakpoint.getProperties();
		if(properties == null)
		{
			return;
		}

		properties.METHOD_ENTRY = myWatchEntryCheckBox.isSelected();
		properties.METHOD_EXIT = myWatchExitCheckBox.isSelected();
	}
}
