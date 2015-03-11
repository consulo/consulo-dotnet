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

package org.mustbe.consulo.mono.dotnet.sdk;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.sdk.DotNetSdkType;
import org.mustbe.consulo.mono.dotnet.MonoDotNetIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.SdkTable;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import lombok.val;

/**
 * @author VISTALL
 * @since 06.12.13.
 */
public class MonoSdkType extends DotNetSdkType
{
	public static final String LINUX_COMPILER = "/usr/bin/mcs";

	@NotNull
	public String getExecutable(@NotNull Sdk sdk)
	{
		String runFile = null;
		if(SystemInfo.isWindows)
		{
			runFile = sdk.getHomePath() + "/../../../bin/mono.exe";
		}
		else if(SystemInfo.isMac)
		{
			runFile = sdk.getHomePath() + "/../../../bin/mono";
		}
		else if(SystemInfo.isLinux)
		{
			runFile = "/usr/bin/mono";
		}
		else
		{
			throw new UnsupportedOperationException(SystemInfo.OS_NAME);
		}
		return runFile;
	}

	@NotNull
	public static MonoSdkType getInstance()
	{
		return EP_NAME.findExtension(MonoSdkType.class);
	}

	public MonoSdkType()
	{
		super("MONO_DOTNET_SDK");
	}

	@NotNull
	@Override
	public Collection<String> suggestHomePaths()
	{
		String defaultHomePath = getDefaultHomePath();
		if(defaultHomePath == null)
		{
			return Collections.emptyList();
		}
		File dir = new File(defaultHomePath, "lib/mono");
		if(!dir.exists())
		{
			return Collections.emptyList();
		}
		List<String> list = new ArrayList<String>(1);
		for(File file : dir.listFiles())
		{
			list.add(file.getPath());
		}
		return list;
	}

	@Override
	public boolean canCreatePredefinedSdks()
	{
		return true;
	}

	@Nullable
	private String getDefaultHomePath()
	{
		if(SystemInfo.isWindows)
		{
			return "C:/Program Files (x86)/Mono/";
		}
		if(SystemInfo.isMac)
		{
			return "/Library/Frameworks/Mono.framework/Home/";
		}
		if(SystemInfo.isLinux)
		{
			File file = new File(LINUX_COMPILER);
			if(file.exists())
			{
				return "/usr/";
			}
		}
		return null;
	}

	@Override
	public boolean isValidSdkHome(String s)
	{
		return new File(s, "mscorlib.dll").exists();
	}

	@Nullable
	@Override
	public String getVersionString(String s)
	{
		return new File(s).getName();
	}

	@Override
	public String suggestSdkName(String currentSdkName, String sdkHome)
	{
		File file = new File(sdkHome);
		return getPresentableName() + " " + file.getName();
	}

	@NotNull
	@Override
	public String getPresentableName()
	{
		return "Mono .NET";
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return MonoDotNetIcons.Mono;
	}

	@Override
	public boolean supportsCustomCreateUI()
	{
		return true;
	}

	@Override
	public void showCustomCreateUI(SdkModel sdkModel, JComponent parentComponent, final Consumer<Sdk> sdkCreatedCallback)
	{
		File monoLib = null;
		if(SystemInfo.isLinux)
		{
			File file = new File(LINUX_COMPILER);
			if(!file.exists())
			{
				Messages.showErrorDialog(parentComponent, "\'" + LINUX_COMPILER + "\' not found.");
				return;
			}
			monoLib = new File("/usr/lib/mono");
		}
		else if(SystemInfo.isWindows || SystemInfo.isMac)
		{
			FileChooserDescriptor singleFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
			val toSelectPath = getDefaultHomePath();
			val toSelect = toSelectPath == null ? null : LocalFileSystem.getInstance().findFileByPath(toSelectPath);
			VirtualFile monoDir = FileChooser.chooseFile(singleFolderDescriptor, null, toSelect);
			if(monoDir == null)
			{
				return;
			}

			monoLib = new File(monoDir.getPath(), "lib/mono");
		}

		if(monoLib == null)
		{
			Messages.showErrorDialog(parentComponent, "Current OS is not supported: " + SystemInfo.OS_NAME);
			return;
		}

		if(!monoLib.exists())
		{
			Messages.showErrorDialog(parentComponent, "File: " + monoLib.getAbsolutePath() + " is not exists.");
			return;
		}

		val list = new ArrayList<Pair<String, File>>();

		File[] files = monoLib.listFiles();
		if(files != null)
		{
			for(File file : files)
			{
				if(isValidSdkHome(file.getAbsolutePath()))
				{
					list.add(new Pair<String, File>(file.getName(), file));
				}
			}
		}

		DefaultActionGroup actionGroup = new DefaultActionGroup();
		for(val pair : list)
		{
			actionGroup.add(new AnAction(pair.getFirst())
			{
				@Override
				public void actionPerformed(AnActionEvent anActionEvent)
				{
					val path = pair.getSecond();
					val absolutePath = path.getAbsolutePath();

					String uniqueSdkName = SdkConfigurationUtil.createUniqueSdkName(MonoSdkType.this, absolutePath,
							SdkTable.getInstance().getAllSdks());
					SdkImpl sdk = new SdkImpl(uniqueSdkName, MonoSdkType.this);
					sdk.setVersionString(getVersionString(absolutePath));
					sdk.setHomePath(absolutePath);

					sdkCreatedCallback.consume(sdk);
				}
			});
		}

		DataContext dataContext = DataManager.getInstance().getDataContext(parentComponent);

		ListPopup choose = JBPopupFactory.getInstance().createActionGroupPopup("Choose", actionGroup, dataContext,
				JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, false);

		choose.showInCenterOf(parentComponent);
	}
}
