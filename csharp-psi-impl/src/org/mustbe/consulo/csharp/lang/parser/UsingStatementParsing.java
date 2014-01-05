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

package org.mustbe.consulo.csharp.lang.parser;

import org.mustbe.consulo.csharp.lang.parser.exp.ExpressionParsing;
import com.intellij.lang.PsiBuilder;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class UsingStatementParsing extends SharingParsingHelpers
{
	public static void parseUsingList(PsiBuilder builder, PsiBuilder.Marker marker)
	{
		boolean empty = true;
		while(builder.getTokenType() == USING_KEYWORD)
		{
			parseUsing(builder);

			empty = false;
		}

		if(empty)
		{
			marker.drop();
		}
		else
		{
			marker.done(USING_NAMESPACE_LIST);
		}
	}

	public static void parseUsing(PsiBuilder builder)
	{
		val marker = builder.mark();

		builder.advanceLexer();

		ExpressionParsing.parseQualifiedReference(builder, null);

		expect(builder, SEMICOLON, "';' expected");

		marker.done(USING_NAMESPACE_STATEMENT);
	}
}
