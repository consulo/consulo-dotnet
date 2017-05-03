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

package consulo.dotnet.run.remote;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import consulo.ui.ComboBox;
import consulo.ui.ComboBoxes;
import consulo.ui.Component;
import consulo.ui.Components;
import consulo.ui.LabeledComponents;
import consulo.ui.Layouts;
import consulo.ui.RequiredUIAccess;
import consulo.ui.TextField;
import consulo.ui.VerticalLayout;

/**
 * @author VISTALL
 * @since 27-Dec-16
 */
public class DotNetRemoteConfigurable<C extends DotNetRemoteConfiguration> extends SettingsEditor<C>
{
	private final Project myProject;

	private TextField myHostField;
	private TextField myPortField;
	private ComboBox<Module> myModuleComboBox;
	private ComboBox<Boolean> myModeBox;

	public DotNetRemoteConfigurable(Project project)
	{
		myProject = project;
	}

	@Nullable
	@Override
	@RequiredUIAccess
	protected Component createUIComponent()
	{
		VerticalLayout vertical = Layouts.vertical();
		vertical.add(LabeledComponents.leftFilled("Host", myHostField = Components.textField()));
		vertical.add(LabeledComponents.leftFilled("Port", myPortField = Components.textField()));
		vertical.add(LabeledComponents.leftFilled("Module", myModuleComboBox = Components.comboBox(ModuleManager.getInstance(myProject).getSortedModules())));

		ComboBoxes.SimpleBuilder<Boolean> modeBuilder = ComboBoxes.simple();
		modeBuilder.add(Boolean.TRUE, "attach");
		modeBuilder.add(Boolean.FALSE, "listen");
		myModeBox = modeBuilder.build();

		vertical.add(LabeledComponents.left("Mode", myModeBox));

		myModuleComboBox.setRender((listItemPresentation, i, module) -> {
			if(module == null)
			{
				listItemPresentation.append("<none>");
			}
			else
			{
				listItemPresentation.append(module.getName());
			}
		});
		return vertical;
	}

	@Override
	@RequiredUIAccess
	protected void resetEditorFrom(C remoteConfiguration)
	{
		myHostField.setValue(remoteConfiguration.HOST);
		myPortField.setValue(String.valueOf(remoteConfiguration.PORT));
		Module module = remoteConfiguration.getConfigurationModule().getModule();
		myModuleComboBox.setValue(module != null ? module : null);
		myModeBox.setValue(remoteConfiguration.SERVER_MODE);
	}

	@Override
	@RequiredUIAccess
	protected void applyEditorTo(C remoteConfiguration) throws ConfigurationException
	{
		remoteConfiguration.HOST = myHostField.getValue();
		try
		{
			remoteConfiguration.PORT = Integer.parseInt(myPortField.getValue());
		}
		catch(NumberFormatException e)
		{
			//
		}
		remoteConfiguration.SERVER_MODE = myModeBox.getValue();
		remoteConfiguration.getConfigurationModule().setModule(myModuleComboBox.getValue());
	}
}
