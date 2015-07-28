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

package org.mustbe.consulo.dotnet.resolve;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.ManagingFS;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.IdFilter;

/**
 * @author VISTALL
 * @since 28.07.2015
 */
public class GlobalSearchScopeFilter extends IdFilter
{
	private GlobalSearchScope mySearchScope;

	public GlobalSearchScopeFilter(GlobalSearchScope searchScope)
	{
		mySearchScope = searchScope;
	}

	@Override
	public boolean containsFileId(int id)
	{
		VirtualFile virtualFile = ManagingFS.getInstance().findFileById(id);
		return virtualFile != null && mySearchScope.accept(virtualFile);
	}
}
