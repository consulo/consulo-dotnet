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

package consulo.msil.representation.fileSystem;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import consulo.application.AccessRule;
import consulo.msil.lang.psi.MsilFile;
import consulo.msil.representation.MsilFileRepresentationProvider;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public class MsilFileRepresentationVirtualFile extends LightVirtualFile
{
	private final String myPath;
	private final Project myProject;
	private final VirtualFile myIlFile;
	private final MsilFileRepresentationProvider myMsilFileRepresentationProvider;
	private CharSequence myContent;

	public MsilFileRepresentationVirtualFile(String name, String path, FileType fileType, Project project, VirtualFile ilFile, MsilFileRepresentationProvider msilFileRepresentationProvider)
	{
		super(name, fileType, "");
		myPath = path;
		myProject = project;
		myIlFile = ilFile;
		myMsilFileRepresentationProvider = msilFileRepresentationProvider;
		setWritable(false);
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
			myContent = buildText();
		}
		return myContent;
	}

	private CharSequence buildText()
	{
		if(!myIlFile.isValid())
		{
			return "";
		}

		PsiFile file = AccessRule.read(() -> PsiManager.getInstance(myProject).findFile(myIlFile));

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
