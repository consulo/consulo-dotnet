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

package org.mustbe.consulo.dotnet.ide.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.Icon;

import org.consulo.lombok.annotations.Logger;
import org.consulo.sdk.SdkUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.msil.MsilFileType;
import com.intellij.ide.util.ChooseElementsDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.PathChooserDialog;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTable;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.vfs.ArchiveFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.openapi.vfs.util.ArchiveVfsUtil;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 27.05.14
 */
@Logger
public class ExportDotNetSdkToZipAction extends AnAction
{
	@Override
	public void actionPerformed(AnActionEvent anActionEvent)
	{
		List<Sdk> sdks = ContainerUtil.filter(SdkTable.getInstance().getAllSdks(), Conditions.alwaysTrue());

		if(sdks.isEmpty())
		{
			Messages.showErrorDialog("No .NET sdks", "Error");
			return;
		}

		final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
		ChooseElementsDialog<Sdk> sdk = new ChooseElementsDialog<Sdk>(project, sdks, "Select", "Select sdk for export")
		{
			@Override
			protected String getItemText(Sdk item)
			{
				return item.getName();
			}

			@Nullable
			@Override
			protected Icon getItemIcon(Sdk item)
			{
				return SdkUtil.getIcon(item);
			}
		};

		List<Sdk> selectedSdks = sdk.showAndGetResult();
		if(selectedSdks.isEmpty())
		{
			return;
		}

		final Sdk selected = selectedSdks.get(0);

		PathChooserDialog pathChooser = FileChooserFactory.getInstance().createPathChooser(FileChooserDescriptorFactory.createSingleFolderDescriptor
				(), project, null);

		pathChooser.choose(project.getBaseDir(), new Consumer<List<VirtualFile>>()
		{
			@Override
			public void consume(List<VirtualFile> virtualFiles)
			{
				val dir = virtualFiles.get(0);

				new Task.ConditionalModal(project, "Exporting: " + selected.getName(), false, PerformInBackgroundOption.DEAF)
				{
					@Override
					public void run(@NotNull final ProgressIndicator progressIndicator)
					{
						progressIndicator.setIndeterminate(true);
						export(selected, dir, progressIndicator);
					}
				}.queue();
			}
		});

	}

	private static void export(final Sdk selected, final VirtualFile dir, ProgressIndicator progressIndicator)
	{
		try
		{
			val buffer = new byte[1024];

			VirtualFile[] files = selected.getRootProvider().getFiles(OrderRootType.BINARIES);
			for(VirtualFile v : files)
			{
				VirtualFile virtualFileForArchive = ArchiveVfsUtil.getVirtualFileForArchive(v);
				if(virtualFileForArchive == null)
				{
					continue;
				}
				val name = virtualFileForArchive.getNameWithoutExtension() + ".zip";

				progressIndicator.setText2("Export to: " + name);

				File file = new File(dir.getPath(), name);
				file.createNewFile();

				val fos = new FileOutputStream(file);

				val zos = new ZipOutputStream(fos);

				VfsUtilCore.visitChildrenRecursively(v, new VirtualFileVisitor()
				{
					@Override
					public boolean visitFile(@NotNull VirtualFile virtualFile)
					{
						try
						{
							if(virtualFile.getFileType() != MsilFileType.INSTANCE)
							{
								return true;
							}
							String path = virtualFile.getPath();

							int i = path.indexOf(ArchiveFileSystem.ARCHIVE_SEPARATOR);
							if(i == -1)
							{
								return true;
							}

							String pathInZip = path.substring(i + 2, path.length());

							ZipEntry zipEntry = new ZipEntry(pathInZip);
							zos.putNextEntry(zipEntry);
							InputStream in = virtualFile.getInputStream();

							int len;
							while((len = in.read(buffer)) > 0)
							{
								zos.write(buffer, 0, len);
							}

							in.close();
							zos.closeEntry();
						}
						catch(IOException e)
						{
							LOGGER.error(e);
						}
						return true;
					}
				});
				zos.close();
			}
		}
		catch(IOException e)
		{
			LOGGER.error(e);
		}
	}
}
