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

package org.mustbe.consulo.csharp.cfs.ide.highlight;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.cfs.psi.CfsItem;
import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @author VISTALL
 * @since 28.03.2015
 */
public class CfsItemHighlightUsagesHandlerFactory implements HighlightUsagesHandlerFactory
{
	@Nullable
	@Override
	public HighlightUsagesHandlerBase createHighlightUsagesHandler(Editor editor, PsiFile file)
	{
		int offset = TargetElementUtilBase.adjustOffset(file, editor.getDocument(), editor.getCaretModel().getOffset());
		PsiElement target = file.findElementAt(offset);
		if(target != null && target.getParent() instanceof PsiLanguageInjectionHost)
		{
			PsiElement elementInInjected = InjectedLanguageUtil.findElementInInjected((PsiLanguageInjectionHost) target.getParent(), offset);
			CfsItem cfsItem = elementInInjected == null ? null : PsiTreeUtil.getParentOfType(elementInInjected, CfsItem.class);
			if(cfsItem != null)
			{
				return new CfsItemHighlightUsagesHandler(editor, file, cfsItem);
			}
		}
		return null;
	}
}
