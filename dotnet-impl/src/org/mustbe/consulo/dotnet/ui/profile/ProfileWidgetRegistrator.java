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

package org.mustbe.consulo.dotnet.ui.profile;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;

/**
 * @author VISTALL
 * @since 31.01.14
 */
public class ProfileWidgetRegistrator extends AbstractProjectComponent
{
	private ProfileWidget myProfileWidget;

	public ProfileWidgetRegistrator(Project project)
	{
		super(project);
	}

	@Override
	public void projectOpened()
	{
		StatusBar statusBar = WindowManager.getInstance().getIdeFrame(myProject).getStatusBar();

		myProfileWidget = new ProfileWidget(myProject);

		statusBar.addWidget(myProfileWidget, "after Position");
	}

	@Override
	public void projectClosed()
	{
		StatusBar statusBar = WindowManager.getInstance().getIdeFrame(myProject).getStatusBar();

		statusBar.removeWidget(myProfileWidget.ID());
	}
}
