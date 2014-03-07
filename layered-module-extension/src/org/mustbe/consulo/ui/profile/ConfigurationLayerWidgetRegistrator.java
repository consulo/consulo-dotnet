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

package org.mustbe.consulo.ui.profile;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.module.extension.LayeredModuleExtension;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;

/**
 * @author VISTALL
 * @since 07.03.14
 */
public abstract class ConfigurationLayerWidgetRegistrator extends AbstractProjectComponent
{
	private ConfigurationLayerWidget myConfigurationLayerWidget;

	public ConfigurationLayerWidgetRegistrator(Project project)
	{
		super(project);
	}

	@NotNull
	public abstract String getPrefix();

	@NotNull
	public abstract Class<? extends LayeredModuleExtension> getExtensionClass();

	@Override
	public void projectOpened()
	{
		StatusBar statusBar = WindowManager.getInstance().getIdeFrame(myProject).getStatusBar();

		myConfigurationLayerWidget = new ConfigurationLayerWidget(myProject, getPrefix(), getExtensionClass());

		statusBar.addWidget(myConfigurationLayerWidget, "after Position");
	}

	@Override
	public void projectClosed()
	{
		StatusBar statusBar = WindowManager.getInstance().getIdeFrame(myProject).getStatusBar();

		statusBar.removeWidget(myConfigurationLayerWidget.ID());
	}
}
