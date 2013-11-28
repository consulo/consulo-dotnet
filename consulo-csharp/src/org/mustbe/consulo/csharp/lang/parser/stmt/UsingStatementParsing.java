package org.mustbe.consulo.csharp.lang.parser.stmt;

import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.parser.exp.ExpressionParsing;
import com.intellij.lang.PsiBuilder;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class UsingStatementParsing extends SharingParsingHelpers
{
	public static void parseUsingList(PsiBuilder builder)
	{
		val m = builder.mark();

		while(builder.getTokenType() == USING_KEYWORD)
		{
			parseUsing(builder);
		}

		m.done(USING_LIST);
	}

	public static void parseUsing(PsiBuilder builder)
	{
		val marker = builder.mark();

		builder.advanceLexer();

		ExpressionParsing.parseQualifiedReference(builder, null);

		expect(builder, SEMICOLON, "';' expected");

		marker.done(USING_STATEMENT);
	}
}
