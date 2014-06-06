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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import mono.debugger.StackFrameMirror;

/**
 * @author VISTALL
 * @since 10.04.14
 */
public abstract class DotNetDebuggerProvider
{
	public static final ExtensionPointName<DotNetDebuggerProvider> EP_NAME = ExtensionPointName.create("org.mustbe.consulo.dotnet.core.debugger" +
			".provider");

	@NotNull
	public abstract FileType getSupportedFileType();

	@NotNull
	public abstract PsiFile createExpressionCodeFragment(
			@NotNull Project project, @NotNull PsiElement sourcePosition, @NotNull String text, boolean isPhysical);

	public abstract void evaluate(
			@NotNull StackFrameMirror frame,
			@NotNull DotNetDebugContext debuggerContext,
			@NotNull String expression,
			@Nullable PsiElement elementAt,
			@NotNull XDebuggerEvaluator.XEvaluationCallback callback);

	public boolean isSupported(@NotNull PsiFile psiFile)
	{
		return getSupportedFileType() == psiFile.getFileType();
	}
}
