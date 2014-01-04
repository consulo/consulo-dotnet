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

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.parser.exp.ExpressionParsing;
import org.mustbe.consulo.csharp.lang.psi.CSharpElements;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesAndCommentsBinder;
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
	public static final WhitespacesAndCommentsBinder GREEDY_RIGHT_EDGE_PROCESSOR = new WhitespacesAndCommentsBinder()
	{
		@Override
		public int getEdgePosition(final List<IElementType> tokens, final boolean atStreamEdge, final TokenTextGetter getter)
		{
			return tokens.size();
		}
	};

	public static class TypeInfo
	{
		public boolean isNative;
		public boolean isParameterized;
		public PsiBuilder.Marker marker;
	}

	protected static boolean parseTypeList(@NotNull CSharpBuilderWrapper builder)
	{
		boolean empty = true;
		while(!builder.eof())
		{
			val marker = parseType(builder);
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

	protected static TypeInfo parseType(@NotNull CSharpBuilderWrapper builder)
	{
		TypeInfo typeInfo = parseInnerType(builder);
		if(typeInfo == null)
		{
			return null;
		}

		PsiBuilder.Marker marker = typeInfo.marker;

		if(builder.getTokenType() == LT)
		{
			typeInfo = new TypeInfo();
			typeInfo.isParameterized = true;

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
			typeInfo = new TypeInfo();

			marker = marker.precede();

			builder.advanceLexer();

			marker.done(POINTER_TYPE);
		}

		while(builder.getTokenType() == LBRACKET)
		{
			typeInfo = new TypeInfo();

			marker = marker.precede();
			builder.advanceLexer();
			expect(builder, RBRACKET, "']' expected");
			marker.done(ARRAY_TYPE);
		}

		typeInfo.marker = marker;
		return typeInfo;
	}

	private static TypeInfo parseInnerType(@NotNull CSharpBuilderWrapper builder)
	{
		TypeInfo typeInfo = new TypeInfo();

		PsiBuilder.Marker marker = builder.mark();
		IElementType tokenType = builder.getTokenType();

		typeInfo.marker = marker;
		if(CSharpTokenSets.NATIVE_TYPES.contains(tokenType))
		{
			builder.advanceLexer();
			marker.done(NATIVE_TYPE);

			typeInfo.isNative = true;
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
			return null;
		}

		return typeInfo;
	}

	protected static boolean parseAttributeList(CSharpBuilderWrapper builder)
	{
		boolean empty = true;
		while(builder.getTokenType() == LBRACKET)
		{
			empty = false;
			PsiBuilder.Marker mark = builder.mark();
			builder.advanceLexer();

			builder.enableSoftKeywords(ATTRIBUTE_TARGETS);
			IElementType tokenType = builder.getTokenType();
			builder.disableSoftKeywords(ATTRIBUTE_TARGETS);

			if(builder.lookAhead(1) != COLON)
			{
				builder.remapBackIfSoft();
			}
			else
			{
				if(!ATTRIBUTE_TARGETS.contains(tokenType))
				{
					builder.error("Wrong attribute target");
				}
				builder.advanceLexer(); // target type
				builder.advanceLexer(); // colon
			}

			while(!builder.eof())
			{
				PsiBuilder.Marker attMark = parseAttribute(builder);
				if(attMark == null)
				{
					builder.error("Attribute name expected");
					break;
				}

				if(builder.getTokenType() == COMMA)
				{
					builder.advanceLexer();
				}
				else if(builder.getTokenType() == RBRACKET)
				{
					break;
				}
			}

			expect(builder, RBRACKET, "']' expected");
			mark.done(ATTRIBUTE_LIST);
		}
		return empty;
	}

	private static PsiBuilder.Marker parseAttribute(CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker mark = builder.mark();
		if(ExpressionParsing.parseQualifiedReference(builder, null) == null)
		{
			mark.drop();
			return null;
		}

		if(builder.getTokenType() == LPAR)
		{
			parseAttributeParameterList(builder); //TODO [VISTALL] currently is bad due it cant be parsed expression like [A(t=true)]
		}
		mark.done(ATTRIBUTE);
		return mark;
	}

	public static void parseAttributeParameterList(CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		if(builder.getTokenType() == RPAR)
		{
			builder.advanceLexer();
			mark.done(METHOD_CALL_PARAMETER_LIST);
			return;
		}

		boolean empty = true;
		while(!builder.eof())
		{
			PsiBuilder.Marker marker = ExpressionParsing.parse(builder);
			if(marker == null)
			{
				if(!empty)
				{
					builder.error("Expression expected");
				}
				break;
			}

			empty = false;

			if(builder.getTokenType() == COMMA)
			{
				builder.advanceLexer();
			}
			else if(builder.getTokenType() == RPAR)
			{
				break;
			}
			else
			{
				break;
			}
		}
		expect(builder, RPAR, "')' expected");
		mark.done(METHOD_CALL_PARAMETER_LIST);
	}

	protected static PsiBuilder.Marker parseModifierList(CSharpBuilderWrapper builder)
	{
		val marker = builder.mark();

		while(!builder.eof())
		{
			if(MODIFIERS.contains(builder.getTokenType()))
			{
				builder.advanceLexer();
			}
			else
			{
				break;
			}
		}
		marker.done(MODIFIER_LIST);
		return marker;
	}

	protected static PsiBuilder.Marker parseModifierListWithAttributes(CSharpBuilderWrapper builder)
	{
		if(MODIFIERS.contains(builder.getTokenType()))
		{
			return parseModifierList(builder);
		}
		else
		{
			val marker = builder.mark();
			parseAttributeList(builder);
			while(!builder.eof())
			{
				if(MODIFIERS.contains(builder.getTokenType()))
				{
					builder.advanceLexer();
				}
				else
				{
					break;
				}
			}
			marker.done(MODIFIER_LIST);
			return marker;
		}
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

	@Nullable
	public static IElementType exprType(@Nullable final PsiBuilder.Marker marker)
	{
		return marker != null ? ((LighterASTNode) marker).getTokenType() : null;
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

	protected static boolean expect(PsiBuilder builder, TokenSet tokenSet, String message)
	{
		if(tokenSet.contains(builder.getTokenType()))
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

	public static void emptyElement(final PsiBuilder builder, final IElementType type)
	{
		builder.mark().done(type);
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
