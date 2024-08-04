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

package consulo.dotnet.debugger;

import consulo.annotation.access.RequiredReadAction;
import consulo.document.Document;
import consulo.document.FileDocumentManager;
import consulo.dotnet.debugger.proxy.DotNetTypeProxy;
import consulo.dotnet.psi.DotNetTypeDeclarationUtil;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiWhiteSpace;
import jakarta.annotation.Nonnull;

import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 25.04.14
 */
public class DotNetDebuggerUtil
{
	@Nonnull
	public static String getVmQName(@Nonnull DotNetTypeProxy typeMirror)
	{
		return getVmQName(typeMirror.getFullName());
	}

	@Nonnull
	public static String getVmQName(@Nonnull String fullName)
	{
		// System.List`1[String]
		int i = fullName.indexOf('[');
		if(i != -1)
		{
			fullName = fullName.substring(0, i);
		}

		// change + to / separator
		fullName = fullName.replace('+', DotNetTypeDeclarationUtil.NESTED_SEPARATOR_IN_NAME);
		return fullName;
	}

	@Nullable
	@RequiredReadAction
	public static PsiElement findPsiElement(@Nonnull PsiFile file, final int line)
	{
		final Document doc = FileDocumentManager.getInstance().getDocument(file.getVirtualFile());
		final PsiFile psi = doc == null ? null : PsiDocumentManager.getInstance(file.getProject()).getPsiFile(doc);
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

	@Nullable
	@RequiredReadAction
	public static PsiElement findPsiElement(@Nonnull PsiFile file, final int line, int column)
	{
		final Document doc = FileDocumentManager.getInstance().getDocument(file.getVirtualFile());
		final PsiFile psi = doc == null ? null : PsiDocumentManager.getInstance(file.getProject()).getPsiFile(doc);
		if(psi == null)
		{
			return null;
		}

		int offset = doc.getLineStartOffset(line);
		return psi.findElementAt(offset + column);
	}
}
