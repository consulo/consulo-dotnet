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
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceAsElement;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceHelper;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.AbstractScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.MemberResolveScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.MethodAcceptorImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNamespaceDefTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNativeTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpTypeDefTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.util.CSharpResolveUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.index.TypeByQNameIndex;
import org.mustbe.consulo.dotnet.psi.*;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.CharFilter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpReferenceExpressionImpl extends CSharpElementImpl implements DotNetReferenceExpression, PsiPolyVariantReference
{
	private static final Condition<PsiNamedElement> ourTypeOrMethodOrGenericCondition = new Condition<PsiNamedElement>()
	{
		@Override
		public boolean value(PsiNamedElement psiNamedElement)
		{
			return psiNamedElement instanceof CSharpTypeDeclaration || psiNamedElement instanceof CSharpGenericParameterImpl ||
					psiNamedElement instanceof CSharpMethodDeclaration;
		}
	};

	private static final Condition<PsiNamedElement> ourMethodCondition = new Condition<PsiNamedElement>()
	{
		@Override
		public boolean value(PsiNamedElement psiNamedElement)
		{
			return psiNamedElement instanceof CSharpMethodDeclaration;
		}
	};


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

	private CachedValue<ResolveResult[]> myValue;

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
		return findChildByClass(DotNetExpression.class);
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
	public ResolveResult[] multiResolve(final boolean incompleteCode)
	{
		if(incompleteCode)
		{
			return multiResolve0(true);
		}

		if(myValue != null)
		{
			return myValue.getValue();
		}

		myValue = CachedValuesManager.getManager(getProject()).createCachedValue(new CachedValueProvider<ResolveResult[]>()
		{
			@Nullable
			@Override
			public Result<ResolveResult[]> compute()
			{
				return Result.create(multiResolve0(false), PsiModificationTracker.MODIFICATION_COUNT);
			}
		}, false);

		return myValue.getValue();
	}

	private ResolveResult[] multiResolve0(boolean incompleteCode)
	{
		val kind = kind();
		final String text;
		switch(kind)
		{
			case ATTRIBUTE:
				String referenceName = getReferenceName();
				if(referenceName == null)
				{
					return ResolveResult.EMPTY_ARRAY;
				}
				text = StringUtil.strip(referenceName, CharFilter.NOT_WHITESPACE_FILTER) + "Attribute";
				break;
			case NATIVE_TYPE_WRAPPER:
			case THIS:
				text = "";
				break;
			default:
				String referenceName2 = getReferenceName();
				if(referenceName2 == null)
				{
					return ResolveResult.EMPTY_ARRAY;
				}
				text = StringUtil.strip(referenceName2, CharFilter.NOT_WHITESPACE_FILTER);
				break;
		}
		val psiElements = collectResults(kind, new Condition<PsiNamedElement>()
		{
			@Override
			public boolean value(PsiNamedElement netNamedElement)
			{
				return Comparing.equal(text, netNamedElement.getName());
			}
		}, incompleteCode);

		Condition<PsiElement> newCond = Conditions.alwaysTrue();
		switch(kind)
		{
			case METHOD:
				newCond = new Condition<PsiElement>()
				{
					@Override
					public boolean value(PsiElement psiNamedElement)
					{
						return psiNamedElement instanceof CSharpMethodDeclaration && MethodAcceptorImpl.isAccepted(CSharpReferenceExpressionImpl
								.this, (CSharpMethodDeclaration) psiNamedElement);
					}
				};
				break;
			case TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD:
				newCond = new Condition<PsiElement>()
				{
					@Override
					public boolean value(PsiElement element)
					{
						if(element instanceof DotNetGenericParameterListOwner)
						{
							DotNetReferenceType referenceType = (DotNetReferenceType) getParent();
							if(referenceType.getParent() instanceof DotNetTypeWrapperWithTypeArguments)
							{
								DotNetType[] arguments = ((DotNetTypeWrapperWithTypeArguments) referenceType.getParent()).getArguments();
								return arguments.length == ((DotNetGenericParameterListOwner) element).getGenericParameters().length;
							}
							else
							{
								return true;
							}
						}
						else
						{
							return true;
						}
					}
				};
				break;
		}

		val list = new ArrayList<ResolveResult>(psiElements.size());

		for(PsiElement resolveResult : psiElements)
		{
			boolean value = newCond.value(resolveResult);

			if(!incompleteCode && value || incompleteCode)
			{
				list.add(new PsiElementResolveResult(resolveResult, value));
			}
		}

		return list.isEmpty() ? ResolveResult.EMPTY_ARRAY : list.toArray(new ResolveResult[list.size()]);
	}

	private Collection<? extends PsiElement> collectResults(@NotNull ResolveToKind kind, Condition<PsiNamedElement> condition,
			final boolean incompleteCode)
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
				CSharpNativeTypeRef nativeRuntimeType = CSharpNativeTypeImpl.ELEMENT_TYPE_TO_TYPE.get(nativeElement.getNode().getElementType());
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
				PsiElement psiElement1 = newType.resolve();
				if(psiElement1 == null)
				{
					return Collections.emptyList();
				}
				ResolveState resolveState = ResolveState.initial();
				resolveState = resolveState.put(CSharpResolveUtil.EXTRACTOR_KEY, newType.getGenericExtractor());

				p = new MemberResolveScopeProcessor(Conditions.and(condition, new Condition<PsiNamedElement>()
				{
					@Override
					public boolean value(PsiNamedElement psiNamedElement)
					{
						return psiNamedElement instanceof CSharpFieldDeclarationImpl || psiNamedElement instanceof CSharpPropertyDeclarationImpl;
					}
				}), incompleteCode);
				CSharpResolveUtil.treeWalkUp(p, psiElement1, this, null, resolveState);
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
				}), incompleteCode);
				CSharpResolveUtil.treeWalkUp(p, this, this, parentOfType);
				return p.getElements();
			case NAMESPACE:
				String qName = StringUtil.strip(getText(), CharFilter.NOT_WHITESPACE_FILTER);
				CSharpNamespaceAsElement aPackage = CSharpNamespaceHelper.getNamespaceElementIfFind(getProject(), qName, getResolveScope());
				if(aPackage == null)
				{
					return Collections.emptyList();
				}
				return Collections.<PsiElement>singletonList(aPackage);
			case NAMESPACE_WITH_CREATE_OPTION:
				String qName2 = StringUtil.strip(getText(), CharFilter.NOT_WHITESPACE_FILTER);
				return Collections.<PsiElement>singletonList(new CSharpNamespaceAsElement(getProject(), qName2, getResolveScope()));
			case ATTRIBUTE:
				condition = Conditions.and(condition, ourTypeOrMethodOrGenericCondition);
				val resolveResults = processAnyMember(qualifier, condition, incompleteCode);
				if(resolveResults.size() != 1)
				{
					return resolveResults;
				}
				return resolveResults; //TODO [VISTALL] resolve to constuctor
			case TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD:
			case METHOD:
			case ANY_MEMBER:
				if(kind == ResolveToKind.METHOD)
				{
					condition = Conditions.and(condition, ourMethodCondition);
				}
				else if(kind == ResolveToKind.TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD)
				{
					condition = Conditions.and(condition, ourTypeOrMethodOrGenericCondition);
				}

				return processAnyMember(qualifier, condition, incompleteCode);
		}
		return Collections.emptyList();
	}

	private Collection<PsiElement> processAnyMember(PsiElement qualifier, Condition<PsiNamedElement> condition, boolean incompleteCode)
	{
		PsiElement target = this;
		DotNetGenericExtractor extractor = DotNetGenericExtractor.EMPTY;

		if(qualifier instanceof DotNetReferenceExpression)
		{
			val pair = resolveInfo(((DotNetReferenceExpression) qualifier).resolve());

			if(pair.getFirst() != null)
			{
				target = pair.getFirst();
				extractor = pair.getSecond();
			}
			else
			{
				return Collections.emptyList();
			}
		}

		if(!target.isValid())
		{
			return Collections.emptyList();
		}

		MemberResolveScopeProcessor p = new MemberResolveScopeProcessor(condition, incompleteCode);

		ResolveState resolveState = ResolveState.initial();
		if(extractor != DotNetRuntimeGenericExtractor.EMPTY)
		{
			resolveState = resolveState.put(CSharpResolveUtil.EXTRACTOR_KEY, extractor);
		}

		CSharpResolveUtil.treeWalkUp(p, target, this, null, resolveState);
		return p.getElements();
	}

	@Nullable
	@Override
	public PsiElement resolve()
	{
		ResolveResult[] resolveResults = multiResolve(false);
		return resolveResults.length == 0 ? null : resolveResults[0].getElement();
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
				return ResolveToKind.ANY_MEMBER;
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
			assert true;
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
		PsiElement resolve = resolve();
		if(element instanceof CSharpNamespaceAsElement && resolve instanceof CSharpNamespaceAsElement)
		{
			return Comparing.equal(((CSharpNamespaceAsElement) resolve).getQName(), ((CSharpNamespaceAsElement) element).getQName());
		}
		return resolve == element;
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
		}, true);
		return CSharpLookupElementBuilder.getInstance(getProject()).buildToLookupElements(psiElements);
	}

	@Override
	public boolean isSoft()
	{
		return false;
	}

	@Nullable
	public static DotNetType getType(PsiElement resolve)
	{
		if(resolve instanceof DotNetVariable)
		{
			return ((DotNetVariable) resolve).getType();
		}
		else if(resolve instanceof DotNetMethodDeclaration)
		{
			return ((DotNetMethodDeclaration) resolve).getReturnType();
		}
		return null;
	}

	public static Pair<PsiElement, DotNetGenericExtractor> resolveInfo(PsiElement element)
	{
		DotNetType type = getType(element);
		if(type != null)
		{
			return Pair.create(type.resolve(), type.getGenericExtractor());
		}
		else
		{
			return Pair.create(element, DotNetGenericExtractor.EMPTY);
		}
	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef()
	{
		PsiElement resolve = resolve();
		if(resolve instanceof CSharpNamespaceAsElement)
		{
			return new CSharpNamespaceDefTypeRef(((CSharpNamespaceAsElement) resolve).getQName(), getProject(), getResolveScope());
		}
		else if(resolve instanceof CSharpTypeDeclarationImpl)
		{
			return new CSharpTypeDefTypeRef(((CSharpTypeDeclarationImpl) resolve).getPresentableQName(), getProject(),
					((CSharpTypeDeclarationImpl) resolve).getGenericParametersCount(), getResolveScope());
		}
		else if(resolve instanceof DotNetVariable)
		{
			return ((DotNetVariable) resolve).toTypeRef();
		}
		return DotNetTypeRef.ERROR_TYPE;
	}
}
