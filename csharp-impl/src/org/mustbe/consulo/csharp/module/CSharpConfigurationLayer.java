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

package org.mustbe.consulo.csharp.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.module.extension.ConfigurationLayer;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.components.JBCheckBox;
import lombok.val;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public class CSharpConfigurationLayer implements ConfigurationLayer
{
	private boolean myAllowUnsafeCode;

	@Override
	public void loadState(Element element)
	{
		myAllowUnsafeCode = Boolean.valueOf(element.getAttributeValue("unsafe_code", "false"));
	}

	@Override
	public void getState(Element element)
	{
		element.setAttribute("unsafe_code", Boolean.toString(myAllowUnsafeCode));
	}

	@Nullable
	@Override
	public JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable)
	{
		val panel = new JPanel(new VerticalFlowLayout());
		val comp = new JBCheckBox("Allow unsafe code?", myAllowUnsafeCode);
		comp.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				myAllowUnsafeCode = comp.isSelected();
			}
		});
		panel.add(comp);
		return panel;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof CSharpConfigurationLayer)
		{
			return myAllowUnsafeCode == ((CSharpConfigurationLayer) obj).isAllowUnsafeCode();
		}
		return false;
	}

	@NotNull
	@Override
	public ConfigurationLayer clone()
	{
		CSharpConfigurationLayer profileEx = new CSharpConfigurationLayer();
		profileEx.setAllowUnsafeCode(myAllowUnsafeCode);
		return profileEx;
	}

	public boolean isAllowUnsafeCode()
	{
		return myAllowUnsafeCode;
	}

	public void setAllowUnsafeCode(boolean allowUnsafeCode)
	{
		myAllowUnsafeCode = allowUnsafeCode;
	}
}
