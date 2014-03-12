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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNativeTypeRef;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import lombok.val;

/**
 * @author VISTALL
 * @since 12.03.14
 */
public class CSharpOperatorReferenceImpl extends CSharpElementImpl implements PsiReference, CSharpExpressionWithParameters
{
	public CSharpOperatorReferenceImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitOperatorReference(this);
	}

	@Override
	public PsiElement getElement()
	{
		return this;
	}

	@Override
	public TextRange getRangeInElement()
	{
		PsiElement operator = getOperator();
		return new TextRange(0, operator.getTextLength());
	}

	@NotNull
	public PsiElement getOperator()
	{
		return findNotNullChildByFilter(CSharpTokenSets.BINARY_OPERATORS);
	}

	@Nullable
	@Override
	public PsiElement resolve()
	{
		PsiElement parent = getParent();
		if(parent instanceof CSharpBinaryExpressionImpl)
		{
			val typeRefs = getTypeRefs();
			val dotNetPsiFacade = DotNetPsiFacade.getInstance(getProject());

			if(isAny(typeRefs, DotNetTypes.System_String))
			{
				return dotNetPsiFacade.findType(DotNetTypes.System_String, getResolveScope(), -1);
			}

			if(typeRefs.length == 1)
			{
				return null;
			}

			if(isAll(typeRefs, CSharpNativeTypeRef.LONG))
			{
				return dotNetPsiFacade.findType(CSharpNativeTypeRef.LONG.getWrapperQualifiedClass(), getResolveScope(), -1);
			}

			if(isAll(typeRefs, CSharpNativeTypeRef.INT))
			{
				return dotNetPsiFacade.findType(CSharpNativeTypeRef.INT.getWrapperQualifiedClass(), getResolveScope(), -1);
			}
		}
		return null;
	}

	private static boolean isAll(@NotNull DotNetTypeRef[] typeRefs, @NotNull String qName)
	{
		return Comparing.equal(typeRefs[0].getQualifiedText(), qName) && Comparing.equal(typeRefs[1].getQualifiedText(), qName);
	}

	private static boolean isAll(@NotNull DotNetTypeRef[] typeRefs, @NotNull CSharpNativeTypeRef t)
	{
		return Comparing.equal(typeRefs[0].getQualifiedText(), t.getQualifiedText()) && Comparing.equal(typeRefs[1].getQualifiedText(),
				t.getQualifiedText());
	}

	private static boolean isAny(@NotNull DotNetTypeRef[] typeRefs, @NotNull String qName)
	{
		for(DotNetTypeRef typeRef : typeRefs)
		{
			if(Comparing.equal(typeRef.getQualifiedText(), qName))
			{
				return true;
			}
		}
		return false;
	}

	@NotNull
	@Override
	public String getCanonicalText()
	{
		return getOperator().getText();
	}

	@Override
	public PsiElement handleElementRename(String s) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element)
	{
		return resolve() == element;
	}

	@NotNull
	@Override
	public Object[] getVariants()
	{
		return new Object[0];
	}

	@Override
	public boolean isSoft()
	{
		PsiElement parent = getParent();
		if(parent instanceof CSharpBinaryExpressionImpl)
		{
			DotNetTypeRef[] typeRefs = getTypeRefs();
			if(isAny(typeRefs, DotNetTypes.System_String))
			{
				return true;
			}
		}
		return false;
	}

	@NotNull
	public DotNetTypeRef[] getTypeRefs()
	{
		DotNetExpression[] parameterExpressions = getParameterExpressions();
		DotNetTypeRef[] typeRefs = new DotNetTypeRef[parameterExpressions.length];
		for(int i = 0; i < parameterExpressions.length; i++)
		{
			DotNetExpression parameterExpression = parameterExpressions[i];
			typeRefs[i] = parameterExpression.toTypeRef();
		}
		return typeRefs;
	}

	@NotNull
	@Override
	public DotNetExpression[] getParameterExpressions()
	{
		PsiElement parent = getParent();
		if(parent instanceof CSharpBinaryExpressionImpl)
		{
			DotNetExpression leftExpression = ((CSharpBinaryExpressionImpl) parent).getLeftExpression();
			DotNetExpression rightExpression = ((CSharpBinaryExpressionImpl) parent).getRightExpression();
			if(rightExpression == null)
			{
				return new DotNetExpression[]{leftExpression};
			}
			return new DotNetExpression[]{
					leftExpression,
					rightExpression
			};
		}
		return DotNetExpression.EMPTY_ARRAY;
	}
}
