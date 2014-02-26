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

package org.mustbe.consulo.microsoft.dotnet.sdk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.sdk.DotNetSdkType;
import org.mustbe.consulo.microsoft.dotnet.MicrosoftDotNetIcons;
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
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkTable;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.util.ArchiveVfsUtil;
import com.intellij.util.Consumer;
import lombok.val;

/**
 * @author VISTALL
 * @since 23.11.13.
 */
public class MicrosoftDotNetSdkType extends DotNetSdkType
{
	@NotNull
	public static MicrosoftDotNetSdkType getInstance()
	{
		return findInstance(MicrosoftDotNetSdkType.class);
	}

	public MicrosoftDotNetSdkType()
	{
		super("MICROSOFT_DOTNET_SDK");
	}

	@NotNull
	@Override
	public String suggestHomePath()
	{
		val windir = System.getenv("windir");

		return windir + "/Microsoft.NET";
	}

	@Override
	public boolean isValidSdkHome(String s)
	{
		return new File(s, "csc.exe").exists();
	}

	@Nullable
	@Override
	public String getVersionString(String s)
	{
		return new File(s).getName();
	}

	@Override
	public String suggestSdkName(String s, String s2)
	{
		File file = new File(s2);
		if(file.getParentFile().getName().equalsIgnoreCase("Framework64"))
		{
			return getPresentableName() + " " + file.getName() + " (64)";
		}
		else
		{
			return getPresentableName() + " " + file.getName();
		}
	}

	@NotNull
	@Override
	public String getPresentableName()
	{
		return "Microsoft .NET";
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return MicrosoftDotNetIcons.DotNet;
	}

	@Override
	public boolean supportsCustomCreateUI()
	{
		return SystemInfo.isWindows;
	}

	@Override
	public void setupSdkPaths(Sdk sdk)
	{
		VirtualFile homeDirectory = sdk.getHomeDirectory();
		assert homeDirectory != null;

		File file = new File(homeDirectory.getPath(), "csc.rsp");
		if(file.exists())
		{
			try
			{
				List<String> lines = FileUtil.loadLines(file);

				List<String> libraries = new ArrayList<String>(lines.size());
				libraries.add("mscorlib.dll");
				for(String line : lines)
				{
					if(line.startsWith("/r:"))
					{
						libraries.add(line.substring(3, line.length()));
					}
				}

				SdkModificator sdkModificator = sdk.getSdkModificator();

				for(String orderDll : libraries)
				{
					VirtualFile dllVirtualFile = homeDirectory.findFileByRelativePath(orderDll);
					if(dllVirtualFile == null)
					{
						continue;
					}

					VirtualFile rootForLocalFile = ArchiveVfsUtil.getArchiveRootForLocalFile(dllVirtualFile);
					if(rootForLocalFile != null)
					{
						sdkModificator.addRoot(rootForLocalFile, OrderRootType.CLASSES);
					}

					String xmlFileUrl = homeDirectory.getUrl() + "/" + orderDll.substring(0, orderDll.length() - 3) + "xml";

					VirtualFile docVirtualFile = VirtualFileManager.getInstance().refreshAndFindFileByUrl(xmlFileUrl);
					if(docVirtualFile != null)
					{
						sdkModificator.addRoot(docVirtualFile, OrderRootType.DOCUMENTATION);
					}
				}
				sdkModificator.commitChanges();
			}
			catch(IOException e)
			{
				super.setupSdkPaths(sdk);
			}
		}
		else
		{
			super.setupSdkPaths(sdk);
		}
	}

	@Override
	public void showCustomCreateUI(SdkModel sdkModel, JComponent parentComponent, final Consumer<Sdk> sdkCreatedCallback)
	{
		FileChooserDescriptor singleFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
		VirtualFile microNetVirtualFile = FileChooser.chooseFile(singleFolderDescriptor, null, LocalFileSystem.getInstance().findFileByIoFile(new
				File(suggestHomePath())));
		if(microNetVirtualFile == null)
		{
			return;
		}

		File microNet = VfsUtil.virtualToIoFile(microNetVirtualFile);

		List<Pair<String, File>> list = getValidSdkDirs(microNet);

		val thisSdks = SdkTable.getInstance().getSdksOfType(this);
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

					String uniqueSdkName = SdkConfigurationUtil.createUniqueSdkName(MicrosoftDotNetSdkType.this, absolutePath, thisSdks);
					SdkImpl sdk = new SdkImpl(uniqueSdkName, MicrosoftDotNetSdkType.this);
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

	public List<Pair<String, File>> getValidSdkDirs(File microNet)
	{
		List<Pair<String, File>> list = new ArrayList<Pair<String, File>>();

		File framework = new File(microNet, "Framework");
		if(framework.exists())
		{
			File[] files = framework.listFiles();
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
		}

		framework = new File(microNet, "Framework64");
		if(framework.exists())
		{
			File[] files = framework.listFiles();
			if(files != null)
			{
				for(File file : files)
				{
					if(isValidSdkHome(file.getAbsolutePath()))
					{
						list.add(new Pair<String, File>(file.getName() + "(x64)", file));
					}
				}
			}
		}
		return list;
	}
}
