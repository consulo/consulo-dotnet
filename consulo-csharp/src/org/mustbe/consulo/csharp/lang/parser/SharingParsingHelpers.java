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
	protected static void parseTypeList(@NotNull CSharpBuilderWrapper builder)
	{
		while(!builder.eof())
		{
			PsiBuilder.Marker marker = parseType(builder);
			if(marker == null)
			{
				break;
			}

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

	protected static PsiBuilder.Marker parseType(@NotNull CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker marker = builder.mark();
		IElementType tokenType = builder.getTokenType();
		if(CSharpTokenSets.NATIVE_TYPES.contains(tokenType))
		{
			builder.advanceLexer();
		}
		else if(builder.getTokenType() == IDENTIFIER)
		{
			ExpressionParsing.parseQualifiedReference(builder, null);
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
		}
		else
		{
			marker.drop();
			marker = null;
		}

		if(marker != null)
		{
			marker.done(TYPE);
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
