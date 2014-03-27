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
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.ide.codeInsight.actions.ConvertToNormalCallFix;
import org.mustbe.consulo.csharp.ide.highlight.check.CompilerCheck;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpEventDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpPropertyDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpSoftTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpArrayAccessExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpEnumConstantDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpFileImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpGenericConstraintImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpGenericParameterImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpOperatorReferenceImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReferenceExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpTypeDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpTypeDefStatementImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.util.CSharpMethodImplUtil;
import org.mustbe.consulo.dotnet.psi.DotNetElement;
import org.mustbe.consulo.dotnet.psi.DotNetFieldDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.codeInsight.daemon.impl.quickfix.QuickFixAction;
import com.intellij.codeInsight.daemon.impl.quickfix.QuickFixActionRegistrarImpl;
import com.intellij.codeInsight.quickfix.UnresolvedReferenceQuickFixProvider;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ReferenceRange;
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

		if(element instanceof DotNetElement)
		{
			for(Map.Entry<CompilerCheck<PsiElement>, Class<?>> compilerCheckClassEntry : CSharpCompilerChecks.ourValues.entrySet())
			{
				if(compilerCheckClassEntry.getValue().isAssignableFrom(element.getClass()))
				{
					CompilerCheck<PsiElement> key = compilerCheckClassEntry.getKey();
					key.add(element, myHighlightInfoHolder);
				}
			}
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
	public void visitFieldDeclaration(DotNetFieldDeclaration declaration)
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
	public void visitTypeDefStatement(CSharpTypeDefStatementImpl statement)
	{
		super.visitTypeDefStatement(statement);

		highlightNamed(statement, statement.getNameIdentifier());
	}

	@Override
	public void visitParameter(DotNetParameter parameter)
	{
		super.visitParameter(parameter);

		highlightNamed(parameter, parameter.getNameIdentifier());
	}

	@Override
	public void visitPropertyDeclaration(CSharpPropertyDeclaration declaration)
	{
		super.visitPropertyDeclaration(declaration);

		highlightNamed(declaration, declaration.getNameIdentifier());
	}

	@Override
	public void visitEventDeclaration(CSharpEventDeclaration declaration)
	{
		super.visitEventDeclaration(declaration);

		highlightNamed(declaration, declaration.getNameIdentifier());
	}

	@Override
	public void visitOperatorReference(CSharpOperatorReferenceImpl referenceExpression)
	{
		super.visitOperatorReference(referenceExpression);

		PsiElement resolve = referenceExpression.resolve();

		if(resolve == null && !referenceExpression.isSoft())
		{
			PsiElement element = referenceExpression.getElement();

			HighlightInfo info = HighlightInfo.newHighlightInfo(HighlightInfoType.WRONG_REF).descriptionAndTooltip("Operator is not resolved").range
					(element).create();

			myHighlightInfoHolder.add(info);
		}
	}

	@Override
	public void visitArrayAccessExpression(CSharpArrayAccessExpressionImpl expression)
	{
		super.visitArrayAccessExpression(expression);

		PsiElement resolve = expression.resolve();
		if(resolve == null)
		{
			List<TextRange> absoluteRanges = ReferenceRange.getAbsoluteRanges(expression);

			for(TextRange textRange : absoluteRanges)
			{
				HighlightInfo info = HighlightInfo.newHighlightInfo(HighlightInfoType.WRONG_REF).descriptionAndTooltip("Array method is not " +
						"resolved").range(textRange).create();

				myHighlightInfoHolder.add(info);
			}
		}
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
			PsiElement element = validResults.get(0);
			HighlightInfo highlightInfo = highlightNamed(element, referenceElement);

			if(highlightInfo != null && CSharpMethodImplUtil.isExtensionWrapper(element))
			{
				QuickFixAction.registerQuickFixAction(highlightInfo, ConvertToNormalCallFix.INSTANCE);
			}
		}
		else
		{
			if(invalidResults.isEmpty())
			{
				HighlightInfo info = HighlightInfo.newHighlightInfo(HighlightInfoType.WRONG_REF).descriptionAndTooltip("'" + referenceElement
						.getText() + "' is not resolved").range(referenceElement).create();

				myHighlightInfoHolder.add(info);

				UnresolvedReferenceQuickFixProvider.registerReferenceFixes(expression, new QuickFixActionRegistrarImpl(info));
			}
			else
			{
				HighlightInfo info = HighlightInfo.newHighlightInfo(HighlightInfoType.ERROR).descriptionAndTooltip("'" + referenceElement.getText()
						+ "' .....").range(referenceElement).create();

				myHighlightInfoHolder.add(info);
			}
		}
	}

	@Nullable
	public HighlightInfo highlightNamed(@Nullable PsiElement element, @Nullable PsiElement target)
	{
		return CSharpHighlightUtil.highlightNamed(myHighlightInfoHolder, element, target);
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
