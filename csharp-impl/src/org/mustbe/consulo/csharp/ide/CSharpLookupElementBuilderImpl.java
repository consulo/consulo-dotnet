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
import org.mustbe.consulo.csharp.lang.psi.CSharpRecursiveElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpArrayTypeImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpNativeTypeImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpPointerTypeImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReferenceTypeImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpTypeWrapperWithTypeArgumentsImpl;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceExpression;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
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
import lombok.val;

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
			DotNetParameter[] parameterTypes = methodDeclaration.getParameters();

			String parameterText = "(" + StringUtil.join(parameterTypes, new Function<DotNetParameter, String>()
			{
				@Override
				public String fun(DotNetParameter parameter)
				{
					return toStringType(parameter.getType());
				}
			}, ", ") + ")";

			LookupElementBuilder builder = LookupElementBuilder.create(methodDeclaration);
			builder = builder.withIcon(IconDescriptorUpdaters.getIcon(element, Iconable.ICON_FLAG_VISIBILITY));

			builder = builder.withTypeText(toStringType(methodDeclaration.getReturnType()));
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
		else if(element instanceof DotNetVariable)
		{
			DotNetVariable typeDeclaration = (DotNetVariable) element;
			LookupElementBuilder builder = LookupElementBuilder.create(typeDeclaration);

			builder = builder.withIcon(IconDescriptorUpdaters.getIcon(element, Iconable.ICON_FLAG_VISIBILITY));

			builder = builder.withTypeText(toStringType(((DotNetVariable) element).getType()));

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

	private static String toStringType(DotNetType type)
	{
		if(type == null)
		{
			return "<null>";
		}

		val builder = new StringBuilder();
		type.accept(new CSharpRecursiveElementVisitor()
		{
			@Override
			public void visitReferenceType(CSharpReferenceTypeImpl type)
			{
				DotNetReferenceExpression referenceExpression = type.getReferenceExpression();
				if(referenceExpression == null)
				{
					builder.append("<null>");
				}
				else
				{
					builder.append(referenceExpression.getReferenceName());
				}
			}

			@Override
			public void visitTypeWrapperWithTypeArguments(CSharpTypeWrapperWithTypeArgumentsImpl typeArguments)
			{
				super.visitTypeWrapperWithTypeArguments(typeArguments);

				DotNetType[] arguments = typeArguments.getArguments();
				if(arguments.length > 0)
				{
					builder.append("<");
					for(int i = 0; i < arguments.length; i++)
					{
						DotNetType argument = arguments[i];
						if(i != 0)
						{
							builder.append(", ");
						}
						builder.append(toStringType(argument));
					}
					builder.append(">");
				}
			}

			@Override
			public void visitNativeType(CSharpNativeTypeImpl type)
			{
				builder.append(type.getText());
			}

			@Override
			public void visitPointerType(CSharpPointerTypeImpl type)
			{
				super.visitPointerType(type);
				builder.append("*");
			}

			@Override
			public void visitArrayType(CSharpArrayTypeImpl type)
			{
				super.visitArrayType(type);
				builder.append("[]");
			}
		});
		return builder.toString();
	}
}
