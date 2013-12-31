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

package org.mustbe.consulo.csharp.ide.codeInsight.actions;

import java.util.Collection;
import java.util.List;

import org.mustbe.consulo.csharp.lang.psi.CSharpFileFactory;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReferenceExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingListImpl;
import org.mustbe.consulo.dotnet.DotNetBundle;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.stub.index.TypeByQNameIndex;
import com.intellij.codeInsight.hint.QuestionAction;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.source.tree.LeafPsiElement;

/**
 * @author VISTALL
 * @since 30.12.13.
 */
public class AddUsingAction implements QuestionAction
{
	private final Editor myEditor;
	private final Project myProject;
	private final CSharpReferenceExpressionImpl myRef;
	private final List<String> myElements;

	public AddUsingAction(Editor editor, CSharpReferenceExpressionImpl ref, List<String> q)
	{
		myEditor = editor;
		myRef = ref;
		myProject = ref.getProject();
		myElements = q;
	}

	private PsiElement getElementForBeforeAdd()
	{
		PsiFile containingFile = myRef.getContainingFile();
		for(PsiElement psiElement : containingFile.getChildren())
		{
			if(psiElement instanceof CSharpUsingListImpl)
			{
				return psiElement;
			}
		}

		return containingFile;
	}

	@Override
	public boolean execute()
	{
		PsiDocumentManager.getInstance(myProject).commitAllDocuments();

		BaseListPopupStep<String> step = new BaseListPopupStep<String>(DotNetBundle.message("add.using"), myElements, AllIcons.Nodes.Package)
		{
			@Override
			public PopupStep onChosen(final String selectedValue, boolean finalChoice)
			{
				PsiDocumentManager.getInstance(myProject).commitAllDocuments();

				new WriteCommandAction<Object>(myRef.getProject(), myRef.getContainingFile())
				{
					@Override
					protected void run(Result<Object> objectResult) throws Throwable
					{
						execute0(selectedValue);
					}
				}.execute();
				return FINAL_CHOICE;
			}
		};

		JBPopupFactory.getInstance().createListPopup(step).showInBestPositionFor(myEditor);

		return true;
	}

	private void execute0(String qName)
	{
		PsiElement elementForBeforeAdd = getElementForBeforeAdd();

		if(elementForBeforeAdd instanceof CSharpUsingListImpl)
		{
			((CSharpUsingListImpl) elementForBeforeAdd).addUsing(qName);
		}
		else if(elementForBeforeAdd instanceof PsiFile)
		{
			PsiElement firstChild = elementForBeforeAdd.getFirstChild();

			assert firstChild != null;

			CSharpUsingListImpl usingStatement = CSharpFileFactory.createUsingList(myProject, qName);

			PsiElement psiElement = elementForBeforeAdd.addBefore(usingStatement, firstChild);

			psiElement.getNode().addChild(new LeafPsiElement(TokenType.WHITE_SPACE, "\n"));
		}

		int caretOffset = myEditor.getCaretModel().getOffset();
		RangeMarker caretMarker = myEditor.getDocument().createRangeMarker(caretOffset, caretOffset);
		int colByOffset = myEditor.offsetToLogicalPosition(caretOffset).column;
		int col = myEditor.getCaretModel().getLogicalPosition().column;
		int virtualSpace = col == colByOffset ? 0 : col - colByOffset;
		int line = myEditor.getCaretModel().getLogicalPosition().line;
		LogicalPosition pos = new LogicalPosition(line, 0);
		myEditor.getCaretModel().moveToLogicalPosition(pos);

		bindToRef(qName);

		line = myEditor.getCaretModel().getLogicalPosition().line;
		LogicalPosition pos1 = new LogicalPosition(line, col);
		myEditor.getCaretModel().moveToLogicalPosition(pos1);
		if(caretMarker.isValid())
		{
			LogicalPosition pos2 = myEditor.offsetToLogicalPosition(caretMarker.getStartOffset());
			int newCol = pos2.column + virtualSpace;
			myEditor.getCaretModel().moveToLogicalPosition(new LogicalPosition(pos2.line, newCol));
			myEditor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
		}
	}

	private void bindToRef(String qName)
	{
		Collection<DotNetTypeDeclaration> list = TypeByQNameIndex.getInstance().get(qName + "." + myRef.getReferenceName(), myProject,
				myRef.getResolveScope());

		if(!list.isEmpty())
		{
			myRef.bindToElement(list.iterator().next());
		}
	}
}
