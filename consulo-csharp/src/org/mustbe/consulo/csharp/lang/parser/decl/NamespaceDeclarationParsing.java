package org.mustbe.consulo.csharp.lang.parser.decl;

import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.parser.exp.ExpressionParsing;
import org.mustbe.consulo.csharp.lang.parser.stmt.UsingStatementParsing;
import com.intellij.lang.PsiBuilder;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class NamespaceDeclarationParsing extends SharingParsingHelpers
{
	public static void parse(CSharpBuilderWrapper builder, PsiBuilder.Marker marker)
	{
		builder.advanceLexer();

		if(ExpressionParsing.parseQualifiedReference(builder, null) == null)
		{
			builder.error("Name expected");
		}

		if(expect(builder, LBRACE, "'{' expected"))
		{
			UsingStatementParsing.parseUsingList(builder);

			while(!builder.eof() && builder.getTokenType() != RBRACE)
			{
				if(!DeclarationParsing.parse(builder, true))
				{
					break;
				}
			}

			expect(builder, RBRACE, "'}' expected");
		}

		marker.done(NAMESPACE_DECLARATION);
	}

}
