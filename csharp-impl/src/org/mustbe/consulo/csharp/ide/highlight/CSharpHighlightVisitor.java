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

package org.mustbe.consulo.csharp.ide.highlight;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpSoftTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpEventDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpFieldDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpFileImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpGenericConstraintImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpGenericParameterImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpParameterImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpPropertyDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReferenceExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpTypeDeclarationImpl;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpHighlightVisitor extends CSharpElementVisitor implements HighlightVisitor
{
	private HighlightInfoHolder myHighlightInfoHolder;

	@Override
	public boolean suitableForFile(@NotNull PsiFile psiFile)
	{
		return psiFile instanceof CSharpFileImpl;
	}

	@Override
	public void visit(@NotNull PsiElement element)
	{
		element.acceptChildren(this);
	}

	@Override
	public void visitElement(PsiElement element)
	{
		IElementType elementType = element.getNode().getElementType();
		if(CSharpSoftTokens.ALL.contains(elementType))
		{
			myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION).range(element).textAttributes(CSharpHighlightKey
					.SOFT_KEYWORD).create());
		}

		if(element.getNode().getElementType() == CSharpTokens.IDENTIFIER)
		{
			PsiElement parent = element.getParent();

			parent.accept(this);
		}
	}

	@Override
	public void visitGenericParameter(CSharpGenericParameterImpl parameter)
	{
		highlightNamed(parameter, parameter.getNameIdentifier());
	}

	@Override
	public void visitGenericConstraint(CSharpGenericConstraintImpl constraint)
	{
		super.visitGenericConstraint(constraint);

		val genericParameterReference = constraint.getGenericParameterReference();
		if(genericParameterReference == null)
		{
			return;
		}
		val referenceElement = genericParameterReference.getReferenceElement();
		val resolve = genericParameterReference.resolve();

		if(resolve != null)
		{
			assert referenceElement != null;
			highlightNamed(resolve, referenceElement);
		}
		else
		{
			assert referenceElement != null;
			myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.WRONG_REF).range(referenceElement).create());
		}
	}

	@Override
	public void visitTypeDeclaration(CSharpTypeDeclarationImpl declaration)
	{
		super.visitTypeDeclaration(declaration);

		highlightNamed(declaration, declaration.getNameIdentifier());
	}

	@Override
	public void visitFieldDeclaration(CSharpFieldDeclarationImpl declaration)
	{
		super.visitFieldDeclaration(declaration);

		highlightNamed(declaration, declaration.getNameIdentifier());
	}

	@Override
	public void visitParameter(CSharpParameterImpl parameter)
	{
		super.visitParameter(parameter);

		highlightNamed(parameter, parameter.getNameIdentifier());
	}

	@Override
	public void visitPropertyDeclaration(CSharpPropertyDeclarationImpl declaration)
	{
		super.visitPropertyDeclaration(declaration);

		highlightNamed(declaration, declaration.getNameIdentifier());
	}

	@Override
	public void visitEventDeclaration(CSharpEventDeclarationImpl declaration)
	{
		super.visitEventDeclaration(declaration);

		highlightNamed(declaration, declaration.getNameIdentifier());
	}

	@Override
	public void visitReferenceExpression(CSharpReferenceExpressionImpl expression)
	{
		super.visitReferenceExpression(expression);

		PsiElement resolve = expression.resolve();
		if(resolve == null)
		{
			PsiElement referenceElement = expression.getReferenceElement();
			if(referenceElement == null)
			{
				return;
			}
			myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.WRONG_REF).descriptionAndTooltip
					("'" + referenceElement.getText() + "' is not resolved").range(referenceElement).create());
		}
		else
		{
			PsiElement referenceElement = expression.getReferenceElement();
			if(referenceElement == null)
			{
				return;
			}
			highlightNamed(resolve, referenceElement);
		}
	}

	private void highlightNamed(@Nullable PsiElement element, @Nullable PsiElement target)
	{
		if(target == null)
		{
			return;
		}

		TextAttributesKey key = null;
		if(element instanceof CSharpTypeDeclarationImpl)
		{
			key = CSharpHighlightKey.CLASS_NAME;
		}
		else if(element instanceof CSharpGenericParameterImpl)
		{
			key = CSharpHighlightKey.GENERIC_PARAMETER_NAME;
		}
		else if(element instanceof DotNetParameter)
		{
			key = CSharpHighlightKey.PARAMETER;
		}
		else if(element instanceof DotNetMethodDeclaration)
		{
			key = ((DotNetMethodDeclaration) element).hasModifier(CSharpTokens.STATIC_KEYWORD) ? CSharpHighlightKey.STATIC_METHOD : CSharpHighlightKey
					.INSTANCE_METHOD;
		}
		else if(element instanceof DotNetVariable)
		{
			key = ((DotNetVariable) element).hasModifier(CSharpTokens.STATIC_KEYWORD) ? CSharpHighlightKey.STATIC_FIELD : CSharpHighlightKey
					.INSTANCE_FIELD;
		}
		else
		{
			return;
		}

		myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION).range(target).textAttributes(key).create());
	}

	@Override
	public boolean analyze(@NotNull PsiFile psiFile, boolean b, @NotNull HighlightInfoHolder highlightInfoHolder, @NotNull Runnable runnable)
	{
		myHighlightInfoHolder = highlightInfoHolder;
		runnable.run();
		return true;
	}

	@NotNull
	@Override
	public HighlightVisitor clone()
	{
		return new CSharpHighlightVisitor();
	}

	@Override
	public int order()
	{
		return 0;
	}
}
