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

package org.mustbe.consulo.dotnet.debugger;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.compiler.DotNetMacros;
import org.mustbe.consulo.dotnet.module.MainConfigurationLayer;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import lombok.val;

/**
 * @author VISTALL
 * @since 25.04.14
 */
public class DotNetDebuggerUtil
{
	public static File getOutputFile(PsiElement element)
	{
		DotNetModuleExtension extension = ModuleUtilCore.getExtension(element, DotNetModuleExtension.class);
		if(extension == null)
		{
			return null;
		}
		String currentLayerName = extension.getCurrentLayerName();
		MainConfigurationLayer currentLayer = (MainConfigurationLayer) extension.getCurrentLayer();
		val exeFile = DotNetMacros.extract(extension.getModule(), currentLayerName, currentLayer);
		return new File(exeFile);
	}

	@Nullable
	public static PsiElement findPsiElement(@NotNull final Project project, @NotNull final VirtualFile file, final int line)
	{

		final Document doc = FileDocumentManager.getInstance().getDocument(file);
		final PsiFile psi = doc == null ? null : PsiDocumentManager.getInstance(project).getPsiFile(doc);
		if(psi == null)
		{
			return null;
		}

		int offset = doc.getLineStartOffset(line);
		int endOffset = doc.getLineEndOffset(line);
		for(int i = offset + 1; i < endOffset; i++)
		{
			PsiElement el = psi.findElementAt(i);
			if(el != null && !(el instanceof PsiWhiteSpace))
			{
				return el;
			}
		}

		return null;
	}
}
