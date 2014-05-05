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
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.MethodAcceptorImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNativeTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpOperatorHelper;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
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
		Object o = resolve0();
		if(o instanceof PsiElement)
		{
			return (PsiElement) o;
		}
		return null;
	}

	private Object resolve0()
	{
		PsiElement parent = getParent();
		if(parent instanceof CSharpBinaryExpressionImpl)
		{
			//TODO [search in methods]

			DotNetTypeRef returnTypeInStubs = findReturnTypeInStubs();
			if(returnTypeInStubs != null)
			{
				return returnTypeInStubs;
			}
		}
		return null;
	}

	@NotNull
	public DotNetTypeRef resolveToTypeRef()
	{
		Object o = resolve0();
		if(o instanceof DotNetTypeRef)
		{
			return (DotNetTypeRef) o;
		}
		else if(o instanceof PsiElement)
		{
			return CSharpReferenceExpressionImpl.toTypeRef((PsiElement) o);
		}
		return DotNetTypeRef.ERROR_TYPE;
	}

	private DotNetTypeRef findReturnTypeInStubs()
	{
		IElementType elementType = getOperator().getNode().getElementType();
		if(elementType == CSharpTokenSets.OROR || elementType == CSharpTokens.ANDAND)
		{
			return CSharpNativeTypeRef.BOOL;
		}

		CSharpOperatorHelper operatorHelper = CSharpOperatorHelper.getInstance(getProject());

		for(DotNetNamedElement dotNetNamedElement : operatorHelper.getStubMembers())
		{
			if(!isAccepted(this, dotNetNamedElement))
			{
				continue;
			}

			if(MethodAcceptorImpl.isAccepted(this, (CSharpMethodDeclaration) dotNetNamedElement))
			{
				return ((CSharpMethodDeclaration) dotNetNamedElement).getReturnTypeRef();
			}
		}
		return null;
	}

	private static boolean isAccepted(CSharpOperatorReferenceImpl reference, PsiElement element)
	{
		if(!(element instanceof CSharpMethodDeclaration))
		{
			return false;
		}
		val methodDeclaration = (CSharpMethodDeclaration) element;
		if(!methodDeclaration.isOperator())
		{
			return false;
		}

		IElementType elementType = reference.getOperator().getNode().getElementType();
		PsiElement parent = reference.getParent();
		if(parent instanceof CSharpBinaryExpressionImpl)
		{
			if(methodDeclaration.getParameters().length != 2)
			{
				return false;
			}

			if(methodDeclaration.getOperatorElementType() == elementType)
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

			return findReturnTypeInStubs() != null;
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
			typeRefs[i] = parameterExpression.toTypeRef(true);
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
