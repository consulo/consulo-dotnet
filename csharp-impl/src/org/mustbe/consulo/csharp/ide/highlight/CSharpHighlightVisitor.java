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

package org.mustbe.consulo.csharp.ide.highlight;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpInheritUtil;
import org.mustbe.consulo.csharp.lang.psi.CSharpSoftTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.*;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.codeInsight.daemon.impl.quickfix.QuickFixActionRegistrarImpl;
import com.intellij.codeInsight.quickfix.UnresolvedReferenceQuickFixProvider;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
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
		element.accept(this);
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
	public void visitMacroBody(CSharpMacroBodyImpl block)
	{
		myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION).range(block).textAttributes(CodeInsightColors
				.NOT_USED_ELEMENT_ATTRIBUTES).create());
	}

	@Override
	public void visitMacroBlockStart(CSharpMacroBlockStartImpl start)
	{
		PsiElement startElement = start.getStartElement();
		if(startElement != null && startElement.getNode().getElementType() == CSharpTokens.MACRO_REGION_KEYWORD)
		{
			myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION).range(start).textAttributes(CSharpHighlightKey
					.LINE_COMMENT).create());
		}
	}

	@Override
	public void visitMacroBlockStop(CSharpMacroBlockStopImpl stop)
	{
		IElementType startElementType = stop.findStopElementType();
		if(startElementType == CSharpTokens.MACRO_ENDREGION_KEYWORD)
		{
			myHighlightInfoHolder.add(HighlightInfo.newHighlightInfo(HighlightInfoType.INFORMATION).range(stop).textAttributes(CSharpHighlightKey
					.LINE_COMMENT).create());
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
	public void visitEnumConstantDeclaration(CSharpEnumConstantDeclarationImpl declaration)
	{
		super.visitEnumConstantDeclaration(declaration);

		highlightNamed(declaration, declaration.getNameIdentifier());
	}

	@Override
	public void visitParameter(DotNetParameter parameter)
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
		PsiElement referenceElement = expression.getReferenceElement();
		if(referenceElement == null)
		{
			return;
		}

		ResolveResult[] r = expression.multiResolve(true);
		List<PsiElement> validResults = new ArrayList<PsiElement>();
		List<PsiElement> invalidResults = new ArrayList<PsiElement>();

		for(ResolveResult resolveResult : r)
		{
			if(resolveResult.isValidResult())
			{
				validResults.add(resolveResult.getElement());
			}
			else
			{
				invalidResults.add(resolveResult.getElement());
			}
		}

		if(validResults.size() > 0)
		{
			highlightNamed(validResults.get(0), referenceElement);
		}
		else
		{
			if(invalidResults.isEmpty())
			{
				HighlightInfo info = HighlightInfo.newHighlightInfo(HighlightInfoType.WRONG_REF).descriptionAndTooltip("'" + referenceElement
						.getText() + "' is not resolved").range(referenceElement).create();

				myHighlightInfoHolder.add(info);

				if(expression.getQualifier() == null)
				{
					UnresolvedReferenceQuickFixProvider.registerReferenceFixes(expression, new QuickFixActionRegistrarImpl(info));
				}
			}
			else
			{
				HighlightInfo info = HighlightInfo.newHighlightInfo(HighlightInfoType.ERROR).descriptionAndTooltip("'" + referenceElement.getText()
						+ "' .....").range(referenceElement).create();

				myHighlightInfoHolder.add(info);
			}
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
			if(CSharpInheritUtil.isParent(DotNetTypes.System_Attribute, (DotNetTypeDeclaration) element, true))
			{
				key = CSharpHighlightKey.ATTRIBUTE_NAME;
			}
			else
			{
				key = CSharpHighlightKey.CLASS_NAME;
			}
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
			key = ((DotNetMethodDeclaration) element).hasModifier(DotNetModifier.STATIC) ? CSharpHighlightKey.STATIC_METHOD : CSharpHighlightKey
					.INSTANCE_METHOD;
		}
		else if(element instanceof CSharpLocalVariableImpl)
		{
			return;
		}
		else if(element instanceof DotNetVariable)
		{
			key = ((DotNetVariable) element).hasModifier(DotNetModifier.STATIC) ? CSharpHighlightKey.STATIC_FIELD : CSharpHighlightKey
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
