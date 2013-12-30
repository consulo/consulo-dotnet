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
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.NullableFunction;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public class StatementParsing extends SharingParsingHelpers
{
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
		}, wrapper, VAR_KEYWORD);
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
			/*else if(wrapper.getTokenType() == WHILE_KEYWORD)
			{
				parseStatementWithParenthesesExpression(wrapper, marker, LOCK_STATEMENT);
			}     */
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

		PsiBuilder.Marker typeMarker = parseType(wrapper);
		if(typeMarker == null)
		{
			if(constToken)
			{
				wrapper.error("Type expected");
			}
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
