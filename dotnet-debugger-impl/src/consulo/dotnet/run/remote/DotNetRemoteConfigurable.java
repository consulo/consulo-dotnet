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

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;

/**
 * @author VISTALL
 * @since 27-Dec-16
 */
public class DotNetRemoteConfigurable<C extends DotNetRemoteConfiguration> extends SettingsEditor<C>
{
	public DotNetRemoteConfigurable(Project project)
	{
	}

	@Override
	protected void resetEditorFrom(C remoteConfiguration)
	{

	}

	@Override
	protected void applyEditorTo(C remoteConfiguration) throws ConfigurationException
	{

	}
}
