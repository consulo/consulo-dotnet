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
			parse(builder);
		}
		mark.done(elementType);
		return builder.getTreeBuilt();
	}

	private void parse(PsiBuilder builder)
	{
		IElementType tokenType = builder.getTokenType();
		if(tokenType == _CLASS_KEYWORD)
		{
			parseClass(builder);
		}
		else if(tokenType == _CUSTOM_KEYWORD)
		{
			parseAttribute(builder);
		}
		else if(tokenType == _FIELD_KEYWORD)
		{
			parseField(builder);
		}
		else if(tokenType == _EVENT_KEYWORD)
		{
			parseEvent(builder);
		}
		else if(tokenType == _PROPERTY_KEYWORD)
		{
			parseProperty(builder);
		}
		else if(tokenType == _METHOD_KEYWORD)
		{
			parseMethod(builder);
		}
		else
		{
			builder.error("Unexpected token " + tokenType);
			builder.advanceLexer();
		}
	}

	private void parseMethod(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseModifierList(builder);

		parseType(builder);

		expect(builder, IDENTIFIERS, "Identifier expected");

		if(expect(builder, LPAR, "'(' expected"))
		{
			PsiBuilder.Marker parameterListMarker = builder.mark();

			if(builder.getTokenType() != RPAR)
			{
				while(!builder.eof())
				{
					PsiBuilder.Marker parameterMarker = builder.mark();
					parseModifierList(builder);
					parseType(builder);
					expect(builder, IDENTIFIERS, "Identifier expected");
					parameterMarker.done(PARAMETER);

					if(builder.getTokenType() == COMMA)
					{
						builder.advanceLexer();
					}
					else
					{
						break;
					}
				}
			}

			expect(builder, RPAR, "')' expected");
			parameterListMarker.done(PARAMETER_LIST);
		}

		if(expect(builder, LBRACE, "'{' expected"))
		{
			while(!builder.eof())
			{
				if(builder.getTokenType() == RBRACE)
				{
					break;
				}
				else
				{
					parse(builder);
				}
			}
			expect(builder, RBRACE, "'}' expected");
		}

		mark.done(PROPERTY);
	}

	private void parseProperty(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseModifierList(builder);

		parseType(builder);

		expect(builder, IDENTIFIER, "Identifier expected");

		if(expect(builder, LBRACE, "'{' expected"))
		{
			while(!builder.eof())
			{
				if(builder.getTokenType() == RBRACE)
				{
					break;
				}
				else
				{
					parse(builder);
				}
			}
			expect(builder, RBRACE, "'}' expected");
		}

		mark.done(PROPERTY);
	}

	private void parseField(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseModifierList(builder);

		parseType(builder);

		expect(builder, IDENTIFIER, "Identifier expected");

		//TODO [VISTALL] initializer

		mark.done(FIELD);
	}

	private void parseEvent(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseModifierList(builder);

		parseType(builder);

		expect(builder, IDENTIFIER, "Identifier expected");

		if(expect(builder, LBRACE, "'{' expected"))
		{
			while(!builder.eof())
			{
				if(builder.getTokenType() == RBRACE)
				{
					break;
				}
				else
				{
					parse(builder);
				}
			}
			expect(builder, RBRACE, "'}' expected");
		}

		mark.done(EVENT);
	}

	private void parseAttribute(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseType(builder);

		expect(builder, COLONCOLON, "'::' expected");
		expect(builder, IDENTIFIER, "Identifier expected");
		expect(builder, LPAR, "'(' expected");
		expect(builder, RPAR, "')' expected");
		expect(builder, EQ, "'=' expected");
		expect(builder, LPAR, "'(' expected");
		expect(builder, RPAR, "')' expected");

		mark.done(CUSTOM_ATTRIBUTE);
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

		parseTypeList(builder, IMPLEMENTS_KEYWORD, null, IMPLEMENTS_TYPE_LIST);

		if(expect(builder, LBRACE, "'{' expected"))
		{
			while(!builder.eof())
			{
				if(builder.getTokenType() == RBRACE)
				{
					break;
				}
				else
				{
					parse(builder);
				}
			}
			expect(builder, RBRACE, "'}' expected");
		}
		mark.done(CLASS);
	}

	private void parseTypeList(PsiBuilder builder, IElementType start, IElementType stop, IElementType to)
	{
		if(builder.getTokenType() == start)
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

			if(stop != null)
			{
				expect(builder, stop, stop + " is expected");
			}

			newMark.done(to);
		}
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
			mark = mark.precede();
			builder.advanceLexer();
			mark.done(POINTER_TYPE);
		}
		else if(builder.getTokenType() == LT)
		{
			mark = mark.precede();
			parseTypeList(builder, LT, GT, TYPE_ARGUMENTS_TYPE_LIST);
			mark.done(TYPE_WITH_TYPE_ARGUMENTS);
		}

		while(builder.getTokenType() == LBRACKET)
		{
			mark = mark.precede();
			expect(builder, LBRACKET, "'[' expected");
			expect(builder, RBRACKET, "']' expected");
			mark.done(ARRAY_TYPE);
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

	private static boolean expect(PsiBuilder builder, TokenSet tokenSet, String text)
	{
		if(!PsiBuilderUtil.expect(builder, tokenSet))
		{
			if(text != null)
			{
				builder.error(text);
			}
			return false;
		}
		else
		{
			return true;
		}
	}

	private static boolean expect(PsiBuilder builder, IElementType tokenSet, String text)
	{
		if(!PsiBuilderUtil.expect(builder, tokenSet))
		{
			if(text != null)
			{
				builder.error(text);
			}
			return false;
		}
		else
		{
			return true;
		}
	}
}
