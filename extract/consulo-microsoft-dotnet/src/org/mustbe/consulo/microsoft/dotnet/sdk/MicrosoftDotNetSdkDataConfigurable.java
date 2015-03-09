/*
 * Copyright 2013-2015 must-be.org
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

package org.mustbe.consulo.microsoft.dotnet.sdk;

import javax.swing.JComponent;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.components.JBTextField;

/**
 * @author VISTALL
 * @since 09.03.2015
 */
public class MicrosoftDotNetSdkDataConfigurable implements AdditionalDataConfigurable
{
	private final MicrosoftDotNetSdkData mySdkAdditionalData;

	public MicrosoftDotNetSdkDataConfigurable(MicrosoftDotNetSdkData sdkAdditionalData)
	{
		mySdkAdditionalData = sdkAdditionalData;
	}

	@Override
	public void setSdk(Sdk sdk)
	{
	}

	@Nullable
	@Override
	public JComponent createComponent()
	{
		JBTextField compilerPathField = new JBTextField(mySdkAdditionalData.getCompilerPath());
		compilerPathField.setEditable(false);
		return LabeledComponent.left(compilerPathField, "Compiler Path");
	}

	@Override
	public boolean isModified()
	{
		return false;
	}

	@Override
	public void apply() throws ConfigurationException
	{

	}

	@Override
	public void reset()
	{

	}

	@Override
	public void disposeUIResources()
	{

	}
}
