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

package org.mustbe.consulo.nemerle.lang.psi;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.nemerle.lang.NemerleFileType;
import org.mustbe.consulo.nemerle.lang.NemerleLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;

/**
 * @author VISTALL
 * @since 25.12.13.
 */
public class NemerleFile extends PsiFileBase
{
	public NemerleFile(@NotNull FileViewProvider viewProvider)
	{
		super(viewProvider, NemerleLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public FileType getFileType()
	{
		return NemerleFileType.INSTANCE;
	}
}
