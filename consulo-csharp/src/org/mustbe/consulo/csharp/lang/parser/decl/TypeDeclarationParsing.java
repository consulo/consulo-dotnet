package org.mustbe.consulo.csharp.lang.parser.decl;

import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.parser.stmt.UsingStatementParsing;
import com.intellij.lang.PsiBuilder;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class TypeDeclarationParsing extends SharingParsingHelpers
{
	public static void parse(CSharpBuilderWrapper builder, PsiBuilder.Marker marker)
	{
		builder.advanceLexer();

		expect(builder, IDENTIFIER, "Name expected");

		GenericParameterParsing.parseList(builder);

		//TODO [VISTALL] extend list

		GenericParameterParsing.parseGenericConstraintList(builder);

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

		marker.done(TYPE_DECLARATION);
	}
}
