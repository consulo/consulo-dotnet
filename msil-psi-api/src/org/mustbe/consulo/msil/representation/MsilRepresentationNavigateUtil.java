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

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.reference.SoftReference;

/**
 * @author VISTALL
 * @since 03.06.14
 */
public class MsilRepresentationNavigateUtil
{
	public static Key<SoftReference<PsiElement>> MSIL_ELEMENT = Key.create("msil-element");

	public static void navigateToRepresentation(@NotNull final PsiElement msilElement, @NotNull FileType fileType)
	{
		MsilFileRepresentationManager manager = MsilFileRepresentationManager.getInstance(msilElement.getProject());

		VirtualFile virtualFile = msilElement.getContainingFile().getVirtualFile();
		if(virtualFile == null)
		{
			return;
		}

		PsiFile representationFile = manager.getRepresentationFile(fileType, virtualFile);
		if(representationFile == null)
		{
			return;
		}

		navigateToRepresentation(representationFile, msilElement);
	}

	public static void navigateToRepresentation(@NotNull PsiFile file, @NotNull final PsiElement msilElement)
	{
		final Ref<PsiElement> elementRef = new Ref<PsiElement>(null);
		file.accept(new PsiRecursiveElementWalkingVisitor()
		{
			@Override
			protected void elementFinished(PsiElement element)
			{
				SoftReference<PsiElement> ref = element.getUserData(MSIL_ELEMENT);

				PsiElement psiElement = SoftReference.dereference(ref);
				if(psiElement == msilElement)
				{
					elementRef.set(element);
					stopWalking();
				}
			}
		});

		PsiElement element = elementRef.get();
		if(element != null)
		{
			if(element instanceof PsiNameIdentifierOwner)
			{
				PsiElement nameIdentifier = ((PsiNameIdentifierOwner) element).getNameIdentifier();
				if(nameIdentifier instanceof Navigatable)
				{
					((Navigatable) nameIdentifier).navigate(true);
					return;
				}
			}
			((Navigatable) element).navigate(true);
		}
		else
		{
			file.navigate(true);
		}
	}
}
