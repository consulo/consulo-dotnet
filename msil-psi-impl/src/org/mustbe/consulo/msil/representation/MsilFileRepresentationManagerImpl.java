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

package org.mustbe.consulo.msil.representation;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.msil.lang.psi.MsilFile;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author VISTALL
 * @since 27.05.14
 */
public class MsilFileRepresentationManagerImpl extends MsilFileRepresentationManager
{
	@NotNull
	@Override
	public VirtualFile[] getRepresentFiles(@NotNull MsilFile msilFile)
	{
		MsilFileRepresentationProvider[] extensions = MsilFileRepresentationProvider.EP_NAME.getExtensions();
		List<VirtualFile> list = new ArrayList<VirtualFile>(extensions.length);
		for(MsilFileRepresentationProvider extension : extensions)
		{
			Pair<String, ? extends FileType> representResult = extension.getRepresentResult(msilFile);
			if(representResult != null)
			{
				list.add(new MsilFileRepresentationVirtualFile(representResult));
			}
		}
		return VfsUtil.toVirtualFileArray(list);
	}
}
