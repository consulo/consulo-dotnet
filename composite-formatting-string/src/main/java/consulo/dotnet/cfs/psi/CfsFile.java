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

package consulo.dotnet.cfs.psi;

import consulo.dotnet.cfs.lang.CfsFileType;
import consulo.dotnet.cfs.lang.CfsLanguage;
import consulo.language.file.FileViewProvider;
import consulo.language.impl.psi.PsiFileBase;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 31.08.14
 */
public class CfsFile extends PsiFileBase
{
	public CfsFile(@Nonnull FileViewProvider viewProvider)
	{
		super(viewProvider, CfsLanguage.INSTANCE);
	}

	@Nonnull
	public CfsItem[] getItems()
	{
		return findChildrenByClass(CfsItem.class);
	}

	@Nonnull
	@Override
	public FileType getFileType()
	{
		return CfsFileType.INSTANCE;
	}
}
