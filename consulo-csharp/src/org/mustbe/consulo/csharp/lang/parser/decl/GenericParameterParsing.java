package org.mustbe.consulo.csharp.lang.parser.decl;

import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import com.intellij.lang.PsiBuilder;
import lombok.val;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public class GenericParameterParsing extends SharingParsingHelpers
{
	public static void parseList(CSharpBuilderWrapper builder)
	{
		if(builder.getTokenType() != LT)
		{
			return;
		}

		PsiBuilder.Marker mark = builder.mark();
		builder.advanceLexer();

		while(true)
		{
			parse(builder);

			if(builder.getTokenType() == COMMA)
			{
				builder.advanceLexer();
			}
			else
			{
				break;
			}
		}

		expect(builder, GT, "'>' expected");

		mark.done(GENERIC_PARAMETER_LIST);
	}

	public static void parse(CSharpBuilderWrapper builder)
	{
		val marker = builder.mark();

		parseModifierList(builder);

		expect(builder, IDENTIFIER, "Name expected");

		marker.done(GENERIC_PARAMETER);
	}
}
