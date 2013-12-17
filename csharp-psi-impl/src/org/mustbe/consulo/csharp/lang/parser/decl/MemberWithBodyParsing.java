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

import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.parser.stmt.StatementParsing;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public class MemberWithBodyParsing extends SharingParsingHelpers
{
	protected static void parseAccessors(CSharpBuilderWrapper builder, IElementType to, TokenSet tokenSet)
	{
		if(expect(builder, LBRACE, "'{' expected"))
		{
			while(!builder.eof())
			{
				if(parseAccessor(builder, to, tokenSet) == null)
				{
					break;
				}
			}

			expect(builder, RBRACE, "'}' expected");
		}
	}

	protected static PsiBuilder.Marker parseAccessor(CSharpBuilderWrapper builder, IElementType to, TokenSet tokenSet)
	{
		PsiBuilder.Marker marker = builder.mark();

		parseModifierList(builder);

		builder.enableSoftKeywords(tokenSet);
		boolean contains = tokenSet.contains(builder.getTokenType());
		builder.disableSoftKeywords(tokenSet);

		if(contains)
		{
			builder.advanceLexer();

			if(builder.getTokenType() == LBRACE)
			{
				parseCodeBlock(builder);
			}
			else
			{
				expect(builder, SEMICOLON, "';' expected");
			}

			marker.done(to);
		}
		else
		{
			marker.drop();
			marker = null;
		}
		return marker;
	}

	public static void parseCodeBlock(CSharpBuilderWrapper builder)
	{
		if(builder.getTokenType() == LBRACE)
		{
			PsiBuilder.Marker mark = builder.mark();
			builder.advanceLexer();

			while(!builder.eof())
			{
				if(builder.getTokenType() == RBRACE)
				{
					break;
				}
				else
				{
					PsiBuilder.Marker marker = StatementParsing.parse(builder);
					if(marker == null)
					{
						break;
					}
				}
			}

			expect(builder, RBRACE, "'}' expected");
			mark.done(CODE_BLOCK);
		}
		else
		{
			builder.error("'{' expected");
		}
	}
}