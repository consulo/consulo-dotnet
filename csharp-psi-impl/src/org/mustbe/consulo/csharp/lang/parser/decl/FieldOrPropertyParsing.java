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
import org.mustbe.consulo.csharp.lang.parser.exp.ExpressionParsing;
import com.intellij.lang.PsiBuilder;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class FieldOrPropertyParsing extends MemberWithBodyParsing
{
	public static void parseFieldBeforeName(CSharpBuilderWrapper builder, PsiBuilder.Marker marker)
	{
		if(parseType(builder) == null)
		{
			builder.error("Type expected");
		}
		else
		{
			if(expect(builder, IDENTIFIER, "Name expected"))
			{
				parseInitializer(builder);
			}
		}

		marker.done(FIELD_DECLARATION);
	}

	private static void parseInitializer(CSharpBuilderWrapper builder)
	{
		if(!expect(builder, SEMICOLON, null))
		{
			if(expect(builder, EQ, "'=' expected"))
			{
				PsiBuilder.Marker parse = ExpressionParsing.parse(builder);
				if(parse == null)
				{
					builder.error("Expression expected");
				}
				else
				{
					expect(builder, SEMICOLON, "';' expected");
				}
			}
		}
	}

	public static void parseFieldOrPropertyAfterName(CSharpBuilderWrapper builderWrapper, PsiBuilder.Marker marker)
	{
		if(builderWrapper.getTokenType() == LBRACE)
		{
			parseAccessors(builderWrapper, PROPERTY_ACCESSOR, PROPERTY_ACCESSOR_START);

			marker.done(PROPERTY_DECLARATION);
		}
		else
		{
			parseInitializer(builderWrapper);

			marker.done(FIELD_DECLARATION);
		}
	}
}
