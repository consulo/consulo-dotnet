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

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.ide.CSharpErrorBundle;
import org.mustbe.consulo.csharp.ide.highlight.check.CompilerCheck;
import org.mustbe.consulo.csharp.lang.psi.CSharpLocalVariable;
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpSoftTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpLambdaExpressionImpl;
import org.mustbe.consulo.csharp.module.extension.CSharpLanguageVersion;
import org.mustbe.consulo.dotnet.psi.DotNetModifierList;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Function;

/**
 * @author VISTALL
 * @since 15.05.14
 */
public class CS1644 extends CompilerCheck<PsiElement>
{
	class Feature
	{
		private String myName;
		private CSharpLanguageVersion myLanguageVersion;
		private Function<PsiElement, PsiElement> myFunc;

		Feature(String name, CSharpLanguageVersion languageVersion, Function<PsiElement, PsiElement> processor)
		{
			myName = name;
			myLanguageVersion = languageVersion;
			myFunc = processor;
		}
	}

	private List<Feature> myFeatures = new ArrayList<Feature>()
	{
		{
			add(new Feature("lambda expressions", CSharpLanguageVersion._3_0, new Function<PsiElement, PsiElement>()
			{
				@Override
				public PsiElement fun(PsiElement element)
				{
					if(element instanceof CSharpLambdaExpressionImpl)
					{
						return element;
					}
					return null;
				}
			}));
			add(new Feature("implicitly typed local variable", CSharpLanguageVersion._3_0, new Function<PsiElement, PsiElement>()
			{
				@Override
				public PsiElement fun(PsiElement element)
				{
					if(element instanceof CSharpLocalVariable && ((CSharpLocalVariable) element).toTypeRef(false) == DotNetTypeRef.AUTO_TYPE)
					{
						return ((CSharpLocalVariable) element).getType();
					}
					return null;
				}
			}));
			add(new Feature("extension methods", CSharpLanguageVersion._3_0, new Function<PsiElement, PsiElement>()
			{
				@Override
				public PsiElement fun(PsiElement element)
				{
					if(element instanceof CSharpMethodDeclaration)
					{
						DotNetParameter[] parameters = ((CSharpMethodDeclaration) element).getParameters();
						if(parameters.length > 0)
						{
							DotNetModifierList modifierList = parameters[0].getModifierList();
							if(modifierList != null)
							{
								PsiElement modifier = modifierList.getModifier(CSharpTokens.THIS_KEYWORD);
								if(modifier != null)
								{
									return modifier;
								}
							}
						}
					}
					return null;
				}
			}));
		}
	};

	private TokenSet myAllKeywords = TokenSet.orSet(CSharpTokenSets.KEYWORDS, CSharpSoftTokens.ALL);

	@Nullable
	@Override
	public CompilerCheckResult check(@NotNull CSharpLanguageVersion languageVersion, @NotNull PsiElement element)
	{
		for(Feature feature : myFeatures)
		{
			if(languageVersion.ordinal() < feature.myLanguageVersion.ordinal())
			{
				PsiElement fun = feature.myFunc.fun(element);
				if(fun == null)
				{
					continue;
				}
				String message = CSharpErrorBundle.message(getClass().getSimpleName(), feature.myName, languageVersion.getPresentableName());
				if(ApplicationManager.getApplication().isInternal())
				{
					message = getClass().getSimpleName() + ": " + message;
				}

				CompilerCheckResult result = new CompilerCheckResult();
				result.setText(message);
				result.setTextRange(fun.getTextRange());

				IElementType elementType = fun.getNode().getElementType();
				if(!myAllKeywords.contains(elementType))
				{
					boolean foundKeywordAndItSolo = false;
					ASTNode[] children = fun.getNode().getChildren(null);
					for(ASTNode child : children)
					{
						if(CSharpTokenSets.COMMENTS.contains(child.getElementType()) || CSharpTokenSets.WHITE_SPACE == child.getElementType())
						{
							continue;
						}

						if(myAllKeywords.contains(child.getElementType()))
						{
							foundKeywordAndItSolo = true;
						}
						else if(foundKeywordAndItSolo)  // if we found keyword but parent have other elements - we cant highlight as error
						{
							return result;
						}
					}

					if(foundKeywordAndItSolo)
					{
						result.setHighlightInfoType(HighlightInfoType.WRONG_REF);
					}
				}
				else
				{
					result.setHighlightInfoType(HighlightInfoType.WRONG_REF);
				}
				return result;
			}
		}
		return null;
	}
}
