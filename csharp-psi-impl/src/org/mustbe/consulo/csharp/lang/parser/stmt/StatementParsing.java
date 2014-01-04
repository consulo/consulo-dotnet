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

package org.mustbe.consulo.csharp.lang.parser.stmt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.parser.exp.ExpressionParsing;
import org.mustbe.consulo.csharp.lang.parser.exp.LinqParsing;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.NullableFunction;
import lombok.val;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public class StatementParsing extends SharingParsingHelpers
{
	private static final TokenSet BODY_SOFT_KEYWORDS = TokenSet.orSet(TokenSet.create(VAR_KEYWORD, YIELD_KEYWORD), LinqParsing.LINQ_KEYWORDS);

	public static PsiBuilder.Marker parse(CSharpBuilderWrapper wrapper)
	{
		return parseWithSoftElements(new NullableFunction<CSharpBuilderWrapper, PsiBuilder.Marker>()
		{
			@Nullable
			@Override
			public PsiBuilder.Marker fun(CSharpBuilderWrapper builderWrapper)
			{
				return parseStatement(builderWrapper);
			}
		}, wrapper, BODY_SOFT_KEYWORDS);
	}

	private static PsiBuilder.Marker parseStatement(CSharpBuilderWrapper wrapper)
	{
		IElementType tokenType = wrapper.getTokenType();
		PsiBuilder.Marker marker = parseVariableDecl(wrapper, tokenType == CONST_KEYWORD);
		if(marker == null)
		{
			marker = wrapper.mark();

			tokenType = wrapper.getTokenType();

			if(tokenType == LOCK_KEYWORD)
			{
				parseStatementWithParenthesesExpression(wrapper, marker, LOCK_STATEMENT);
			}
			else if(tokenType == BREAK_KEYWORD)
			{
				wrapper.advanceLexer();

				expect(wrapper, SEMICOLON, "';' expected");

				marker.done(BREAK_STATEMENT);
			}
			else if(tokenType == CONTINUE_KEYWORD)
			{
				wrapper.advanceLexer();

				expect(wrapper, SEMICOLON, "';' expected");

				marker.done(CONTINUE_STATEMENT);
			}
			else if(tokenType == RETURN_KEYWORD)
			{
				parseReturnStatement(wrapper, marker);
			}
			else if(tokenType == FOREACH_KEYWORD)
			{
				parseForeach(wrapper, marker);
			}
			else if(tokenType == YIELD_KEYWORD)
			{
				parseYieldStatement(wrapper, marker);
			}
			else if(tokenType == IF_KEYWORD)
			{
				parseIfStatement(wrapper, marker);
			}
			else if(tokenType == LBRACE)
			{
				parseBlockStatement(wrapper, marker);
			}
			else if(wrapper.getTokenType() == WHILE_KEYWORD)
			{
				parseStatementWithParenthesesExpression(wrapper, marker, WHILE_STATEMENT);
			}
			else if(ExpressionParsing.parse(wrapper) != null)
			{
				expect(wrapper, SEMICOLON, "';' expected");

				marker.done(EXPRESSION_STATEMENT);
			}
			else
			{
				wrapper.error("Unknown how parse: " + wrapper.getTokenType());
				wrapper.advanceLexer();

				marker.drop();
			}
			return marker;
		}
		else
		{
			marker = marker.precede();

			marker.done(LOCAL_VARIABLE_DECLARATION_STATEMENT);
		}

		return marker;
	}

	@NotNull
	private static PsiBuilder.Marker parseIfStatement(final CSharpBuilderWrapper builder, final PsiBuilder.Marker mark)
	{
		builder.advanceLexer();

		if(!parseExpressionInParenth(builder))
		{
			mark.done(IF_STATEMENT);
			return mark;
		}

		val thenStatement = parseStatement(builder);
		if(thenStatement == null)
		{
			builder.error("Expected statement");
			mark.done(IF_STATEMENT);
			return mark;
		}

		if(!expect(builder, ELSE_KEYWORD, null))
		{
			mark.done(IF_STATEMENT);
			return mark;
		}

		val elseStatement = parseStatement(builder);
		if(elseStatement == null)
		{
			builder.error("Expected statement");
		}

		mark.done(IF_STATEMENT);
		return mark;
	}

	private static void parseForeach(CSharpBuilderWrapper builder, PsiBuilder.Marker marker)
	{
		assert builder.getTokenType() == FOREACH_KEYWORD;

		builder.advanceLexer();

		if(expect(builder, LPAR, "'(' expected"))
		{
			PsiBuilder.Marker varMarker = builder.mark();

			if(parseType(builder) != null)
			{
				expect(builder, IDENTIFIER, "Identifier expected");
			}
			else
			{
				builder.error("Type expected");
			}

			varMarker.done(LOCAL_VARIABLE);

			if(expect(builder, IN_KEYWORD, "'in' expected"))
			{
				if(ExpressionParsing.parse(builder) == null)
				{
					builder.error("Expression expected");
				}
			}

			expect(builder, RPAR, "')' expected");
		}

		if(builder.getTokenType() == SEMICOLON)
		{
			builder.advanceLexer();
		}
		else
		{
			StatementParsing.parse(builder);
		}

		marker.done(FOREACH_STATEMENT);
	}

	private static PsiBuilder.Marker parseBlockStatement(CSharpBuilderWrapper builder, PsiBuilder.Marker marker)
	{
		if(builder.getTokenType() == LBRACE)
		{
			builder.advanceLexer();

			while(!builder.eof())
			{
				if(builder.getTokenType() == RBRACE)
				{
					break;
				}
				else
				{
					PsiBuilder.Marker anotherMarker = parse(builder);
					if(anotherMarker == null)
					{
						break;
					}
				}
			}

			expect(builder, RBRACE, "'}' expected");
			marker.done(BLOCK_STATEMENT);
			return marker;
		}
		else
		{
			builder.error("'{' expected");
			return null;
		}
	}

	private static boolean parseExpressionInParenth(final CSharpBuilderWrapper builder)
	{
		if(!expect(builder, LPAR, "'(' expected"))
		{
			return false;
		}

		final PsiBuilder.Marker beforeExpr = builder.mark();
		final PsiBuilder.Marker expr = ExpressionParsing.parse(builder);
		if(expr == null || builder.getTokenType() == SEMICOLON)
		{
			beforeExpr.rollbackTo();
			builder.error("Expression expected");
			if(builder.getTokenType() != RPAR)
			{
				return false;
			}
		}
		else
		{
			beforeExpr.drop();
			if(builder.getTokenType() != RPAR)
			{
				builder.error("')' expected");
				return false;
			}
		}

		builder.advanceLexer();
		return true;
	}

	private static void parseYieldStatement(CSharpBuilderWrapper wrapper, PsiBuilder.Marker marker)
	{
		assert wrapper.getTokenType() == YIELD_KEYWORD;

		wrapper.advanceLexer();

		if(wrapper.getTokenType() == BREAK_KEYWORD)
		{
			PsiBuilder.Marker mark = wrapper.mark();
			wrapper.advanceLexer();
			mark.done(BREAK_STATEMENT);
		}
		else if(wrapper.getTokenType() == RETURN_KEYWORD)
		{
			PsiBuilder.Marker mark = wrapper.mark();
			wrapper.advanceLexer();
			ExpressionParsing.parse(wrapper);
			mark.done(RETURN_STATEMENT);
		}
		else
		{
			wrapper.error("'break' or 'return' expected");
		}

		expect(wrapper, SEMICOLON, "';' expected");

		marker.done(YIELD_STATEMENT);
	}

	private static void parseReturnStatement(CSharpBuilderWrapper wrapper, PsiBuilder.Marker marker)
	{
		assert wrapper.getTokenType() == RETURN_KEYWORD;

		wrapper.advanceLexer();

		ExpressionParsing.parse(wrapper);

		expect(wrapper, SEMICOLON, "';' expected");

		marker.done(RETURN_STATEMENT);
	}

	private static void parseStatementWithParenthesesExpression(CSharpBuilderWrapper wrapper, PsiBuilder.Marker marker, IElementType doneElement)
	{
		wrapper.advanceLexer();

		parseExpressionInParenth(wrapper);

		StatementParsing.parse(wrapper);

		expect(wrapper, SEMICOLON, null);

		marker.done(doneElement);
	}

	private static PsiBuilder.Marker parseVariableDecl(CSharpBuilderWrapper wrapper, boolean constToken)
	{
		PsiBuilder.Marker mark = wrapper.mark();

		if(constToken)
		{
			wrapper.advanceLexer();
		}

		val typeInfo = parseType(wrapper);
		if(typeInfo == null)
		{
			if(constToken)
			{
				wrapper.error("Type expected");
			}
			mark.rollbackTo();

			return null;
		}

		if(wrapper.getTokenType() == DOT && typeInfo.isNative)
		{
			mark.rollbackTo();

			return null;
		}

		if(wrapper.getTokenType() == IDENTIFIER)
		{
			wrapper.advanceLexer();

			if(!expect(wrapper, SEMICOLON, null))
			{
				if(expect(wrapper, EQ, "'=' expected"))
				{
					PsiBuilder.Marker parse = ExpressionParsing.parse(wrapper);
					if(parse == null)
					{
						wrapper.error("Expression expected");
					}
					else
					{
						expect(wrapper, SEMICOLON, "';' expected");
					}
				}
			}
			mark.done(LOCAL_VARIABLE);
			return mark;
		}

		mark.rollbackTo();
		return null;
	}
}
