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

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.parser.decl.MemberWithBodyParsing;
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
				return parse0(builderWrapper);
			}
		}, wrapper, BODY_SOFT_KEYWORDS);
	}

	private static PsiBuilder.Marker parse0(CSharpBuilderWrapper wrapper)
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
			else if(tokenType == YIELD_KEYWORD)
			{
				parseYieldStatement(wrapper, marker);
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

		ExpressionParsing.parseParenthesesExpression(wrapper);

		MemberWithBodyParsing.parseCodeBlock(wrapper);

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
