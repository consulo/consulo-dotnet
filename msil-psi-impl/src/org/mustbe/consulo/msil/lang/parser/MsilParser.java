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
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderUtil;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import lombok.val;

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
		else if(tokenType == _ASSEMBLY_KEYWORD)
		{
			parseAssembly(builder);
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
		else if(tokenType == _PARAM_KEYWORD)
		{
			parseParamAttributeList(builder);
		}
		else if(XXX_ACCESSOR_START.contains(tokenType))
		{
			parseAccessor(builder);
		}
		else
		{
			builder.error("Unexpected token " + tokenType);
			builder.advanceLexer();
		}
	}

	private void parseParamAttributeList(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		if(expect(builder, LBRACKET, "'[' expected"))
		{
			expect(builder, NUMBER, "Index expected");
			expect(builder, RBRACKET, "']' expected");
		}

		while(!builder.eof())
		{
			if(builder.getTokenType() == _CUSTOM_KEYWORD)
			{
				parseAttribute(builder);
			}
			else
			{
				break;
			}
		}

		mark.done(PARAMETER_ATTRIBUTE_LIST);
	}

	private void parseAssembly(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

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

		mark.done(ASSEMBLY);
	}

	private void parseMethod(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseModifierList(builder);

		parseType(builder);

		expect(builder, IDENTIFIERS_AND_CTOR, "Identifier expected");

		parseGenericList(builder);

		parseParameterList(builder);

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

		mark.done(METHOD);
	}

	private void parseProperty(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseModifierList(builder);

		parseType(builder);

		expect(builder, IDENTIFIERS, "Identifier expected");

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

		expect(builder, IDENTIFIERS, "Identifier expected");

		//TODO [VISTALL] initializer

		mark.done(FIELD);
	}

	private void parseEvent(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseModifierList(builder);

		parseType(builder);

		expect(builder, IDENTIFIERS, "Identifier expected");

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

	private void parseAccessor(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseType(builder); // return type

		parseType(builder); // contains

		expect(builder, COLONCOLON, "'::' expected");

		expect(builder, IDENTIFIERS, "Identifier expected");

		parseParameterList(builder);

		mark.done(XXX_ACCESSOR);
	}

	private void parseParameterList(PsiBuilder builder)
	{
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
					if(IDENTIFIERS.contains(builder.getTokenType()))
					{
						builder.advanceLexer();
					}
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
	}

	private void parseAttribute(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseType(builder);

		expect(builder, COLONCOLON, "'::' expected");
		expect(builder, _CTOR_KEYWORD, "'.ctor' expected");
		parseParameterList(builder);
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

		expect(builder, IDENTIFIERS, "Expected name");

		parseGenericList(builder);

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
		else if(tokenType == NUMBER)
		{
			builder.advanceLexer();
			mark.done(METHOD_GENERIC_TYPE);
		}
		else if(tokenType == EXCL)
		{
			builder.advanceLexer();
			expect(builder, IDENTIFIERS, "Identifier expected");
			mark.done(CLASS_GENERIC_TYPE);
		}
		else if(REFERENCE_TYPE_START.contains(tokenType))
		{
			builder.advanceLexer();

			parseReferenceExpression(builder);

			if(builder.getTokenType() == BACKSLASH)
			{
				builder.advanceLexer();
				expect(builder, IDENTIFIERS, "Identifier expected");
			}

			mark.done(REFERENCE_TYPE);
		}
		else
		{
			parseReferenceExpression(builder);

			if(builder.getTokenType() == BACKSLASH)
			{
				builder.advanceLexer();
				expect(builder, IDENTIFIERS, "Identifier expected");
			}

			mark.done(REFERENCE_TYPE);
		}

		while(builder.getTokenType() == PERC)
		{
			mark = mark.precede();
			builder.advanceLexer();
			mark.done(POINTER_TYPE);
		}

		if(builder.getTokenType() == LT)
		{
			mark = mark.precede();
			parseTypeList(builder, LT, GT, TYPE_ARGUMENTS_TYPE_LIST);
			mark.done(TYPE_WITH_TYPE_ARGUMENTS);
		}

		while(builder.getTokenType() == LBRACKET)
		{
			mark = mark.precede();

			expect(builder, LBRACKET, "'[' expected");
			parseArrayDimensions(builder);
			expect(builder, RBRACKET, "']' expected");
			mark.done(ARRAY_TYPE);
		}

		if(builder.getTokenType() == AND)
		{
			mark = mark.precede();
			builder.advanceLexer();
			mark.done(TYPE_BY_REF);
		}
	}

	private void parseArrayDimensions(PsiBuilder builder)
	{
		while(true)
		{
			PsiBuilder.Marker marker = parseArrayDimension(builder);
			if(marker == null)
			{
				break;
			}

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

	private PsiBuilder.Marker parseArrayDimension(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		if(builder.getTokenType() == MsilTokens.NUMBER)
		{
			builder.advanceLexer();

			expect(builder, MsilTokens.ELLIPSIS, "'...' expected");

			mark.done(MsilStubElements.ARRAY_DIMENSION);
			return mark;
		}
		else
		{
			mark.drop();
			return null;
		}
	}

	private void parseReferenceExpression(PsiBuilder builder)
	{
		if(IDENTIFIERS.contains(builder.getTokenType()))
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

	public void parseGenericList(PsiBuilder builder)
	{
		if(builder.getTokenType() != LT)
		{
			return;
		}

		PsiBuilder.Marker mark = builder.mark();
		builder.advanceLexer();

		while(true)
		{
			parseGeneric(builder);

			if(builder.getTokenType() == COMMA)
			{
				builder.advanceLexer();
			}
			else
			{
				break;
			}
		}

		expect(builder, GT, "'>' expected");

		mark.done(GENERIC_PARAMETER_LIST);
	}

	public void parseGeneric(PsiBuilder builder)
	{
		val marker = builder.mark();

		if(builder.getTokenType() == PLUS || builder.getTokenType() == MINUS)
		{
			builder.advanceLexer();
		}

		expect(builder, IDENTIFIERS, "Name expected");

		marker.done(GENERIC_PARAMETER);
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
