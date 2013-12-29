/*
 * Copyright 2013 must-be.org
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
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceAsElement;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceHelper;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.CollectScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.MemberResolveScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.MemberToTypeValueResolveScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNamespaceDefRuntimeType;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpTypeDefRuntimeType;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.util.CSharpResolveUtil;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterListOwner;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceExpression;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceType;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiQualifiedReferenceElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpReferenceExpressionImpl extends CSharpElementImpl implements DotNetReferenceExpression, PsiQualifiedReferenceElement,
		PsiPolyVariantReference
{
	private enum ResolveToKind
	{
		TYPE_PARAMETER_FROM_PARENT,
		NAMESPACE,
		NAMESPACE_WITH_CREATE_OPTION,
		METHOD,
		ATTRIBUTE,
		TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD,
		ANY_MEMBER
	}

	public CSharpReferenceExpressionImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public PsiReference getReference()
	{
		return this;
	}

	@Nullable
	public PsiElement getReferenceElement()
	{
		return findChildByType(CSharpTokens.IDENTIFIER);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitReferenceExpression(this);
	}

	@Nullable
	@Override
	public PsiElement getQualifier()
	{
		return findChildByClass(CSharpReferenceExpressionImpl.class);
	}

	@Nullable
	@Override
	public String getReferenceName()
	{
		PsiElement referenceElement = getReferenceElement();
		return referenceElement == null ? null : referenceElement.getText();
	}

	@Override
	public PsiElement getElement()
	{
		return this;
	}

	@Override
	public TextRange getRangeInElement()
	{
		PsiElement referenceElement = getReferenceElement();
		if(referenceElement == null)
		{
			return TextRange.EMPTY_RANGE;
		}

		PsiElement qualifier = getQualifier();
		int startOffset = qualifier != null ? qualifier.getTextLength() + 1 : 0;
		return new TextRange(startOffset, referenceElement.getTextLength() + startOffset);
	}

	@NotNull
	@Override
	public ResolveResult[] multiResolve(boolean b)
	{
		ResolveToKind kind = kind();
		PsiElement parent = getParent();
		PsiElement qualifier = getQualifier();
		switch(kind)
		{
			case TYPE_PARAMETER_FROM_PARENT:
				DotNetGenericParameterListOwner parameterListOwner = PsiTreeUtil.getParentOfType(this, DotNetGenericParameterListOwner.class);
				if(parameterListOwner == null)
				{
					return ResolveResult.EMPTY_ARRAY;
				}

				for(val o : parameterListOwner.getGenericParameters())
				{
					if(Comparing.equal(o.getName(), getReferenceName()))
					{
						return new ResolveResult[]{new PsiElementResolveResult(o)};
					}
				}
				break;
			case NAMESPACE:
			case NAMESPACE_WITH_CREATE_OPTION:
				String qName = stripSpaces(getText());
				CSharpNamespaceAsElement aPackage = CSharpNamespaceHelper.getNamespaceElement(getProject(), qName, getResolveScope());
				if(!aPackage.isValid())
				{
					return ResolveResult.EMPTY_ARRAY;
				}
				return new ResolveResult[]{new PsiElementResolveResult(aPackage)};
			case ATTRIBUTE:
				ResolveResult[] resolveResults = processTypeOrGenericParameterOrMethod(qualifier, getReferenceName() + "Attribute");
				if(resolveResults.length != 1)
				{
					return resolveResults;
				}
				return resolveResults; //TODO [VISTALL] resolve to constuctor
			case TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD:
				return processTypeOrGenericParameterOrMethod(qualifier, getReferenceName());
			case METHOD:
			case ANY_MEMBER:
				MemberResolveScopeProcessor p = new MemberResolveScopeProcessor(getReferenceName(), kind == ResolveToKind.METHOD);

				PsiElement target = this;
				if(qualifier instanceof DotNetExpression)
				{
					DotNetRuntimeType runtimeType = ((DotNetExpression) qualifier).toRuntimeType();
					if(runtimeType == DotNetRuntimeType.ERROR_TYPE)
					{
						return ResolveResult.EMPTY_ARRAY;
					}

					PsiElement psiElement = runtimeType.toPsiElement();
					if(psiElement == null)
					{
						return ResolveResult.EMPTY_ARRAY;
					}
					target = psiElement;
				}

				p.putUserData(CSharpResolveUtil.QUALIFIED, target != this);
				CSharpResolveUtil.treeWalkUp(p, target, null);
				return p.toResolveResults();
		}
		return ResolveResult.EMPTY_ARRAY;
	}

	private ResolveResult[] processTypeOrGenericParameterOrMethod(PsiElement qualifier, String referenceName)
	{
		PsiElement target = this;
		if(qualifier instanceof CSharpReferenceExpressionImpl)
		{
			PsiElement resolve = ((CSharpReferenceExpressionImpl) qualifier).resolve();
			if(resolve != null)
			{
				target = resolve;
			}
		}

		MemberToTypeValueResolveScopeProcessor p = new MemberToTypeValueResolveScopeProcessor(referenceName);
		p.putUserData(CSharpResolveUtil.QUALIFIED, target != this);

		CSharpResolveUtil.treeWalkUp(p, target, null);
		return p.toResolveResults();
	}

	@Nullable
	@Override
	public PsiElement resolve()
	{
		ResolveResult[] resolveResults = multiResolve(false);
		return resolveResults.length == 0 ? null : resolveResults[0].getElement();
	}

	private String stripSpaces(String text)
	{
		StringBuilder builder = new StringBuilder(text.length());
		char[] chars = text.toCharArray();
		for(char aChar : chars)
		{
			if(!StringUtil.isWhiteSpace(aChar))
			{
				builder.append(aChar);
			}
		}
		return builder.toString();
	}

	private ResolveToKind kind()
	{
		PsiElement parent = getParent();
		if(parent instanceof CSharpGenericConstraintImpl)
		{
			DotNetGenericParameterListOwner parameterListOwner = PsiTreeUtil.getParentOfType(this, DotNetGenericParameterListOwner.class);
			if(parameterListOwner == null)
			{
				return null;
			}

			return ResolveToKind.TYPE_PARAMETER_FROM_PARENT;
		}
		else if(parent instanceof CSharpNamespaceDeclarationImpl)
		{
			return ResolveToKind.NAMESPACE_WITH_CREATE_OPTION;
		}
		else if(parent instanceof DotNetReferenceType)
		{
			return ResolveToKind.TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD;
		}
		else if(parent instanceof CSharpUsingStatementImpl)
		{
			return ResolveToKind.NAMESPACE;
		}
		else if(parent instanceof CSharpAttributeImpl)
		{
			return ResolveToKind.ATTRIBUTE;
		}
		else if(parent instanceof CSharpReferenceExpressionImpl)
		{
			CSharpNamespaceDeclarationImpl netNamespaceDeclaration = PsiTreeUtil.getParentOfType(this, CSharpNamespaceDeclarationImpl.class);
			if(netNamespaceDeclaration != null)
			{
				DotNetReferenceExpression namespaceReference = netNamespaceDeclaration.getNamespaceReference();
				if(namespaceReference != null && PsiTreeUtil.isAncestor(namespaceReference, this, false))
				{
					return ResolveToKind.NAMESPACE_WITH_CREATE_OPTION;
				}
			}

			if(PsiTreeUtil.getParentOfType(this, CSharpAttributeImpl.class) != null)
			{
				return ResolveToKind.NAMESPACE;
			}

			if(PsiTreeUtil.getParentOfType(this, CSharpUsingStatementImpl.class) != null)
			{
				return ResolveToKind.NAMESPACE;
			}

			if(PsiTreeUtil.getParentOfType(this, DotNetReferenceType.class) != null)
			{
				return ResolveToKind.NAMESPACE;
			}
		}
		else if(parent instanceof CSharpMethodCallExpressionImpl)
		{
			return ResolveToKind.METHOD;
		}

		return ResolveToKind.ANY_MEMBER;
	}

	@NotNull
	@Override
	public String getCanonicalText()
	{
		return getText();
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
		PsiElement qualifier = getQualifier();

		PsiElement target = this;
		if(qualifier instanceof DotNetExpression)
		{
			DotNetRuntimeType runtimeType = ((DotNetExpression) qualifier).toRuntimeType();
			if(runtimeType == DotNetRuntimeType.ERROR_TYPE)
			{
				return ArrayUtil.EMPTY_OBJECT_ARRAY;
			}

			PsiElement psiElement = runtimeType.toPsiElement();
			if(psiElement == null)
			{
				return ArrayUtil.EMPTY_OBJECT_ARRAY;
			}
			target = psiElement;
		}

		CollectScopeProcessor p = new CollectScopeProcessor();
		p.putUserData(CSharpResolveUtil.QUALIFIED, target != this);

		CSharpResolveUtil.treeWalkUp(p, target, null);

		return p.getElements().toArray();
	}

	@Override
	public boolean isSoft()
	{
		return false;
	}

	@Override
	public DotNetRuntimeType toRuntimeType()
	{
		PsiElement resolve = resolve();
		if(resolve instanceof CSharpNamespaceAsElement)
		{
			return new CSharpNamespaceDefRuntimeType(((CSharpNamespaceAsElement) resolve).getQName(), getProject(),
					getResolveScope());
		}
		else if(resolve instanceof CSharpTypeDeclarationImpl)
		{
			return new CSharpTypeDefRuntimeType(((CSharpTypeDeclarationImpl) resolve).getPresentableQName(), getProject(), getResolveScope());
		}
		else if(resolve instanceof DotNetVariable)
		{
			return ((DotNetVariable) resolve).toRuntimeType();
		}
		return DotNetRuntimeType.ERROR_TYPE;
	}
}
