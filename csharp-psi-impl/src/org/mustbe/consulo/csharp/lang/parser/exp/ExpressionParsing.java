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
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public class ExpressionParsing extends SharingParsingHelpers
{
	private static final TokenSet CONSTANTS = TokenSet.create(INTEGER_LITERAL, STRING_LITERAL, DOUBLE_LITERAL, FLOAT_LITERAL, LONG_LITERAL,
			BOOL_LITERAL, NULL_LITERAL);

	@Nullable
	public static PsiBuilder.Marker parse(CSharpBuilderWrapper wrapper)
	{
		PsiBuilder.Marker marker = parsePrimary(wrapper);


		return marker;
	}

	private static PsiBuilder.Marker parsePrimary(CSharpBuilderWrapper wrapper)
	{
		PsiBuilder.Marker mark = wrapper.mark();
		IElementType tokenType = wrapper.getTokenType();
		if(CONSTANTS.contains(tokenType))
		{
			wrapper.advanceLexer();

			mark.done(CONSTANT_EXPRESSION);
		}
		else
		{
			mark.drop();
			mark = null;
		}
		return mark;
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
				parseQualifiedReference(builder, marker.precede());
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
