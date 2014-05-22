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

package org.mustbe.consulo.msil.lang.parser;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.msil.lang.psi.MsilElements;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderUtil;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilParser implements PsiParser, MsilTokens, MsilTokenSets, MsilElements
{
	@NotNull
	@Override
	public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder, @NotNull LanguageVersion languageVersion)
	{
		PsiBuilder.Marker mark = builder.mark();
		while(!builder.eof())
		{
			IElementType tokenType = builder.getTokenType();
			if(tokenType == _CLASS_KEYWORD)
			{
				parseClass(builder);
			}
			else
			{
				builder.advanceLexer();
			}
		}
		mark.done(elementType);
		return builder.getTreeBuilt();
	}

	private void parseClass(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseModifierList(builder);

		expect(builder, IDENTIFIER, "Expected name");

		if(builder.getTokenType() == EXTENDS_KEYWORD)
		{
			PsiBuilder.Marker newMark = builder.mark();

			builder.advanceLexer();

			parseType(builder);

			newMark.done(EXTENDS_TYPE_LIST);
		}

		if(builder.getTokenType() == IMPLEMENTS_KEYWORD)
		{
			PsiBuilder.Marker newMark = builder.mark();

			builder.advanceLexer();

			while(!builder.eof())
			{
				parseType(builder);

				if(builder.getTokenType() != COMMA)
				{
					break;
				}
				else
				{
					builder.advanceLexer();
				}
			}

			newMark.done(IMPLEMENTS_TYPE_LIST);
		}

		mark.done(CLASS);
	}

	private void parseType(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		IElementType tokenType = builder.getTokenType();
		if(NATIVE_TYPES.contains(tokenType))
		{
			builder.advanceLexer();
			mark.done(NATIVE_TYPE);
		}
		else if(REFERENCE_TYPE_START.contains(tokenType))
		{
			builder.advanceLexer();

			parseReferenceExpression(builder);

			mark.done(REFERENCE_TYPE);
		}
		else
		{
			parseReferenceExpression(builder);

			mark.done(REFERENCE_TYPE);
		}

		if(builder.getTokenType() == PERC)
		{

		}
	}

	private void parseReferenceExpression(PsiBuilder builder)
	{
		if(builder.getTokenType() == IDENTIFIER)
		{
			PsiBuilder.Marker mark = builder.mark();

			builder.advanceLexer();

			mark.done(REFERENCE_EXPRESSION);
		}
		else
		{
			builder.error("Identifier expected");
		}
	}

	private void parseModifierList(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		while(MsilTokenSets.MODIFIERS.contains(builder.getTokenType()))
		{
			builder.advanceLexer();
		}
		mark.done(MODIFIER_LIST);
	}

	private static boolean expect(PsiBuilder builder, TokenSet tokenSet, String name)
	{
		if(!PsiBuilderUtil.expect(builder, tokenSet))
		{
			builder.error(name);
			return false;
		}
		else
		{
			return true;
		}
	}

	private static boolean expect(PsiBuilder builder, IElementType tokenSet, String name)
	{
		if(!PsiBuilderUtil.expect(builder, tokenSet))
		{
			builder.error(name);
			return false;
		}
		else
		{
			return true;
		}
	}
}
