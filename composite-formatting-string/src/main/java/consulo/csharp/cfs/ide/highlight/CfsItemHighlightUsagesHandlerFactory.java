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

package consulo.csharp.cfs.ide.highlight;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.annotation.access.RequiredReadAction;
import consulo.csharp.cfs.psi.CfsFile;
import consulo.csharp.cfs.psi.CfsItem;
import consulo.dotnet.psi.DotNetCallArgumentList;
import consulo.dotnet.psi.DotNetExpression;
import consulo.codeInsight.TargetElementUtil;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase;
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerFactory;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;

/**
 * @author VISTALL
 * @since 28.03.2015
 */
public class CfsItemHighlightUsagesHandlerFactory implements HighlightUsagesHandlerFactory
{
	@Nullable
	@Override
	@RequiredReadAction
	public HighlightUsagesHandlerBase createHighlightUsagesHandler(Editor editor, PsiFile file)
	{
		int offset = TargetElementUtil.adjustOffset(file, editor.getDocument(), editor.getCaretModel().getOffset());
		PsiElement target = file.findElementAt(offset);
		if(target != null)
		{
			if(target.getParent() instanceof PsiLanguageInjectionHost)
			{
				PsiElement elementInInjected = InjectedLanguageUtil.findElementInInjected((PsiLanguageInjectionHost) target.getParent(), offset);
				CfsItem cfsItem = elementInInjected == null ? null : PsiTreeUtil.getParentOfType(elementInInjected, CfsItem.class);
				if(cfsItem != null)
				{
					return new CfsItemHighlightUsagesFromItemHandler(editor, file, cfsItem);
				}
			}
			else
			{
				PsiElement targetElement = null;

				PsiElement parent = target;
				while(parent != null)
				{
					PsiElement nextParent = parent.getParent();
					if(nextParent instanceof DotNetCallArgumentList)
					{
						targetElement = parent;
						break;
					}

					parent = nextParent;
				}

				if(targetElement == null)
				{
					return null;
				}

				DotNetCallArgumentList callArgumentList = (DotNetCallArgumentList) targetElement.getParent();
				assert callArgumentList != null;

				final Ref<CfsFile> cfsFileRef = Ref.create();
				DotNetExpression[] expressions = callArgumentList.getExpressions();
				int thisIndex = ArrayUtil.indexOf(callArgumentList.getArguments(), targetElement);
				if(thisIndex == -1)
				{
					return null;
				}
				for(DotNetExpression expression : expressions)
				{
					if(expression instanceof PsiLanguageInjectionHost)
					{
						InjectedLanguageUtil.enumerate(expression, new PsiLanguageInjectionHost.InjectedPsiVisitor()
						{
							@Override
							public void visit(@Nonnull PsiFile injectedPsi, @Nonnull List<PsiLanguageInjectionHost.Shred> places)
							{
								if(injectedPsi instanceof CfsFile)
								{
									cfsFileRef.setIfNull((CfsFile) injectedPsi);
								}
							}
						});
					}
				}

				CfsFile cfsFile = cfsFileRef.get();
				if(cfsFile == null)
				{
					return null;
				}

				return new CfsItemHighlightUsagesFromArgumentHandler(editor, file, targetElement, cfsFile, thisIndex - 1);
			}
		}
		return null;
	}

	@RequiredReadAction
	public static void addOccurrence(@Nonnull List<TextRange> ranges, @Nonnull PsiElement element)
	{
		TextRange range = element.getTextRange();
		if(range != null)
		{
			range = InjectedLanguageManager.getInstance(element.getProject()).injectedToHost(element, range);
			ranges.add(range);
		}
	}

}
