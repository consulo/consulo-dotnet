package org.mustbe.consulo.csharp.lang.parser;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.parser.exp.ExpressionParsing;
import org.mustbe.consulo.csharp.lang.psi.CSharpElements;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class SharingParsingHelpers implements CSharpTokenSets, CSharpTokens, CSharpElements
{
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
}
