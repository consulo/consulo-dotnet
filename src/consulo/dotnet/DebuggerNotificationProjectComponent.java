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

package consulo.dotnet;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;

/**
 * @author VISTALL
 * @since 29.04.2016
 */
public class DebuggerNotificationProjectComponent extends AbstractProjectComponent
{
	private static final NotificationGroup ourGroup = new NotificationGroup("dotnet-debugger", NotificationDisplayType.BALLOON, true);

	public DebuggerNotificationProjectComponent(Project project)
	{
		super(project);
	}

	@Override
	public void projectOpened()
	{
		String text = "Plugin .NET debugging API was greatly changed. <br> It required action for implementing another .NET debuggers. <br>" +
				"What why you can found some new bugs in already exists functional.<br> Please report it <a href=\"https://github.com/consulo/consulo-dotnet/issues\">here</a>.<br>" +
				"Introduced initial implementation for <b>Microsoft .NET debugger</b> and <b>Exception Breakpoints</b>. <br> I apologize for the inconvenience, VISTALL";
		ourGroup.createNotification(".NET Debugging API", text, NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER).notify(myProject);
	}
}
