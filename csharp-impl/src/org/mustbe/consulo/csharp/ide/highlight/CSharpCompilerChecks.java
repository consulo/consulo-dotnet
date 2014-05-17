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

package org.mustbe.consulo.csharp.ide.highlight;

import java.lang.reflect.ParameterizedType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.ide.highlight.check.CompilerCheck;
import org.mustbe.consulo.csharp.module.extension.CSharpLanguageVersion;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 15.05.14
 */
public enum CSharpCompilerChecks
{
	CS0029(CSharpLanguageVersion._1_0, HighlightInfoType.ERROR), // assign type check
	CS0155(CSharpLanguageVersion._1_0, HighlightInfoType.ERROR), // throw object must be child of System.Exception
	CS0214(CSharpLanguageVersion._1_0, HighlightInfoType.WRONG_REF), // fixed can be used inside unsafe context
	CS0227(CSharpLanguageVersion._1_0, HighlightInfoType.WRONG_REF), // 'unsafe' modifier check
	CS0231(CSharpLanguageVersion._1_0, HighlightInfoType.ERROR), // 'params' modifier must be last
	CS0304(CSharpLanguageVersion._2_0, HighlightInfoType.ERROR), // generic cant be new without new() constraint
	CS0401(CSharpLanguageVersion._2_0, HighlightInfoType.ERROR), // new() constraint must be last
	CS0409(CSharpLanguageVersion._2_0, HighlightInfoType.ERROR), // generic constraint already defined for generic
	CS0413(CSharpLanguageVersion._2_0, HighlightInfoType.ERROR), // 'S' operator  cant use to generic without class constraint, or reference
	CS0449(CSharpLanguageVersion._2_0, HighlightInfoType.ERROR), // struct or class constraint must be first
	CS0815(CSharpLanguageVersion._3_0, HighlightInfoType.ERROR), // lambdas cant be cast to 'var'
	CS1100(CSharpLanguageVersion._3_0, HighlightInfoType.ERROR), // 'this' modifier can be only set to first parameter
	CS1644(CSharpLanguageVersion._1_0, HighlightInfoType.ERROR), // features checks
	CS1737(CSharpLanguageVersion._1_0, HighlightInfoType.ERROR); // parameter default values check for order

	public static final CSharpCompilerChecks[] VALUES = CSharpCompilerChecks.values();

	private final CSharpLanguageVersion myLanguageVersion;
	private final HighlightInfoType myType;
	private final CompilerCheck<PsiElement> myCheck;
	private final Class<?> myTargetClass;

	CSharpCompilerChecks(CSharpLanguageVersion languageVersion, HighlightInfoType type)
	{
		myLanguageVersion = languageVersion;
		myType = type;
		try
		{
			Class<?> aClass = Class.forName("org.mustbe.consulo.csharp.ide.highlight.check.impl." + name());
			myCheck = (CompilerCheck<PsiElement>) aClass.newInstance();

			ParameterizedType genericType = (ParameterizedType) aClass.getGenericSuperclass();

			myTargetClass = (Class<?>) genericType.getActualTypeArguments()[0];
		}
		catch(Exception e)
		{
			throw new Error(e);
		}
	}

	@Nullable
	public CompilerCheck.CompilerCheckResult check(CSharpLanguageVersion languageVersion, PsiElement element)
	{
		CompilerCheck.CompilerCheckResult check = myCheck.check(languageVersion, element);
		if(check == null)
		{
			return null;
		}
		if(check.getHighlightInfoType() == null)
		{
			check.setHighlightInfoType(myType);
		}
		return check;
	}

	@NotNull
	public CSharpLanguageVersion getLanguageVersion()
	{
		return myLanguageVersion;
	}

	@NotNull
	public Class<?> getTargetClass()
	{
		return myTargetClass;
	}
}
