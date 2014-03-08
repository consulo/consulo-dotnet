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

import org.consulo.lombok.annotations.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.ide.CSharpLookupElementBuilder;
import org.mustbe.consulo.csharp.lang.psi.*;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceAsElement;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceHelper;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpVisibilityUtil;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.AbstractScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.MemberResolveScopeProcessor;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.MethodAcceptorImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpGenericParameterTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNamespaceDefTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpNativeTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type.CSharpTypeDefTypeRef;
import org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.util.CSharpResolveUtil;
import org.mustbe.consulo.dotnet.DotNetTypes;
import org.mustbe.consulo.dotnet.psi.*;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.CharFilter;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiQualifiedReference;
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
@Logger
public class CSharpReferenceExpressionImpl extends CSharpElementImpl implements DotNetReferenceExpression, PsiPolyVariantReference
{
	private static final Condition<PsiNamedElement> ourTypeOrMethodOrGenericCondition = new Condition<PsiNamedElement>()
	{
		@Override
		public boolean value(PsiNamedElement psiNamedElement)
		{
			return psiNamedElement instanceof CSharpTypeDeclaration || psiNamedElement instanceof CSharpGenericParameterImpl ||
					psiNamedElement instanceof CSharpMethodDeclaration || psiNamedElement instanceof CSharpTypeDefStatementImpl;
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


	public static enum ResolveToKind
	{
		TYPE_PARAMETER_FROM_PARENT,
		NAMESPACE,
		NAMESPACE_WITH_CREATE_OPTION,
		METHOD,
		ATTRIBUTE,
		NATIVE_TYPE_WRAPPER,
		ARRAY_METHOD,
		TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD,
		ANY_MEMBER,
		FIELD_OR_PROPERTY,
		THIS,
		BASE,
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
				return Result.create(multiResolve0(true), PsiModificationTracker.MODIFICATION_COUNT);
			}
		}, false);

		return myValue.getValue();
	}

	private ResolveResult[] multiResolve0(boolean named)
	{
		ResolveToKind kind = kind();

		CSharpExpressionWithParameters p = null;
		PsiElement parent = getParent();
		if(parent instanceof CSharpExpressionWithParameters)
		{
			p = (CSharpExpressionWithParameters) parent;
		}
		return multiResolve0(named, kind, p, this);
	}

	public static <T extends PsiQualifiedReference & PsiElement> ResolveResult[] multiResolve0(boolean named,
			final ResolveToKind kind,
			final CSharpExpressionWithParameters parameters,
			final T e)
	{
		Condition<PsiNamedElement> namedElementCondition;
		switch(kind)
		{
			case ATTRIBUTE:
				val referenceName = e.getReferenceName();
				if(referenceName == null)
				{
					return ResolveResult.EMPTY_ARRAY;
				}
				namedElementCondition = new Condition<PsiNamedElement>()
				{
					@Override
					public boolean value(PsiNamedElement netNamedElement)
					{
						String candinateName = netNamedElement.getName();
						if(candinateName == null)
						{
							return false;
						}
						if(Comparing.equal(referenceName, candinateName))
						{
							return true;
						}

						if(candinateName.endsWith("Attribute") && Comparing.equal(referenceName + "Attribute", netNamedElement.getName()))
						{
							return true;
						}
						return false;
					}
				};
				break;
			case NATIVE_TYPE_WRAPPER:
			case THIS:
			case BASE:
			case ARRAY_METHOD:
				namedElementCondition = Conditions.alwaysTrue();
				break;
			default:
				val referenceName2 = e.getReferenceName();
				if(referenceName2 == null)
				{
					return ResolveResult.EMPTY_ARRAY;
				}
				val text2 = StringUtil.strip(referenceName2, CharFilter.NOT_WHITESPACE_FILTER);
				namedElementCondition = new Condition<PsiNamedElement>()
				{
					@Override
					public boolean value(PsiNamedElement netNamedElement)
					{
						return Comparing.equal(text2, netNamedElement.getName());
					}
				};
				break;
		}

		Condition<PsiNamedElement> newCond = null;
		switch(kind)
		{
			case METHOD:
				newCond = new Condition<PsiNamedElement>()
				{
					@Override
					public boolean value(PsiNamedElement psiNamedElement)
					{
						return psiNamedElement instanceof CSharpMethodDeclaration && MethodAcceptorImpl.isAccepted(parameters,
								(CSharpMethodDeclaration) psiNamedElement);
					}
				};

				break;
			case ARRAY_METHOD:
				newCond = new Condition<PsiNamedElement>()
				{
					@Override
					public boolean value(PsiNamedElement psiNamedElement)
					{
						return psiNamedElement instanceof DotNetArrayMethodDeclaration && MethodAcceptorImpl.isAccepted(parameters,
								(DotNetArrayMethodDeclaration) psiNamedElement);
					}
				};
				break;
			case TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD:
				newCond = new Condition<PsiNamedElement>()
				{
					@Override
					public boolean value(PsiNamedElement element)
					{
						if(element instanceof DotNetGenericParameterListOwner)
						{
							DotNetReferenceType referenceType = (DotNetReferenceType) e.getParent();
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

		if(newCond != null)
		{
			namedElementCondition = Conditions.and(namedElementCondition, newCond);
		}

		Collection<? extends PsiElement>  psiElements = collectResults(kind, namedElementCondition, e, named);

		val list = new ArrayList<ResolveResult>(psiElements.size());

		for(PsiElement resolveResult : psiElements)
		{
			list.add(new PsiElementResolveResult(resolveResult, true));
		}

		return list.isEmpty() ? ResolveResult.EMPTY_ARRAY : list.toArray(new ResolveResult[list.size()]);
	}

	private static <T extends PsiQualifiedReference & PsiElement> Collection<? extends PsiElement> collectResults(@NotNull ResolveToKind kind,
			Condition<PsiNamedElement> condition,
			T element,
			final boolean named)
	{
		if(!element.isValid())
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
		PsiElement qualifier = element.getQualifier();
		switch(kind)
		{
			case THIS:
				DotNetTypeDeclaration typeDeclaration = PsiTreeUtil.getParentOfType(element, DotNetTypeDeclaration.class);
				if(typeDeclaration != null)
				{
					return Collections.singletonList(typeDeclaration);
				}
				break;
			case BASE:
				DotNetTypeRef baseDotNetTypeRef = ((CSharpReferenceExpressionImpl)element).resolveBaseTypeRef();
				PsiElement baseElement = baseDotNetTypeRef.resolve(element);
				if(baseElement != null)
				{
					return Collections.singletonList(baseElement);
				}
				break;
			case TYPE_PARAMETER_FROM_PARENT:
				DotNetGenericParameterListOwner parameterListOwner = PsiTreeUtil.getParentOfType(element, DotNetGenericParameterListOwner.class);
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
				PsiElement nativeElement = ((CSharpReferenceExpressionImpl)element).findChildByType(CSharpTokenSets.NATIVE_TYPES);
				assert nativeElement != null;
				CSharpNativeTypeRef nativeRuntimeType = CSharpNativeTypeImpl.ELEMENT_TYPE_TO_TYPE.get(nativeElement.getNode().getElementType());
				if(nativeRuntimeType == null)
				{
					return Collections.emptyList();
				}
				PsiElement resolve = nativeRuntimeType.resolve(element);
				if(resolve == null)
				{
					return Collections.emptyList();
				}

				return Collections.singletonList(resolve);
			case FIELD_OR_PROPERTY:
				CSharpNewExpression newExpression = PsiTreeUtil.getParentOfType(element, CSharpNewExpression.class);
				assert newExpression != null;
				DotNetTypeRef dotNetTypeRef = newExpression.toTypeRef();
				if(dotNetTypeRef == DotNetTypeRef.ERROR_TYPE)
				{
					return Collections.emptyList();
				}
				PsiElement psiElement1 = dotNetTypeRef.resolve(element);
				if(psiElement1 == null)
				{
					return Collections.emptyList();
				}
				ResolveState resolveState = ResolveState.initial();
				resolveState = resolveState.put(CSharpResolveUtil.EXTRACTOR_KEY, dotNetTypeRef.getGenericExtractor(psiElement1, element));

				p = new MemberResolveScopeProcessor(Conditions.and(condition, new Condition<PsiNamedElement>()
				{
					@Override
					public boolean value(PsiNamedElement psiNamedElement)
					{
						return psiNamedElement instanceof CSharpFieldDeclaration || psiNamedElement instanceof CSharpPropertyDeclaration;
					}
				}), named);
				CSharpResolveUtil.walkChildren(p, psiElement1, element, null, resolveState);
				return p.getElements();
			case LABEL:
				DotNetQualifiedElement parentOfType = PsiTreeUtil.getParentOfType(element, DotNetQualifiedElement.class);
				assert parentOfType != null;
				p = new MemberResolveScopeProcessor(Conditions.and(condition, new Condition<PsiNamedElement>()
				{
					@Override
					public boolean value(PsiNamedElement psiNamedElement)
					{
						return psiNamedElement instanceof CSharpLabeledStatementImpl;
					}
				}), named);
				CSharpResolveUtil.treeWalkUp(p, element, element, parentOfType);
				return p.getElements();
			case NAMESPACE:
				String qName = StringUtil.strip(element.getText(), CharFilter.NOT_WHITESPACE_FILTER);
				CSharpNamespaceAsElement aPackage = CSharpNamespaceHelper.getNamespaceElementIfFind(element.getProject(), qName, element.getResolveScope());
				if(aPackage == null)
				{
					return Collections.emptyList();
				}
				return Collections.<PsiElement>singletonList(aPackage);
			case NAMESPACE_WITH_CREATE_OPTION:
				String qName2 = StringUtil.strip(element.getText(), CharFilter.NOT_WHITESPACE_FILTER);
				return Collections.<PsiElement>singletonList(new CSharpNamespaceAsElement(element.getProject(), qName2, element.getResolveScope()));
			case ATTRIBUTE:
				/*condition = Conditions.and(condition, ourTypeOrMethodOrGenericCondition);
				val resolveResults = processAnyMember(qualifier, condition, named);
				if(resolveResults.size() != 1)
				{
					return resolveResults;
				}
				return resolveResults; //TODO [VISTALL] resolve to constuctor   */
			case TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD:
			case METHOD:
			case ARRAY_METHOD:
			case ANY_MEMBER:
				if(kind == ResolveToKind.METHOD)
				{
					condition = Conditions.and(condition, ourMethodCondition);
				}
				else if(kind == ResolveToKind.TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD)
				{
					condition = Conditions.and(condition, ourTypeOrMethodOrGenericCondition);
				}

				return processAnyMember(qualifier, condition, element, kind, named);
		}
		return Collections.emptyList();
	}

	private static <T extends PsiQualifiedReference & PsiElement> Collection<PsiElement> processAnyMember(
			PsiElement qualifier,
			Condition<PsiNamedElement> condition,
			T element,
			ResolveToKind kind,
			boolean incompleteCode)
	{
		PsiElement target = element;
		DotNetGenericExtractor extractor = DotNetGenericExtractor.EMPTY;

		if(qualifier instanceof DotNetExpression)
		{
			DotNetTypeRef dotNetTypeRef = ((DotNetExpression) qualifier).toTypeRef();

			PsiElement resolve = dotNetTypeRef.resolve(element);

			if(resolve != null)
			{
				target = resolve;
				extractor = dotNetTypeRef.getGenericExtractor(resolve, element);
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
		if(extractor != DotNetGenericExtractor.EMPTY)
		{
			resolveState = resolveState.put(CSharpResolveUtil.EXTRACTOR_KEY, extractor);
		}

		if(target != element)
		{
			CSharpResolveUtil.walkChildren(p, target, element, null, resolveState);
		}
		else
		{
			PsiElement last = null;
			PsiElement targetToWalkChildren = null;

			PsiElement temp = element.getParent();
			while(temp != null)
			{
				if(temp instanceof DotNetParameter)
				{
					last = element;
					targetToWalkChildren = PsiTreeUtil.getParentOfType(temp, DotNetMethodDeclaration.class);
					break;
				}
				else if(temp instanceof DotNetType)
				{
					last = null;
					break;
				}
				else if(temp instanceof DotNetFieldDeclaration)
				{
					last = element;
					targetToWalkChildren = temp.getParent();
					break;
				}
				else if(temp instanceof DotNetXXXAccessor)
				{
					last = temp;
					targetToWalkChildren = temp.getParent().getParent();
					break;
				}
				else if(temp instanceof DotNetMethodDeclaration || temp instanceof CSharpConstructorDeclaration)
				{
					DotNetLikeMethodDeclaration netMethodDeclaration = (DotNetLikeMethodDeclaration) temp;
					DotNetParameterList parameterList = netMethodDeclaration.getParameterList();
					targetToWalkChildren = temp.getParent();
					last = parameterList == null ? netMethodDeclaration.getCodeBlock() : parameterList;
					break;
				}
				temp = temp.getParent();
			}

			if(!CSharpResolveUtil.treeWalkUp(p, target, element, last, resolveState))
			{
				return p.getElements();
			}

			if(last == null)
			{
				return p.getElements();
			}

			if(targetToWalkChildren == null)
			{
				LOGGER.warn(element.getText() + " " + last + " " + kind);
				return Collections.emptyList();
			}
			CSharpResolveUtil.walkChildren(p, targetToWalkChildren, element, null, resolveState);
		}

		return p.getElements();
	}

	@NotNull
	private DotNetTypeRef resolveBaseTypeRef()
	{
		DotNetTypeDeclaration typeDeclaration = PsiTreeUtil.getParentOfType(this, DotNetTypeDeclaration.class);
		if(typeDeclaration == null)
		{
			return DotNetTypeRef.ERROR_TYPE;
		}

		DotNetType[] anExtends = typeDeclaration.getExtends();
		if(anExtends.length == 0)
		{
			return new CSharpTypeDefTypeRef(DotNetTypes.System_Object, 0);
		}
		else
		{
			for(DotNetType anExtend : anExtends)
			{
				DotNetTypeRef dotNetTypeRef = anExtend.toTypeRef();

				PsiElement resolve = dotNetTypeRef.resolve(this);
				if(resolve instanceof DotNetTypeDeclaration && !((DotNetTypeDeclaration) resolve).isInterface())
				{
					return dotNetTypeRef;
				}
			}

			return new CSharpTypeDefTypeRef(DotNetTypes.System_Object, 0);
		}
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
		PsiElement tempElement = getParent();
		if(tempElement instanceof CSharpGenericConstraintImpl)
		{
			DotNetGenericParameterListOwner parameterListOwner = PsiTreeUtil.getParentOfType(this, DotNetGenericParameterListOwner.class);
			if(parameterListOwner == null)
			{
				return ResolveToKind.ANY_MEMBER;
			}

			return ResolveToKind.TYPE_PARAMETER_FROM_PARENT;
		}
		else if(tempElement instanceof CSharpNamespaceDeclarationImpl)
		{
			return ResolveToKind.NAMESPACE_WITH_CREATE_OPTION;
		}
		else if(tempElement instanceof DotNetReferenceType)
		{
			return ResolveToKind.TYPE_OR_GENERIC_PARAMETER_OR_DELEGATE_METHOD;
		}
		else if(tempElement instanceof CSharpUsingNamespaceStatementImpl)
		{
			assert true;
			return ResolveToKind.NAMESPACE;
		}
		else if(tempElement instanceof CSharpAttributeImpl)
		{
			return ResolveToKind.ATTRIBUTE;
		}
		else if(tempElement instanceof CSharpFieldOrPropertySet)
		{
			if(((CSharpFieldOrPropertySet) tempElement).getNameReferenceExpression() == this)
			{
				return ResolveToKind.FIELD_OR_PROPERTY;
			}
		}
		else if(tempElement instanceof CSharpReferenceExpressionImpl)
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
		else if(tempElement instanceof CSharpMethodCallExpressionImpl)
		{
			return ResolveToKind.METHOD;
		}
		else if(tempElement instanceof CSharpGotoStatementImpl)
		{
			return ResolveToKind.LABEL;
		}

		tempElement = findChildByType(CSharpTokenSets.NATIVE_TYPES);
		if(tempElement != null)
		{
			return ResolveToKind.NATIVE_TYPE_WRAPPER;
		}

		tempElement = findChildByType(CSharpTokens.THIS_KEYWORD);
		if(tempElement != null)
		{
			return ResolveToKind.THIS;
		}
		tempElement = findChildByType(CSharpTokens.BASE_KEYWORD);
		if(tempElement != null)
		{
			return ResolveToKind.BASE;
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
			return Comparing.equal(((CSharpNamespaceAsElement) resolve).getPresentableQName(), ((CSharpNamespaceAsElement) element)
					.getPresentableQName());
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
				return psiNamedElement.getName() != null
						&& !(psiNamedElement instanceof CSharpConstructorDeclaration)
						&& !(psiNamedElement instanceof CSharpMethodDeclaration && ((CSharpMethodDeclaration) psiNamedElement).isOperator())
						&& !(psiNamedElement instanceof CSharpArrayMethodDeclarationImpl)
						&& (psiNamedElement instanceof DotNetModifierListOwner &&
						CSharpVisibilityUtil.isVisibleForCompletion((DotNetModifierListOwner) psiNamedElement, CSharpReferenceExpressionImpl.this));
			}
		}, this, false);
		return CSharpLookupElementBuilder.getInstance(getProject()).buildToLookupElements(this, psiElements);
	}

	@Override
	public boolean isSoft()
	{
		return false;
	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef()
	{
		ResolveToKind kind = kind();
		switch(kind)
		{
			case BASE:
				return resolveBaseTypeRef();
		}

		PsiElement resolve = resolve();
		if(resolve instanceof CSharpNamespaceAsElement)
		{
			return new CSharpNamespaceDefTypeRef(((CSharpNamespaceAsElement) resolve).getPresentableQName());
		}
		else if(resolve instanceof CSharpTypeDeclarationImpl)
		{
			return new CSharpTypeDefTypeRef(((CSharpTypeDeclarationImpl) resolve).getPresentableQName(),
					((CSharpTypeDeclarationImpl) resolve).getGenericParametersCount());
		}
		else if(resolve instanceof CSharpTypeDefStatementImpl)
		{
			return ((CSharpTypeDefStatementImpl) resolve).toTypeRef();
		}
		else if(resolve instanceof DotNetGenericParameter)
		{
			return new CSharpGenericParameterTypeRef((DotNetGenericParameter) resolve);
		}
		else if(resolve instanceof CSharpMethodDeclaration)
		{
			return ((CSharpMethodDeclaration) resolve).getReturnTypeRef();
		}
		else if(resolve instanceof DotNetVariable)
		{
			return ((DotNetVariable) resolve).toTypeRef();
		}
		return DotNetTypeRef.ERROR_TYPE;
	}
}
