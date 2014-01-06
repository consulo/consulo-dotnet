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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.ide.CSharpLookupElementBuilder;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpFieldOrPropertySet;
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpNewExpression;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceAsElement;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceHelper;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.AbstractScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.MemberResolveScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.MemberToTypeValueResolveScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNamespaceDefRuntimeType;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNativeRuntimeType;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpTypeDefRuntimeType;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.util.CSharpResolveUtil;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterListOwner;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceExpression;
import org.mustbe.consulo.dotnet.psi.DotNetReferenceType;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.psi.stub.index.TypeByQNameIndex;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiQualifiedReferenceElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
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
		ATTRIBUTE,
		NATIVE_TYPE_WRAPPER,
		TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD,
		ANY_MEMBER,
		FIELD_OR_PROPERTY,
		THIS,
		LABEL
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
		val kind = kind();
		final String text;
		switch(kind)
		{
			case ATTRIBUTE:
				text = stripSpaces(getReferenceName()) + "Attribute";
				break;
			case NATIVE_TYPE_WRAPPER:
			case THIS:
				text = "";
				break;
			default:
				text = stripSpaces(getReferenceName());
				break;
		}
		val psiElements = collectResults(kind, new Condition<PsiNamedElement>()
		{
			@Override
			public boolean value(PsiNamedElement netNamedElement)
			{
				return Comparing.equal(text, netNamedElement.getName());
			}
		});
		return toResolveResults(psiElements);
	}

	private Collection<? extends PsiElement> collectResults(@NotNull ResolveToKind kind, Condition<PsiNamedElement> condition)
	{
		if(!isValid())
		{
			return Collections.emptyList();
		}

		// dont allow resolving labels in references, when out from goto
		if(kind != ResolveToKind.LABEL)
		{
			condition = Conditions.and(condition, new Condition<PsiNamedElement>()
			{
				@Override
				public boolean value(PsiNamedElement psiNamedElement)
				{
					return !(psiNamedElement instanceof CSharpLabeledStatementImpl);
				}
			});
		}

		AbstractScopeProcessor p = null;
		PsiElement qualifier = getQualifier();
		switch(kind)
		{
			case THIS:
				DotNetTypeDeclaration typeDeclaration = PsiTreeUtil.getParentOfType(this, DotNetTypeDeclaration.class);
				if(typeDeclaration != null)
				{
					return Collections.singletonList(typeDeclaration);
				}
				break;
			case TYPE_PARAMETER_FROM_PARENT:
				DotNetGenericParameterListOwner parameterListOwner = PsiTreeUtil.getParentOfType(this, DotNetGenericParameterListOwner.class);
				if(parameterListOwner == null)
				{
					return Collections.emptyList();
				}

				DotNetGenericParameter[] genericParameters = parameterListOwner.getGenericParameters();
				val list = new ArrayList<PsiElement>(genericParameters.length);
				for(val o : genericParameters)
				{
					if(condition.value(o))
					{
						list.add(o);
					}
				}
				return list;
			case NATIVE_TYPE_WRAPPER:
				PsiElement nativeElement = findChildByType(CSharpTokenSets.NATIVE_TYPES);
				assert nativeElement != null;
				CSharpNativeRuntimeType nativeRuntimeType = CSharpNativeTypeImpl.ELEMENT_TYPE_TO_TYPE.get(nativeElement.getNode().getElementType());
				if(nativeRuntimeType == null)
				{
					return Collections.emptyList();
				}
				Collection<DotNetTypeDeclaration> dotNetTypeDeclarations = TypeByQNameIndex.getInstance().get(nativeRuntimeType
						.getWrapperQualifiedClass(), getProject(), getResolveScope());

				return dotNetTypeDeclarations;
			case FIELD_OR_PROPERTY:
				CSharpNewExpression newExpression = PsiTreeUtil.getParentOfType(this, CSharpNewExpression.class);
				assert newExpression != null;
				DotNetType newType = newExpression.getNewType();
				if(newType == null)
				{
					return Collections.emptyList();
				}
				DotNetRuntimeType runtimeType1 = newType.toRuntimeType();
				PsiElement psiElement1 = runtimeType1.toPsiElement();
				if(psiElement1 == null)
				{
					return Collections.emptyList();
				}
				p = new MemberResolveScopeProcessor(Conditions.and(condition, new Condition<PsiNamedElement>()
				{
					@Override
					public boolean value(PsiNamedElement psiNamedElement)
					{
						return psiNamedElement instanceof CSharpFieldDeclarationImpl || psiNamedElement instanceof CSharpPropertyDeclarationImpl;
					}
				}));
				p.putUserData(CSharpResolveUtil.QUALIFIED, false);
				CSharpResolveUtil.treeWalkUp(p, psiElement1, null);
				return p.getElements();
			case LABEL:
				DotNetQualifiedElement parentOfType = PsiTreeUtil.getParentOfType(this, DotNetQualifiedElement.class);
				assert parentOfType != null;
				p = new MemberResolveScopeProcessor(Conditions.and(condition, new Condition<PsiNamedElement>()
				{
					@Override
					public boolean value(PsiNamedElement psiNamedElement)
					{
						return psiNamedElement instanceof CSharpLabeledStatementImpl;
					}
				}));
				CSharpResolveUtil.treeWalkUp(p, this, parentOfType);
				return p.getElements();
			case NAMESPACE:
				String qName = stripSpaces(getText());
				CSharpNamespaceAsElement aPackage = CSharpNamespaceHelper.getNamespaceElementIfFind(getProject(), qName, getResolveScope());
				if(aPackage == null)
				{
					return Collections.emptyList();
				}
				return Collections.<PsiElement>singletonList(aPackage);
			case NAMESPACE_WITH_CREATE_OPTION:
				return Collections.<PsiElement>singletonList(new CSharpNamespaceAsElement(getProject(), stripSpaces(getText()), getResolveScope()));
			case ATTRIBUTE:
				val resolveResults = processTypeOrGenericParameterOrMethod(qualifier, condition);
				if(resolveResults.size() != 1)
				{
					return resolveResults;
				}
				return resolveResults; //TODO [VISTALL] resolve to constuctor
			case TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD:
				return processTypeOrGenericParameterOrMethod(qualifier, condition);
			case METHOD:
			case ANY_MEMBER:
				Condition<PsiNamedElement> cond = condition;
				if(kind == ResolveToKind.METHOD)
				{
					cond = Conditions.and(cond, new Condition<PsiNamedElement>()
					{
						@Override
						public boolean value(PsiNamedElement psiNamedElement)
						{
							return psiNamedElement instanceof CSharpMethodDeclaration;
						}
					});
				}
				p = new MemberResolveScopeProcessor(cond);

				PsiElement target = this;
				if(qualifier instanceof DotNetExpression)
				{
					DotNetRuntimeType runtimeType = ((DotNetExpression) qualifier).toRuntimeType();
					if(runtimeType == DotNetRuntimeType.ERROR_TYPE)
					{
						return Collections.emptyList();
					}

					PsiElement psiElement = runtimeType.toPsiElement();
					if(psiElement == null)
					{
						return Collections.emptyList();
					}
					target = psiElement;
				}

				p.putUserData(CSharpResolveUtil.QUALIFIED, target != this);
				CSharpResolveUtil.treeWalkUp(p, target, null);
				return p.getElements();
		}
		return Collections.emptyList();
	}

	@NotNull
	private static ResolveResult[] toResolveResults(Collection<? extends PsiElement> elements)
	{
		if(elements.isEmpty())
		{
			return ResolveResult.EMPTY_ARRAY;
		}
		int i = 0;
		ResolveResult[] k = new ResolveResult[elements.size()];
		for(PsiElement element : elements)
		{
			k[i++] = new PsiElementResolveResult(element, element.isValid());
		}
		return k;
	}

	private Collection<PsiElement> processTypeOrGenericParameterOrMethod(PsiElement qualifier, Condition<PsiNamedElement> condition)
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

		if(!target.isValid())
		{
			return Collections.emptyList();
		}

		MemberToTypeValueResolveScopeProcessor p = new MemberToTypeValueResolveScopeProcessor(condition);
		p.putUserData(CSharpResolveUtil.QUALIFIED, target != this);

		CSharpResolveUtil.treeWalkUp(p, target, null);
		return p.getElements();
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

	@NotNull
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
		else if(parent instanceof CSharpUsingNamespaceStatementImpl)
		{
			return ResolveToKind.NAMESPACE;
		}
		else if(parent instanceof CSharpAttributeImpl)
		{
			return ResolveToKind.ATTRIBUTE;
		}
		else if(parent instanceof CSharpFieldOrPropertySet)
		{
			if(((CSharpFieldOrPropertySet) parent).getReferenceExpression() == this)
			{
				return ResolveToKind.FIELD_OR_PROPERTY;
			}
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

			if(PsiTreeUtil.getParentOfType(this, CSharpUsingNamespaceStatementImpl.class) != null)
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
		else if(parent instanceof CSharpGotoStatementImpl)
		{
			return ResolveToKind.LABEL;
		}

		PsiElement nativeElement = findChildByType(CSharpTokenSets.NATIVE_TYPES);
		if(nativeElement != null)
		{
			return ResolveToKind.NATIVE_TYPE_WRAPPER;
		}

		PsiElement childByType = findChildByType(CSharpTokens.THIS_KEYWORD);
		if(childByType != null)
		{
			return ResolveToKind.THIS;
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
		return this;
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
		ResolveToKind kind = kind();
		if(kind == ResolveToKind.NATIVE_TYPE_WRAPPER)
		{
			kind = ResolveToKind.ANY_MEMBER;
		}

		Collection<? extends PsiElement> psiElements = collectResults(kind, new Condition<PsiNamedElement>()
		{
			@Override
			public boolean value(PsiNamedElement psiNamedElement)
			{
				return true;
			}
		});
		return CSharpLookupElementBuilder.getInstance(getProject()).buildToLookupElements(psiElements);
	}

	@Override
	public boolean isSoft()
	{
		return false;
	}

	@NotNull
	@Override
	public DotNetRuntimeType toRuntimeType()
	{
		PsiElement resolve = resolve();
		if(resolve instanceof CSharpNamespaceAsElement)
		{
			return new CSharpNamespaceDefRuntimeType(((CSharpNamespaceAsElement) resolve).getQName(), getProject(), getResolveScope());
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
