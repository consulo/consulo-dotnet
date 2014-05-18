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

package org.mustbe.consulo.csharp.ide.highlight.check.impl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.ide.CSharpErrorBundle;
import org.mustbe.consulo.csharp.ide.highlight.check.AbstractCompilerCheck;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpTypeUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpAssignmentExpressionImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpOperatorReferenceImpl;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Couple;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 15.05.14
 */
public class CS0029 extends AbstractCompilerCheck<PsiElement>
{
	@Override
	public boolean accept(@NotNull PsiElement element)
	{
		Couple<DotNetTypeRef> resolve = resolve(element);
		if(resolve == null)
		{
			return false;
		}
		if(resolve.getFirst() == DotNetTypeRef.AUTO_TYPE)
		{
			return false;
		}
		return !CSharpTypeUtil.isInheritable(resolve.getSecond(), resolve.getFirst(), element);
	}

	@Override
	public void checkImpl(@NotNull PsiElement element, @NotNull CompilerCheckResult checkResult)
	{
		Couple<DotNetTypeRef> resolve = resolve(element);
		if(resolve == null)
		{
			return;
		}
		String message = CSharpErrorBundle.message(myId, resolve.getSecond().getQualifiedText(), resolve.getFirst().getQualifiedText());
		if(ApplicationManager.getApplication().isInternal())
		{
			message = myId + ": " + message;
		}
		checkResult.setText(message);
		if(element instanceof CSharpAssignmentExpressionImpl)
		{
			checkResult.setTextRange(((CSharpAssignmentExpressionImpl) element).getExpressions()[1].getTextRange());
		}
		else if(element instanceof DotNetVariable)
		{
			checkResult.setTextRange(((DotNetVariable) element).getInitializer().getTextRange());
		}
	}

	private Couple<DotNetTypeRef> resolve(PsiElement element)
	{
		if(element instanceof DotNetVariable)
		{
			DotNetExpression initializer = ((DotNetVariable) element).getInitializer();
			if(initializer == null)
			{
				return null;
			}
			return Couple.newOne(((DotNetVariable) element).toTypeRef(false), initializer.toTypeRef(false));
		}
		else if(element instanceof CSharpAssignmentExpressionImpl)
		{
			CSharpOperatorReferenceImpl operatorElement = ((CSharpAssignmentExpressionImpl) element).getOperatorElement();
			if(operatorElement.getOperator().getNode().getElementType() != CSharpTokens.EQ)
			{
				return null;
			}
			DotNetExpression[] expressions = ((CSharpAssignmentExpressionImpl) element).getExpressions();
			if(expressions.length != 2)
			{
				return null;
			}
			return Couple.newOne(expressions[0].toTypeRef(false), expressions[1].toTypeRef(false));
		}
		return null;
	}
}
