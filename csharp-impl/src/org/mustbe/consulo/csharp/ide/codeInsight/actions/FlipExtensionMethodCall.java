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

import org.jetbrains.annotations.NotNull;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;

/**
 * @author VISTALL
 * @since 26.03.14
 */
public class FlipExtensionMethodCall extends PsiElementBaseIntentionAction
{
	public static FlipExtensionMethodCall INSTANCE = new FlipExtensionMethodCall();

	public FlipExtensionMethodCall()
	{
		setText("Flip extension call");
	}

	@Override
	public void invoke(
			@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException
	{

	}

	@Override
	public boolean isAvailable(
			@NotNull Project project, Editor editor, @NotNull PsiElement element)
	{
		return true;
	}

	@NotNull
	@Override
	public String getFamilyName()
	{
		return "C#";
	}
}
