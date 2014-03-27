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

package org.mustbe.consulo.visualStudio;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 27.03.14
 */
public class VisualStudioSolutionFileType implements FileType
{
	public static final VisualStudioSolutionFileType INSTANCE = new VisualStudioSolutionFileType();

	@NotNull
	@Override
	public String getName()
	{
		return "VISUAL_STUDIO_SOLUTION";
	}

	@NotNull
	@Override
	public String getDescription()
	{
		return "Visual Studio solution files";
	}

	@NotNull
	@Override
	public String getDefaultExtension()
	{
		return "sln";
	}

	@Nullable
	@Override
	public Icon getIcon()
	{
		return VisualStudioIcons.VisualStudio;
	}

	@Override
	public boolean isBinary()
	{
		return false;
	}

	@Override
	public boolean isReadOnly()
	{
		return true;
	}

	@Nullable
	@Override
	public String getCharset(@NotNull VirtualFile virtualFile, byte[] bytes)
	{
		return "UTF-8";
	}
}
