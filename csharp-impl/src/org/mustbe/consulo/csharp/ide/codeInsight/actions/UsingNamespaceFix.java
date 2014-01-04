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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReferenceExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingStatementImpl;
import org.mustbe.consulo.dotnet.DotNetBundle;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.dotnet.psi.stub.index.MethodIndex;
import org.mustbe.consulo.dotnet.psi.stub.index.TypeIndex;
import com.intellij.codeInsight.daemon.impl.ShowAutoImportPass;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInspection.HintAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import lombok.val;

/**
 * @author VISTALL
 * @since 30.12.13.
 */
public class UsingNamespaceFix implements HintAction, HighPriorityAction
{
	enum PopupResult
	{
		NOT_AVAILABLE,
		SHOW_HIT,
		SHOW_ACTION
	}

	private final CSharpReferenceExpressionImpl myRef;

	public UsingNamespaceFix(CSharpReferenceExpressionImpl ref)
	{
		myRef = ref;
	}

	public PopupResult doFix(Editor editor)
	{
		PsiElement resolve = myRef.resolve();
		if(resolve != null && resolve.isValid())
		{
			return PopupResult.NOT_AVAILABLE;
		}

		List<String> q = collectAvailableNamespaces();
		if(q.isEmpty())
		{
			return PopupResult.NOT_AVAILABLE;
		}

		AddUsingAction action = new AddUsingAction(editor, myRef, q);
		String message = ShowAutoImportPass.getMessage(q.size() != 1, DotNetBundle.message("use.popup", q.iterator().next()));

		HintManager.getInstance().showQuestionHint(editor, message, myRef.getTextOffset(), myRef.getTextRange().getEndOffset(), action);

		return PopupResult.SHOW_HIT;
	}

	private List<String> collectAvailableNamespaces()
	{
		if(myRef.getQualifier() != null || myRef.getParent() instanceof CSharpUsingStatementImpl || !myRef.isValid())
		{
			return Collections.emptyList();
		}

		val list = new ArrayList<String>();

		String referenceName = myRef.getReferenceName();
		val types = TypeIndex.getInstance().get(referenceName, myRef.getProject(), myRef.getResolveScope());

		for(val type : types)
		{
			String presentableParentQName = type.getPresentableParentQName();
			if(StringUtil.isEmpty(presentableParentQName))
			{
				continue;
			}
			list.add(presentableParentQName);
		}

		val methods = MethodIndex.getInstance().get(referenceName, myRef.getProject(), myRef.getResolveScope());

		for(val method : methods)
		{
			if((method.getParent() instanceof DotNetNamespaceDeclaration || method.getParent() instanceof PsiFile) && method.isDelegate())
			{
				String presentableParentQName = method.getPresentableParentQName();
				if(StringUtil.isEmpty(presentableParentQName))
				{
					continue;
				}
				list.add(presentableParentQName);
			}
		}
		return list;
	}

	@Override
	public boolean showHint(@NotNull Editor editor)
	{
		return doFix(editor) == PopupResult.SHOW_HIT;
	}

	@NotNull
	@Override
	public String getText()
	{
		return DotNetBundle.message("add.using");
	}

	@NotNull
	@Override
	public String getFamilyName()
	{
		return "Import";
	}

	@Override
	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile psiFile)
	{
		return !collectAvailableNamespaces().isEmpty();
	}

	@Override
	public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException
	{

	}

	@Override
	public boolean startInWriteAction()
	{
		return true;
	}
}
