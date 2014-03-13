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

package org.mustbe.consulo.csharp.ide.codeInsight.actions;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpInheritUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReferenceExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpUsingNamespaceStatementImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.index.MethodIndex;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.index.TypeIndex;
import org.mustbe.consulo.dotnet.DotNetBundle;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.codeInsight.daemon.impl.ShowAutoImportPass;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInspection.HintAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ArrayListSet;
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

		Set<String> q = collectAvailableNamespaces();
		if(q.isEmpty())
		{
			return PopupResult.NOT_AVAILABLE;
		}

		AddUsingAction action = new AddUsingAction(editor, myRef, q);
		String message = ShowAutoImportPass.getMessage(q.size() != 1, DotNetBundle.message("use.popup", q.iterator().next()));

		HintManager.getInstance().showQuestionHint(editor, message, myRef.getTextOffset(), myRef.getTextRange().getEndOffset(), action);

		return PopupResult.SHOW_HIT;
	}

	private Set<String> collectAvailableNamespaces()
	{
		if(myRef.getQualifier() != null || myRef.getParent() instanceof CSharpUsingNamespaceStatementImpl || !myRef.isValid())
		{
			return Collections.emptySet();
		}

		String referenceName = myRef.getReferenceName();
		if(StringUtil.isEmpty(referenceName))
		{
			return Collections.emptySet();
		}
		assert referenceName != null;

		val list = new ArrayListSet<String>();

		Collection<DotNetTypeDeclaration> tempTypes;
		Collection<DotNetLikeMethodDeclaration> tempMethods;
		val kind = myRef.kind();
		switch(kind)
		{
			case ATTRIBUTE:
				val cond = new Condition<DotNetTypeDeclaration>()
				{
					@Override
					public boolean value(DotNetTypeDeclaration typeDeclaration)
					{
						return CSharpInheritUtil.isParent(DotNetTypes.System_Attribute, typeDeclaration, true);
					}
				};
				// if attribute endwith Attribute - collect only with
				if(referenceName.endsWith(CSharpReferenceExpressionImpl.AttributeSuffix))
				{
					tempTypes = TypeIndex.getInstance().get(referenceName, myRef.getProject(), myRef.getResolveScope());

					collect(list, tempTypes, cond);
				}
				else
				{
					tempTypes = TypeIndex.getInstance().get(referenceName, myRef.getProject(), myRef.getResolveScope());

					collect(list, tempTypes, cond);

					tempTypes = TypeIndex.getInstance().get(referenceName + CSharpReferenceExpressionImpl.AttributeSuffix, myRef.getProject(),
							myRef.getResolveScope());

					collect(list, tempTypes, cond);
				}
				break;
			default:
				tempTypes = TypeIndex.getInstance().get(referenceName, myRef.getProject(), myRef.getResolveScope());

				collect(list, tempTypes, Conditions.<DotNetTypeDeclaration>alwaysTrue());

				tempMethods = MethodIndex.getInstance().get(referenceName, myRef.getProject(), myRef.getResolveScope());

				collect(list, tempMethods, new Condition<DotNetLikeMethodDeclaration>()
				{
					@Override
					public boolean value(DotNetLikeMethodDeclaration method)
					{
						return (method.getParent() instanceof DotNetNamespaceDeclaration || method.getParent() instanceof PsiFile) && method
								instanceof DotNetMethodDeclaration && ((DotNetMethodDeclaration) method).isDelegate();
					}
				});
				break;
		}
		return list;
	}

	private static <T extends DotNetQualifiedElement> void collect(Set<String> namespaces, Collection<T> element, Condition<T> condition)
	{
		for(val type : element)
		{
			String presentableParentQName = type.getPresentableParentQName();
			if(StringUtil.isEmpty(presentableParentQName))
			{
				continue;
			}

			if(!condition.value(type))
			{
				continue;
			}
			namespaces.add(presentableParentQName);
		}
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
