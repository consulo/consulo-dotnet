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
	public static void parseFieldOrLocalVariableAtTypeWithDone(CSharpBuilderWrapper builder, PsiBuilder.Marker marker, boolean local)
	{
		if(parseType(builder) == null)
		{
			builder.error("Type expected");

			marker.done(local ? LOCAL_VARIABLE : FIELD_DECLARATION);
		}
		else
		{
			parseFieldOrLocalVariableAtNameWithDone(builder, marker, local);
		}
	}

	public static PsiBuilder.Marker parseFieldOrLocalVariableAtTypeWithRollback(CSharpBuilderWrapper builder, PsiBuilder.Marker marker,
			boolean local)
	{
		if(parseType(builder) == null)
		{
			builder.error("Type expected");

			marker.rollbackTo();
			return null;
		}
		else
		{
			return parseFieldOrLocalVariableAtNameWithRollback(builder, marker, local);
		}
	}

	public static void parseFieldOrLocalVariableAtNameWithDone(CSharpBuilderWrapper builder, PsiBuilder.Marker marker, boolean local)
	{
		if(builder.getTokenType() == IDENTIFIER)
		{
			builder.advanceLexer();

			parseFieldAfterName(builder, marker, local);
		}
		else
		{
			builder.error("Name expected");

			marker.done(local ? LOCAL_VARIABLE : FIELD_DECLARATION);
		}
	}

	public static PsiBuilder.Marker parseFieldOrLocalVariableAtNameWithRollback(CSharpBuilderWrapper builder, PsiBuilder.Marker marker,
			boolean local)
	{
		if(builder.getTokenType() == IDENTIFIER)
		{
			builder.advanceLexer();

			return parseFieldAfterName(builder, marker, local);
		}
		else
		{
			builder.error("Name expected");
			marker.rollbackTo();
			return null;
		}
	}

	private static PsiBuilder.Marker parseFieldAfterName(CSharpBuilderWrapper builder, PsiBuilder.Marker marker, boolean local)
	{
		if(builder.getTokenType() == EQ)
		{
			builder.advanceLexer();
			if(ExpressionParsing.parse(builder) == null)
			{
				builder.error("Expression expected");
			}
		}

		if(builder.getTokenType() == COMMA)
		{
			marker.done(local ? LOCAL_VARIABLE : FIELD_DECLARATION);

			builder.advanceLexer();

			PsiBuilder.Marker newMarker = builder.mark();

			parseFieldOrLocalVariableAtNameWithDone(builder, newMarker, local);

			return marker;
		}
		else
		{
			expect(builder, SEMICOLON, "';' expected");

			marker.done(local ? LOCAL_VARIABLE : FIELD_DECLARATION);

			return marker;
		}
	}

	public static void parseFieldOrPropertyAfterName(CSharpBuilderWrapper builderWrapper, PsiBuilder.Marker marker)
	{
		if(builderWrapper.getTokenType() == LBRACE)
		{
			parseAccessors(builderWrapper, XXX_ACCESSOR, PROPERTY_ACCESSOR_START);

			marker.done(PROPERTY_DECLARATION);
		}
		else
		{
			parseFieldAfterName(builderWrapper, marker, false);
		}
	}
}
