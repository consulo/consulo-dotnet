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
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.CollectScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.TypeDefResolveScopeProcessor;
import org.mustbe.consulo.dotnet.packageSupport.DotNetPackageDescriptor;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterListOwner;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceExpression;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import org.mustbe.consulo.packageSupport.Package;
import org.mustbe.consulo.packageSupport.PackageManager;
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
import com.intellij.psi.scope.util.PsiScopesUtilCore;
import com.intellij.psi.util.PsiTreeUtil;
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
		CLASS,
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
		switch(kind)
		{
			case TYPE_PARAMETER_FROM_PARENT:
				DotNetGenericParameterListOwner parameterListOwner = PsiTreeUtil.getParentOfType(this, DotNetGenericParameterListOwner.class);
				if(parameterListOwner == null)
				{
					return null;
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
				Package aPackage = PackageManager.getInstance(getProject()).findPackage(qName, getResolveScope(), DotNetPackageDescriptor.INSTANCE);
				return new ResolveResult[]{new PsiElementResolveResult(aPackage)};
			case CLASS:
				PsiElement qualifier = getQualifier();
				if(qualifier instanceof CSharpReferenceExpressionImpl)
				{
					PsiElement resolve = ((CSharpReferenceExpressionImpl) qualifier).resolve();
					if(resolve instanceof Package)
					{
						TypeDefResolveScopeProcessor p = new TypeDefResolveScopeProcessor(getReferenceName());
						p.putUserData(Package.SEARCH_SCOPE_KEY, getResolveScope());

						PsiScopesUtilCore.treeWalkUp(p, resolve, null);

						return p.toResolveResults();
					}
					else
					{
						return ResolveResult.EMPTY_ARRAY;
					}
				}

				assert qualifier == null;

				break;
			case METHOD:
				break;
			case ANY_MEMBER:

				break;
		}
		return ResolveResult.EMPTY_ARRAY;
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
		else if(parent instanceof DotNetNamespaceDeclaration)
		{
			return ResolveToKind.NAMESPACE_WITH_CREATE_OPTION;
		}
		else if(parent instanceof DotNetReferenceType)
		{
			return ResolveToKind.CLASS;
		}
		else if(parent instanceof CSharpUsingStatementImpl)
		{
			return ResolveToKind.NAMESPACE;
		}
		else if(parent instanceof CSharpReferenceExpressionImpl)
		{
			if(PsiTreeUtil.getParentOfType(this, DotNetNamespaceDeclaration.class) != null)
			{
				return ResolveToKind.NAMESPACE_WITH_CREATE_OPTION;
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
		String qualifiedName = null;

		PsiElement resolve = null;
		if(qualifier instanceof CSharpReferenceExpressionImpl)
		{
			resolve = ((CSharpReferenceExpressionImpl) qualifier).resolve();
		}

		if(resolve instanceof DotNetTypeDeclaration)
		{
			qualifiedName = ((DotNetTypeDeclaration) resolve).getQName();
		}
		else if(resolve instanceof Package)
		{
			qualifiedName = ((Package) resolve).getName();
		}
		CollectScopeProcessor p = new CollectScopeProcessor(qualifiedName);
		p.putUserData(Package.SEARCH_SCOPE_KEY, getResolveScope());

		PsiScopesUtilCore.treeWalkUp(p, resolve == null ? this : resolve, null);

		return p.getElements().toArray();
	}

	@Override
	public boolean isSoft()
	{
		return false;
	}

	@Override
	public DotNetRuntimeType resolveType()
	{
		return DotNetRuntimeType.ERROR_TYPE;
	}
}