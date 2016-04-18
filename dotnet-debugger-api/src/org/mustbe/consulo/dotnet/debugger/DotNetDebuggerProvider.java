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

package org.mustbe.consulo.dotnet.debugger;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.proxy.DotNetStackFrameMirrorProxy;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceExpression;
import com.intellij.lang.Language;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Consumer;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XNamedValue;
import consulo.dotnet.debugger.DotNetDebugContext;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public abstract class DotNetDebuggerProvider
{
	public static final ExtensionPointName<DotNetDebuggerProvider> EP_NAME = ExtensionPointName.create("org.mustbe.consulo.dotnet.core.debugger" + "" +
			".provider");


	@Nullable
	public static DotNetDebuggerProvider getProvider(@Nullable Language language)
	{
		if(language == null)
		{
			return null;
		}
		for(DotNetDebuggerProvider dotNetDebuggerProvider : DotNetDebuggerProvider.EP_NAME.getExtensions())
		{
			if(dotNetDebuggerProvider.getEditorLanguage() == language)
			{
				return dotNetDebuggerProvider;
			}
		}
		return null;
	}

	@NotNull
	public abstract PsiFile createExpressionCodeFragment(@NotNull Project project, @NotNull PsiElement sourcePosition, @NotNull String text, boolean isPhysical);

	public abstract void evaluate(@NotNull DotNetStackFrameMirrorProxy frame,
			@NotNull DotNetDebugContext debuggerContext,
			@NotNull String expression,
			@Nullable PsiElement elementAt,
			@NotNull XDebuggerEvaluator.XEvaluationCallback callback,
			@Nullable XSourcePosition expressionPosition);

	public abstract void evaluate(@NotNull DotNetStackFrameMirrorProxy frame,
			@NotNull DotNetDebugContext debuggerContext,
			@NotNull DotNetReferenceExpression element,
			@NotNull Set<Object> visitedVariables,
			@NotNull Consumer<XNamedValue> callback);

	public abstract boolean isSupported(@NotNull PsiFile psiFile);

	public abstract Language getEditorLanguage();
}
