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

package consulo.dotnet.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dotnet.psi.DotNetAttribute;
import consulo.dotnet.psi.DotNetAttributeUtil;
import consulo.dotnet.psi.DotNetCallArgumentList;
import consulo.dotnet.psi.DotNetCallArgumentListOwner;
import consulo.dotnet.psi.DotNetExpression;
import consulo.dotnet.psi.DotNetLikeMethodDeclaration;
import consulo.dotnet.psi.DotNetParameter;
import consulo.dotnet.util.ArrayUtil2;
import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.util.PsiTreeUtil;
import consulo.lang.LanguageVersion;
import consulo.lang.util.LanguageVersionUtil;

/**
 * @author VISTALL
 * @since 31.08.14
 */
public class MultiHostInjectorByAttribute implements MultiHostInjector
{
	@Override
	public void injectLanguages(@NotNull MultiHostRegistrar multiHostRegistrar, @NotNull PsiElement element)
	{
		DotNetCallArgumentList argumentList = (DotNetCallArgumentList) element;

		DotNetCallArgumentListOwner owner = PsiTreeUtil.getParentOfType(element, DotNetCallArgumentListOwner.class);
		if(owner == null)
		{
			return;
		}

		PsiElement psiElement = owner.resolveToCallable();
		if(!(psiElement instanceof DotNetLikeMethodDeclaration))
		{
			return;
		}

		DotNetParameter[] parameters = ((DotNetLikeMethodDeclaration) psiElement).getParameters();

		DotNetExpression[] expressions = argumentList.getExpressions();

		for(int i = 0; i < parameters.length; i++)
		{
			DotNetParameter parameter = parameters[i];

			DotNetAttribute attribute = DotNetAttributeUtil.findAttribute(parameter, "MustBe.Consulo.Attributes.InjectLanguageAttribute");
			if(attribute != null)
			{
				DotNetExpression dotNetExpression = ArrayUtil2.safeGet(expressions, i);
				if(dotNetExpression == null)
				{
					continue;
				}

				LanguageVersion languageVersion = findLanguageFromAttribute(attribute);
				if(languageVersion == null)
				{
					continue;
				}

				TextRange textRangeForInject = findTextRangeForInject(dotNetExpression);
				if(textRangeForInject == null)
				{
					continue;
				}


				multiHostRegistrar.startInjecting(languageVersion).addPlace("", "", (PsiLanguageInjectionHost) dotNetExpression, textRangeForInject).doneInjecting();
			}
		}
	}

	@Nullable
	private static TextRange findTextRangeForInject(@NotNull DotNetExpression expression)
	{
		for(MultiHostInjectorByAttributeHelper attributeHelper : MultiHostInjectorByAttributeHelper.EP_NAME.getExtensions())
		{
			TextRange textRange = attributeHelper.getTextRangeForInject(expression);
			if(textRange != null)
			{
				return textRange;
			}
		}
		return null;
	}

	@Nullable
	private static LanguageVersion findLanguageFromAttribute(@NotNull DotNetAttribute attribute)
	{
		for(MultiHostInjectorByAttributeHelper attributeHelper : MultiHostInjectorByAttributeHelper.EP_NAME.getExtensions())
		{
			String languageId = attributeHelper.getLanguageId(attribute);
			if(languageId != null)
			{
				if(StringUtil.containsChar(languageId, ':'))
				{
					String[] split = languageId.split(":");
					Language languageByID = Language.findLanguageByID(split[0]);
					if(languageByID == null)
					{
						return null;
					}
					String version = split[1];
					for(LanguageVersion languageVersion : languageByID.getVersions())
					{
						if(languageVersion.getName().equals(version))
						{
							return languageVersion;
						}
					}
					return null;
				}

				Language languageByID = Language.findLanguageByID(languageId);
				return languageByID == null ? null : LanguageVersionUtil.findDefaultVersion(languageByID);
			}
		}
		return null;
	}
}
