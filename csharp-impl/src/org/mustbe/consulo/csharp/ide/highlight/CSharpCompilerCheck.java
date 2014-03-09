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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.ide.CSharpErrorBundle;
import org.mustbe.consulo.csharp.lang.psi.CSharpInheritUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpThrowStatementImpl;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 09.03.14
 */
public enum CSharpCompilerCheck
{
	CS0155(HighlightInfoType.ERROR)
			{
				@Override
				public void accept(@NotNull PsiElement element, @NotNull HighlightInfoHolder holder)
				{
					CSharpThrowStatementImpl statement = (CSharpThrowStatementImpl) element;
					DotNetExpression expression = statement.getExpression();
					if(expression == null)
					{
						return;
					}

					DotNetTypeRef dotNetTypeRef = expression.toTypeRef();

					if(!CSharpInheritUtil.isParentOrSelf(DotNetTypes.System_Exception, dotNetTypeRef, statement, true))
					{
						holder.add(create(element));
					}
				}
			};

	private final HighlightInfoType myType;

	CSharpCompilerCheck(HighlightInfoType type)
	{
		myType = type;
	}

	@Nullable
	protected HighlightInfo create(PsiElement element)
	{
		return HighlightInfo.newHighlightInfo(myType).descriptionAndTooltip(CSharpErrorBundle.message(name())).range(element).create();
	}

	public abstract void accept(@NotNull PsiElement element, @NotNull HighlightInfoHolder holder);
}
