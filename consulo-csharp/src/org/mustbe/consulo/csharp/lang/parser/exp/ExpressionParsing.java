package org.mustbe.consulo.csharp.lang.parser.exp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import com.intellij.lang.PsiBuilder;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class ExpressionParsing extends SharingParsingHelpers
{
	public static PsiBuilder.Marker parseQualifiedReference(@NotNull PsiBuilder builder, @Nullable PsiBuilder.Marker prevMarker)
	{
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
