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

package consulo.msil.impl.representation.fileSystem;

import consulo.application.AccessRule;
import consulo.application.ReadAction;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.representation.MsilFileRepresentationProvider;
import consulo.project.Project;
import consulo.project.ProjectLocator;
import consulo.util.io.URLUtil;
import consulo.util.lang.lazy.LazyValue;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.virtualFileSystem.VirtualFilePathWrapper;
import consulo.virtualFileSystem.VirtualFileSystem;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.virtualFileSystem.light.TextLightVirtualFileBase;

import jakarta.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public class MsilFileRepresentationVirtualFile extends TextLightVirtualFileBase implements VirtualFilePathWrapper
{
	private final String myPath;
	private final String myIlFileUrl;
	private final MsilFileRepresentationProvider myMsilFileRepresentationProvider;
	private CharSequence myContent;

	private final Supplier<String> myPresentablePath;

	public MsilFileRepresentationVirtualFile(String name, String path, FileType fileType, VirtualFile ilFile, MsilFileRepresentationProvider msilFileRepresentationProvider)
	{
		super(name, fileType, ilFile.getModificationStamp());
		myPath = path;
		myIlFileUrl = ilFile.getUrl();
		myMsilFileRepresentationProvider = msilFileRepresentationProvider;
		setWritable(false);

		myPresentablePath = LazyValue.notNull(() -> {
			String temp = myPath.substring(myPath.indexOf(URLUtil.ARCHIVE_SEPARATOR) + 2, myPath.length());
			temp = temp.substring(0, temp.indexOf(MsilFileRepresentationVirtualFileSystem.SEPARATOR));
			temp = temp.substring(0, temp.lastIndexOf("."));
			temp = temp + "." + myMsilFileRepresentationProvider.getFileType().getDefaultExtension();
			return temp;
		});
	}

	@Nonnull
	@Override
	public String getPresentablePath()
	{
		return myPresentablePath.get();
	}

	@Nonnull
	@Override
	public String getPath()
	{
		return myPath;
	}

	@Nonnull
	public CharSequence getContent()
	{
		if(myContent == null)
		{
			CharSequence content = buildText();
			myContent = content;
			return content;
		}
		return myContent;
	}

	@Nonnull
	private CharSequence buildText()
	{
		VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(myIlFileUrl);
		if(fileByUrl == null || !fileByUrl.isValid())
		{
			return "";
		}

		Project project = ReadAction.compute(() -> ProjectLocator.getInstance().guessProjectForFile(fileByUrl));

		if(project == null)
		{
			return "";
		}

		PsiFile file = AccessRule.read(() -> PsiManager.getInstance(project).findFile(fileByUrl));

		if(file == null)
		{
			return "";
		}

		return AccessRule.read(() -> myMsilFileRepresentationProvider.buildContent(getName(), (MsilFile) file));
	}

	@Nonnull
	@Override
	public VirtualFileSystem getFileSystem()
	{
		return MsilFileRepresentationVirtualFileSystem.getInstance();
	}
}
