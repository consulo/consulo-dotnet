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

package consulo.dotnet.debugger;

import consulo.annotation.access.RequiredReadAction;
import consulo.component.extension.ExtensionPointName;
import consulo.debugger.XSourcePosition;
import consulo.debugger.evaluation.XDebuggerEvaluator;
import consulo.debugger.frame.XNamedValue;
import consulo.document.util.TextRange;
import consulo.dotnet.debugger.proxy.DotNetStackFrameProxy;
import consulo.dotnet.psi.DotNetReferenceExpression;
import consulo.dotnet.psi.DotNetType;
import consulo.language.Language;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiNameIdentifierOwner;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.project.Project;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public abstract class DotNetDebuggerProvider
{
	public static final ExtensionPointName<DotNetDebuggerProvider> EP_NAME = ExtensionPointName.create("consulo.dotnet.debuggerProvider");

	@Nullable
	public static DotNetDebuggerProvider getProvider(@Nullable Language language)
	{
		if(language == null)
		{
			return null;
		}
		for(DotNetDebuggerProvider dotNetDebuggerProvider : DotNetDebuggerProvider.EP_NAME.getExtensionList())
		{
			if(dotNetDebuggerProvider.getEditorLanguage() == language)
			{
				return dotNetDebuggerProvider;
			}
		}
		return null;
	}

	@Nonnull
	public abstract PsiFile createExpressionCodeFragment(@Nonnull Project project, @Nonnull PsiElement sourcePosition, @Nonnull String text, boolean isPhysical);

	public abstract void evaluate(@Nonnull DotNetStackFrameProxy frame,
			@Nonnull DotNetDebugContext debuggerContext,
			@Nonnull String expression,
			@Nullable PsiElement elementAt,
			@Nonnull XDebuggerEvaluator.XEvaluationCallback callback,
			@Nullable XSourcePosition expressionPosition);

	public abstract void evaluate(@Nonnull DotNetStackFrameProxy frame,
			@Nonnull DotNetDebugContext debuggerContext,
			@Nonnull DotNetReferenceExpression element,
			@Nonnull Set<Object> visitedVariables,
			@Nonnull Consumer<XNamedValue> callback);

	@RequiredReadAction
	@Nullable
	public TextRange getExpressionRangeAtOffset(@Nonnull PsiFile psiFile, int offset, boolean sideEffectsAllowed)
	{
		PsiElement elementAt = psiFile.findElementAt(offset);
		if(elementAt == null)
		{
			return null;
		}

		PsiNameIdentifierOwner owner = PsiTreeUtil.getParentOfType(elementAt, PsiNameIdentifierOwner.class);
		if(owner != null)
		{
			PsiElement nameIdentifier = owner.getNameIdentifier();
			TextRange textRange = nameIdentifier == null ? null : nameIdentifier.getTextRange();
			if(textRange != null && textRange.contains(offset))
			{
				return textRange;
			}
		}

		DotNetReferenceExpression referenceExpression = PsiTreeUtil.getParentOfType(elementAt, DotNetReferenceExpression.class);
		if(referenceExpression != null)
		{
			// skip type references
			if(PsiTreeUtil.getParentOfType(referenceExpression, DotNetType.class) != null)
			{
				return null;
			}
			return referenceExpression.getTextRange();
		}
		return null;
	}

	public abstract boolean isSupported(@Nonnull PsiFile psiFile);

	public abstract Language getEditorLanguage();
}
