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

package org.mustbe.consulo.csharp.lang.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.parser.exp.ExpressionParsing;
import org.mustbe.consulo.csharp.lang.psi.CSharpElements;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.NullableFunction;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class SharingParsingHelpers implements CSharpTokenSets, CSharpTokens, CSharpElements
{
	protected static boolean parseTypeList(@NotNull CSharpBuilderWrapper builder)
	{
		boolean empty = true;
		while(!builder.eof())
		{
			PsiBuilder.Marker marker = parseType(builder);
			if(marker == null)
			{
				if(!empty)
				{
					builder.error("Type expected");
				}
				break;
			}

			empty = false;

			if(builder.getTokenType() == COMMA)
			{
				builder.advanceLexer();
			}
			else
			{
				break;
			}
		}
		return empty;
	}

	protected static PsiBuilder.Marker parseType(@NotNull CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker marker = parseInnerType(builder);
		if(marker == null)
		{
			return null;
		}

		if(builder.getTokenType() == LT)
		{
			marker = marker.precede();
			builder.advanceLexer();
			if(parseTypeList(builder))
			{
				builder.error("Type expected");
			}
			expect(builder, GT, "'>' expected");
			marker.done(TYPE_WRAPPER_WITH_TYPE_ARGUMENTS);
		}

		if(builder.getTokenType() == MUL)
		{
			marker = marker.precede();

			builder.advanceLexer();

			marker.done(POINTER_TYPE);
		}

		while(builder.getTokenType() == LBRACKET)
		{
			marker = marker.precede();
			builder.advanceLexer();
			expect(builder, RBRACKET, "']' expected");
			marker.done(ARRAY_TYPE);
		}

		return marker;
	}

	private static PsiBuilder.Marker parseInnerType(@NotNull CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker marker = builder.mark();
		IElementType tokenType = builder.getTokenType();

		if(CSharpTokenSets.PRIMITIVE_TYPES.contains(tokenType))
		{
			builder.advanceLexer();
			marker.done(NATIVE_TYPE);
		}
		else if(builder.getTokenType() == IDENTIFIER)
		{
			ExpressionParsing.parseQualifiedReference(builder, null);
			marker.done(REFERENCE_TYPE);
		}
		else if(builder.getTokenType() == GLOBAL_KEYWORD)
		{
			PsiBuilder.Marker mark = builder.mark();
			builder.advanceLexer();

			if(expect(builder, COLONCOLON, "'::' expected"))
			{
				expect(builder, IDENTIFIER, "Identifier expected");
			}
			mark.done(REFERENCE_EXPRESSION);
			marker.done(REFERENCE_TYPE);
		}
		else
		{
			marker.drop();
			marker = null;
		}
		return marker;
	}

	protected static PsiBuilder.Marker parseModifierList(PsiBuilder builder)
	{
		val marker = builder.mark();

		while(!builder.eof())
		{
			if(MODIFIERS.contains(builder.getTokenType()))
			{
				builder.advanceLexer();
			}
			//TODO [VISTALL] attributes
			else
			{
				break;
			}
		}
		marker.done(MODIFIER_LIST);
		return marker;
	}

	@Nullable
	protected static PsiBuilder.Marker parseWithSoftElements(NullableFunction<CSharpBuilderWrapper, PsiBuilder.Marker> func,
			CSharpBuilderWrapper builderWrapper, IElementType... softs)
	{
		return parseWithSoftElements(func, builderWrapper, TokenSet.create(softs));
	}

	@Nullable
	protected static PsiBuilder.Marker parseWithSoftElements(NullableFunction<CSharpBuilderWrapper, PsiBuilder.Marker> func,
			CSharpBuilderWrapper builderWrapper, TokenSet softs)
	{
		builderWrapper.enableSoftKeywords(softs);
		PsiBuilder.Marker fun = func.fun(builderWrapper);
		builderWrapper.disableSoftKeywords(softs);
		return fun;
	}

	protected static boolean expect(PsiBuilder builder, IElementType elementType, String message)
	{
		if(builder.getTokenType() == elementType)
		{
			builder.advanceLexer();
			return true;
		}
		else
		{
			if(message != null)
			{
				builder.error(message);
			}
			return false;
		}
	}

	protected static boolean doneOneElement(PsiBuilder builder, IElementType elementType, IElementType to, String message)
	{
		PsiBuilder.Marker mark = builder.mark();
		if(expect(builder, elementType, message))
		{
			mark.done(to);
			return true;
		}
		else
		{
			mark.drop();
			return false;
		}
	}
}