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
				if(!expect(builder, SEMICOLON, null))
				{
					builder.error("Expression expected");
				}
			}
		}

		marker.done(FIELD_DECLARATION);
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
			if(builderWrapper.getTokenType() == SEMICOLON)
			{
				builderWrapper.advanceLexer();
			}
			else
			{
				builderWrapper.error("Unknown how parse " + builderWrapper.getTokenType());
				builderWrapper.advanceLexer();
			}
			marker.done(FIELD_DECLARATION);
		}
	}
}
