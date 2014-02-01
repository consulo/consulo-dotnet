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

package org.mustbe.consulo.csharp.ide.codeInsight.unsafeCodeInspection;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpFixedStatementImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpModifierListImpl;
import org.mustbe.consulo.csharp.module.CSharpConfigurationProfileEx;
import org.mustbe.consulo.csharp.module.extension.CSharpModuleExtension;
import org.mustbe.consulo.dotnet.module.DotNetModuleUtil;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.dotnet.psi.DotNetModifierListOwner;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @author VISTALL
 * @since 08.01.14
 */
public class UnsafeCodeInspection extends LocalInspectionTool
{
	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly)
	{
		return new CSharpElementVisitor()
		{
			@Override
			public void visitModifierList(CSharpModifierListImpl list)
			{
				PsiElement modifier = list.getModifier(CSharpTokens.UNSAFE_KEYWORD);
				if(modifier == null)
				{
					return;
				}

				CSharpConfigurationProfileEx profileEx = DotNetModuleUtil.getProfileEx(modifier, CSharpConfigurationProfileEx.KEY,
						CSharpModuleExtension.class);

				if(profileEx == null)
				{
					return;
				}

				if(!profileEx.isAllowUnsafeCode())
				{
					holder.registerProblem(modifier, "Unsafe code is not allowed", ProblemHighlightType.GENERIC_ERROR, null,
							LocalQuickFix.EMPTY_ARRAY);
				}
			}

			@Override
			public void visitFixedStatement(CSharpFixedStatementImpl statement)
			{
				DotNetQualifiedElement qualifiedElement = PsiTreeUtil.getParentOfType(statement, DotNetQualifiedElement.class);
				if(!(qualifiedElement instanceof DotNetModifierListOwner))
				{
					return;
				}

				if(!((DotNetModifierListOwner) qualifiedElement).hasModifier(DotNetModifier.UNSAFE))
				{
					PsiElement fixedElement = statement.getFixedElement();

					holder.registerProblem(fixedElement, "Unsafe code is not allowed. Use 'unsafe' modifier", ProblemHighlightType.GENERIC_ERROR,
							null, LocalQuickFix.EMPTY_ARRAY);
				}
			}
		};
	}
}
