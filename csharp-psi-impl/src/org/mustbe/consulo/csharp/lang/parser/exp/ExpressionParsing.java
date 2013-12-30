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

package org.mustbe.consulo.csharp.lang.parser.exp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public class ExpressionParsing extends SharingParsingHelpers
{
	@Nullable
	public static PsiBuilder.Marker parse(CSharpBuilderWrapper wrapper)
	{
		PsiBuilder.Marker marker = parsePrimary(wrapper);
		if(marker == null)
		{
			return null;
		}

		IElementType tokenType = wrapper.getTokenType();
		if(tokenType == LPAR)
		{
			marker = marker.precede();

			parseParameterList(wrapper);

			marker.done(METHOD_CALL_EXPRESSION);
		}

		return marker;
	}

	public static void parseParameterList(CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		if(builder.getTokenType() == RPAR)
		{
			builder.advanceLexer();
			mark.done(METHOD_CALL_PARAMETER_LIST);
			return;
		}

		boolean empty = true;
		while(!builder.eof())
		{
			PsiBuilder.Marker marker = parse(builder);
			if(marker == null)
			{
				if(!empty)
				{
					builder.error("Expression expected");
				}
				break;
			}

			empty = false;

			if(builder.getTokenType() == COMMA)
			{
				builder.advanceLexer();
			}
			else if(builder.getTokenType() == RPAR)
			{
				break;
			}
			else
			{
				break;
			}
		}
		expect(builder, RPAR, "')' expected");
		mark.done(METHOD_CALL_PARAMETER_LIST);
	}

	private static PsiBuilder.Marker parsePrimary(CSharpBuilderWrapper wrapper)
	{
		PsiBuilder.Marker mark = wrapper.mark();
		IElementType tokenType = wrapper.getTokenType();
		if(CONSTANT_LITERALS.contains(tokenType))
		{
			wrapper.advanceLexer();

			mark.done(CONSTANT_EXPRESSION);
		}
		else if(tokenType == TYPEOF_KEYWORD)
		{
			parseTypeOfExpression(wrapper, mark);
		}
		else if(tokenType == NEW_KEYWORD)
		{
			parseNewExpression(wrapper, mark);
		}
		else if(tokenType == LPAR)
		{
			mark.drop();
			mark = parseParenthesesExpression(wrapper);
		}
		else if(wrapper.getTokenType() == IDENTIFIER)
		{
			mark.drop();
			mark = parseQualifiedReference(wrapper, null);
		}
		else
		{
			mark.drop();
			mark = null;
		}
		return mark;
	}

	public static PsiBuilder.Marker parseParenthesesExpression(CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker mark = builder.mark();
		if(expect(builder, LPAR, "'(' expected"))
		{
			parse(builder);
			expect(builder, RPAR, "')' expected");
			mark.done(PARENTHESES_EXPRESSION);
			return mark;
		}
		else
		{
			mark.drop();
			return null;
		}
	}

	private static void parseNewExpression(CSharpBuilderWrapper builder, PsiBuilder.Marker mark)
	{
		builder.advanceLexer();

		IElementType elementType = null;
		PsiBuilder.Marker typeMarker = parseType(builder);
		if(typeMarker == null)
		{
			builder.error("Type expected");
		}
		else
		{
			elementType = ((LighterASTNode) typeMarker).getTokenType();
		}

		if(elementType == ARRAY_TYPE)
		{
			if(builder.getTokenType() == LBRACKET)
			{

			}
		}
		else
		{
			if(builder.getTokenType() == LPAR)
			{
				parseParameterList(builder);
			}

			if(builder.getTokenType() == LBRACE)
			{
				parseFieldOrPropertySetBlock(builder);
			}
		}

		mark.done(NEW_EXPRESSION);
	}

	private static void parseFieldOrPropertySetBlock(CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		while(!builder.eof())
		{
			if(parseFieldOrPropertySet(builder) == null)
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

		expect(builder, RBRACE, "'}' expected");
		mark.done(FIELD_OR_PROPERTY_SET_BLOCK);
	}

	private static PsiBuilder.Marker parseFieldOrPropertySet(CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		if(doneOneElement(builder, IDENTIFIER, REFERENCE_EXPRESSION, "Identifier expected"))
		{
			if(expect(builder, EQ, "'=' expected"))
			{
				if(ExpressionParsing.parse(builder) == null)
				{
					builder.error("Expression expected");
				}
			}
			mark.done(FIELD_OR_PROPERTY_SET);
			return mark;
		}
		else
		{
			mark.drop();
			return null;
		}
	}

	private static void parseTypeOfExpression(CSharpBuilderWrapper builder, PsiBuilder.Marker mark)
	{
		builder.advanceLexer();

		if(expect(builder, LPAR, "'(' expected"))
		{
			if(parseType(builder) == null)
			{
				builder.error("Type expected");
			}
			expect(builder, RPAR, "')' expected");
		}
		mark.done(TYPE_OF_EXPRESSION);
	}

	public static PsiBuilder.Marker parseQualifiedReference(@NotNull PsiBuilder builder, @Nullable PsiBuilder.Marker prevMarker)
	{
		if(prevMarker != null)
		{
			builder.advanceLexer(); // skip dot
		}
		PsiBuilder.Marker marker = prevMarker == null ? builder.mark() : prevMarker;

		if(expect(builder, IDENTIFIER, "Identifier expected"))
		{
			marker.done(REFERENCE_EXPRESSION);

			if(builder.getTokenType() == DOT)
			{
				marker = parseQualifiedReference(builder, marker.precede());
			}
		}
		else
		{
			marker.drop();
			marker = null;
		}

		return marker;
	}
}
