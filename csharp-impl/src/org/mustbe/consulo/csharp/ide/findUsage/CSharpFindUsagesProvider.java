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

package org.mustbe.consulo.csharp.ide.findUsage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.lexer.CSharpLexer;
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.CSharpNamespaceAsElement;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpEventDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpFieldDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpParameterImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpPropertyDeclarationImpl;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 21.12.13.
 */
public class CSharpFindUsagesProvider implements FindUsagesProvider
{
	@Nullable
	@Override
	public WordsScanner getWordsScanner()
	{
		return new DefaultWordsScanner(new CSharpLexer(), TokenSet.create(CSharpTokens.IDENTIFIER), CSharpTokenSets.COMMENTS, CSharpTokenSets.LITERALS);
	}

	@Override
	public boolean canFindUsagesFor(@NotNull PsiElement element)
	{
		return true;
	}

	@Nullable
	@Override
	public String getHelpId(@NotNull PsiElement element)
	{
		return null;
	}

	@NotNull
	@Override
	public String getType(@NotNull PsiElement element)
	{
		if(element instanceof CSharpTypeDeclaration)
		{
			return "type";
		}
		else if(element instanceof CSharpMethodDeclaration)
		{
			return "method";
		}
		else if(element instanceof CSharpNamespaceAsElement)
		{
			return "namespace";
		}
		else if(element instanceof CSharpEventDeclarationImpl)
		{
			return "event";
		}
		else if(element instanceof CSharpPropertyDeclarationImpl)
		{
			return "property";
		}
		else if(element instanceof CSharpParameterImpl)
		{
			return "parameter";
		}
		else if(element instanceof CSharpFieldDeclarationImpl)
		{
			return "field";
		}

		return "getType " + element.getNode().getElementType();
	}

	@NotNull
	@Override
	public String getDescriptiveName(@NotNull PsiElement element)
	{

		return "getDescriptiveName " + element.getNode().getElementType();
	}

	@NotNull
	@Override
	public String getNodeText(@NotNull PsiElement element, boolean b)
	{
		return "getNodeText " + element.getNode().getElementType();
	}
}
