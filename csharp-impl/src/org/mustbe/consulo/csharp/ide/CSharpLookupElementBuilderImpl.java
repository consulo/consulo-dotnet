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

package org.mustbe.consulo.csharp.ide;

import java.util.Collection;

import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.codeInsight.completion.CompletionData;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.IconDescriptorUpdaters;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpLookupElementBuilderImpl extends CSharpLookupElementBuilder
{
	@Override
	public LookupElement[] buildToLookupElements(PsiElement[] arguments)
	{
		if(arguments.length == 0)
		{
			return LookupElement.EMPTY_ARRAY;
		}
		LookupElement[] array = new LookupElement[arguments.length];
		for(int i = 0; i < arguments.length; i++)
		{
			PsiElement argument = arguments[i];
			array[i] = buildLookupElement(argument);
		}
		return array;
	}

	@Override
	public LookupElement[] buildToLookupElements(Collection<? extends PsiElement> arguments)
	{
		if(arguments.isEmpty())
		{
			return LookupElement.EMPTY_ARRAY;
		}
		int i = 0;
		LookupElement[] array = new LookupElement[arguments.size()];
		for(PsiElement argument : arguments)
		{
			array[i++] = buildLookupElement(argument);
		}
		return array;
	}

	private LookupElement buildLookupElement(PsiElement element)
	{
		if(element instanceof CSharpMethodDeclaration)
		{
			CSharpMethodDeclaration methodDeclaration = (CSharpMethodDeclaration) element;
			DotNetRuntimeType[] parameterTypes = methodDeclaration.getParameterTypesForRuntime();

			DotNetRuntimeType returnTypeForRuntime = methodDeclaration.getReturnTypeForRuntime();

			String parameterText = "(" + StringUtil.join(parameterTypes, new Function<DotNetRuntimeType, String>()
			{
				@Override
				public String fun(DotNetRuntimeType dotNetRuntimeType)
				{
					return dotNetRuntimeType.getPresentableText();
				}
			}, ", ") + ")";

			LookupElementBuilder builder = LookupElementBuilder.create(methodDeclaration);
			builder = builder.withIcon(IconDescriptorUpdaters.getIcon(element, Iconable.ICON_FLAG_VISIBILITY));

			builder = builder.withTypeText(returnTypeForRuntime.getPresentableText());
			builder = builder.withTailText(parameterText, false);
			builder = builder.withInsertHandler(new InsertHandler<LookupElement>()
			{
				@Override
				public void handleInsert(InsertionContext insertionContext, LookupElement lookupElement)
				{
					int offset = insertionContext.getEditor().getCaretModel().getOffset();

					PsiElement elementAt = insertionContext.getFile().findElementAt(offset);
					// dont insert () if it inside method call
					if(elementAt == null || elementAt.getNode().getElementType() != CSharpTokens.LPAR)
					{
						insertionContext.getDocument().insertString(offset, "()");
						insertionContext.getEditor().getCaretModel().moveToOffset(offset + 1);
					}
				}
			});
			return builder;
		}
		else if(element instanceof CSharpTypeDeclaration)
		{
			CSharpTypeDeclaration typeDeclaration = (CSharpTypeDeclaration) element;
			LookupElementBuilder builder = LookupElementBuilder.create(typeDeclaration);

			builder = builder.withIcon(IconDescriptorUpdaters.getIcon(element, Iconable.ICON_FLAG_VISIBILITY));

			builder = builder.withTypeText(typeDeclaration.getPresentableParentQName());

			builder = builder.withTailText(CSharpElementPresentationUtil.formatGenericParameters(typeDeclaration), true);

			return builder;
		}
		else
		{
			return CompletionData.objectToLookupItem(element);
		}
	}
}
