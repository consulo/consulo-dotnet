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

package org.mustbe.consulo.csharp.lang.parser.decl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.stmt.StatementParsing;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class MethodParsing extends MemberWithBodyParsing
{
	public static void parseMethodStartAtType(@NotNull CSharpBuilderWrapper builder, @NotNull PsiBuilder.Marker marker)
	{
		if(parseType(builder) != null)
		{
			parseMethodStartAfterType(builder, marker, false);
		}
		else
		{
			builder.error("Name expected");
			marker.done(METHOD_DECLARATION);
		}
	}

	public static void parseMethodStartAfterType(@NotNull CSharpBuilderWrapper builder, @NotNull PsiBuilder.Marker marker, boolean constructor)
	{
		if(constructor)
		{
			expect(builder, IDENTIFIER, "Name expected");
		}
		else
		{
			if(builder.getTokenType() == OPERATOR_KEYWORD)
			{
				builder.advanceLexer();

				IElementType tokenTypeGGLL = builder.getTokenTypeGGLL();
				if(OVERLOADING_OPERATORS.contains(tokenTypeGGLL))
				{
					builder.advanceLexerGGLL();
				}
				else
				{
					builder.error("Operator name expected");
				}
			}
			else
			{
				expect(builder, IDENTIFIER, "Name expected");
			}
		}

		parseMethodStartAfterName(builder, marker, constructor);
	}

	public static void parseMethodStartAfterName(@NotNull CSharpBuilderWrapper builder, @NotNull PsiBuilder.Marker marker, boolean constructor)
	{
		GenericParameterParsing.parseList(builder);

		if(builder.getTokenType() == LPAR)
		{
			parseParameterList(builder);
		}
		else
		{
			builder.error("'(' expected");
		}

		if(constructor)
		{
			//TODO [VISTALL] base calls
		}
		else
		{
			GenericParameterParsing.parseGenericConstraintList(builder);
		}

		if(!expect(builder, SEMICOLON, null))
		{
			if(builder.getTokenType() == LBRACE)
			{
				StatementParsing.parse(builder);
			}
			else
			{
				builder.error("';' expected");
			}
		}

		marker.done(constructor ? CONSTRUCTOR_DECLARATION : METHOD_DECLARATION);
	}


	private static void parseParameterList(CSharpBuilderWrapper builder)
	{
		val mark = builder.mark();

		builder.advanceLexer();

		if(builder.getTokenType() != RPAR)
		{
			while(!builder.eof())
			{
				parseParameter(builder);

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
		mark.done(PARAMETER_LIST);
	}

	private static void parseParameter(CSharpBuilderWrapper builder)
	{
		val mark = builder.mark();

		parseModifierListWithAttributes(builder);

		if(parseType(builder) == null)
		{
			builder.error("Type expected");
		}

		expect(builder, IDENTIFIER, "Name expected");

		mark.done(PARAMETER);
	}
}
