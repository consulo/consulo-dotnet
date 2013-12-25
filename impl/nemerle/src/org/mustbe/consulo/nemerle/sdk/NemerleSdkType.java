/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.nemerle.sdk;

import java.io.File;
import java.util.Arrays;

import javax.swing.Icon;

import org.consulo.lombok.annotations.Logger;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.nemerle.NemerleIcons;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.execution.util.ExecUtil;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;

/**
 * @author VISTALL
 * @since 25.12.13.
 */
@Logger
public class NemerleSdkType extends SdkType
{
	public NemerleSdkType()
	{
		super("NEMERLE_SDK");
	}

	@Nullable
	@Override
	public String suggestHomePath()
	{
		return null;
	}

	@Override
	public boolean isValidSdkHome(String s)
	{
		return new File(s, "ncc.exe").exists();
	}

	@Nullable
	@Override
	public String getVersionString(String s)
	{
		try
		{
			ProcessOutput processOutput = ExecUtil.execAndGetOutput(Arrays.asList(s + "/ncc.exe", "-version"), s);
			//"Nemerle Compiler (ncc) version 1.2.0.183 (git)" +
			//"Copyright (c) University of Wroclaw 2003-2008, Nemerle Project Team 2008-2013 " +
			//"All rights reserved.";

			String firstLine = processOutput.getStderrLines().get(0);
			return firstLine.substring("Nemerle Compiler (ncc) version ".length(), firstLine.length());
		}
		catch(ExecutionException e)
		{
			LOGGER.error(e);
		}
		return "unknown";
	}

	@Override
	public String suggestSdkName(String s, String s2)
	{
		return "nemerle";
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return NemerleIcons.Nemerle;
	}

	@Nullable
	@Override
	public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator)
	{
		return null;
	}

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return type == OrderRootType.CLASSES;
	}

	@NotNull
	@Override
	public String getPresentableName()
	{
		return "Nemerle";
	}

	@Override
	public void saveAdditionalData(SdkAdditionalData sdkAdditionalData, Element element)
	{

	}
}
