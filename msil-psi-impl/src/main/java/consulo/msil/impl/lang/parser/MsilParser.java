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

package consulo.msil.impl.lang.parser;

import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiBuilderUtil;
import consulo.language.parser.PsiParser;
import consulo.language.version.LanguageVersion;
import consulo.msil.impl.lang.psi.MsilElements;
import consulo.msil.impl.lang.psi.MsilTokenSets;
import consulo.msil.impl.lang.psi.MsilTokens;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilParser implements PsiParser, MsilTokens, MsilTokenSets, MsilElements
{
	private static final Map<IElementType, IElementType> ourConstantValues = new HashMap<IElementType, IElementType>();
	static
	{
		ourConstantValues.put(FLOAT32_KEYWORD, DOUBLE_LITERAL);
		ourConstantValues.put(FLOAT64_KEYWORD, DOUBLE_LITERAL);
		ourConstantValues.put(INT8_KEYWORD, NUMBER_LITERAL);
		ourConstantValues.put(UINT8_KEYWORD, NUMBER_LITERAL);
		ourConstantValues.put(INT16_KEYWORD, NUMBER_LITERAL);
		ourConstantValues.put(UINT16_KEYWORD, NUMBER_LITERAL);
		ourConstantValues.put(INT32_KEYWORD, NUMBER_LITERAL);
		ourConstantValues.put(UINT32_KEYWORD, NUMBER_LITERAL);
		ourConstantValues.put(INT64_KEYWORD, NUMBER_LITERAL);
		ourConstantValues.put(UINT64_KEYWORD, NUMBER_LITERAL);
		ourConstantValues.put(CHAR_KEYWORD, NUMBER_LITERAL);
		ourConstantValues.put(BOOL_KEYWORD, BOOL_LITERAL);
	}

	@Nonnull
	@Override
	public ASTNode parse(@Nonnull IElementType elementType, @Nonnull PsiBuilder builder, @Nonnull LanguageVersion languageVersion)
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

		IElementType doneTo = null;
		if(expect(builder, TYPE_KEYWORD, null))
		{
			expect(builder, IDENTIFIER, "Identifier expected");
			doneTo = TYPE_PARAMETER_ATTRIBUTE_LIST;
		}
		else
		{
			if(expect(builder, LBRACKET, "'[' expected"))
			{
				expect(builder, NUMBER_LITERAL, "Index expected");
				expect(builder, RBRACKET, "']' expected");
			}

			parseConstantValue(builder);

			doneTo = PARAMETER_ATTRIBUTE_LIST;
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

		mark.done(doneTo);
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

		parseConstantValue(builder);

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

		mark.done(XACCESSOR);
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

		if(builder.getTokenType() == MsilTokens.LPAR)
		{
			PsiBuilder.Marker signatureMark = builder.mark();

			builder.advanceLexer();

			while(!builder.eof())
			{
				if(builder.getTokenType() == RPAR)
				{
					break;
				}
				else
				{
					if(builder.getTokenType() == HEX_NUMBER_LITERAL)
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

			signatureMark.done(CUSTOM_ATTRIBUTE_SIGNATURE);
		}

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
		else if(tokenType == NUMBER_LITERAL)
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

			mark.done(REFERENCE_TYPE);
		}
		else
		{
			parseReferenceExpression(builder);

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

		if(builder.getTokenType() == MsilTokens.NUMBER_LITERAL)
		{
			builder.advanceLexer();

			expect(builder, MsilTokens.ELLIPSIS, "'...' expected");

			mark.done(ARRAY_DIMENSION);
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

	private static void parseConstantValue(PsiBuilder builder)
	{
		if(builder.getTokenType() == EQ)
		{
			builder.advanceLexer();

			PsiBuilder.Marker marker = builder.mark();

			if(builder.getTokenType() == STRING_LITERAL || builder.getTokenType() == NULLREF_KEYWORD)
			{
				builder.advanceLexer();
			}
			else
			{
				IElementType iElementType = ourConstantValues.get(builder.getTokenType());
				if(iElementType != null)
				{
					builder.advanceLexer();  // advance keyword

					if(expect(builder, LPAR, "'(' expected"))
					{
						expect(builder, iElementType, "Expected value");

						expect(builder, RPAR, "')' expected");
					}
				}
				else
				{
					builder.error("Expected value");
				}
			}


			marker.done(CONSTANT_VALUE);
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
		PsiBuilder.Marker marker = builder.mark();

		expect(builder, GENERIC_CONSTRAINT_KEYWORDS, null);

		expect(builder, _CTOR_KEYWORD, null);

		parseTypeList(builder, LPAR, RPAR, GENERIC_PARAM_EXTENDS_LIST);

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
