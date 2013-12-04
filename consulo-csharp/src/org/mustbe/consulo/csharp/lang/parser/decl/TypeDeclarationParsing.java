package org.mustbe.consulo.csharp.lang.parser.decl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.parser.stmt.UsingStatementParsing;
import com.intellij.lang.PsiBuilder;
import com.intellij.util.NotNullFunction;

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

		if(builder.getTokenType() == COLON)
		{
			parseWithSoftElements(new NotNullFunction<CSharpBuilderWrapper, PsiBuilder.Marker>()
			{
				@NotNull
				@Override
				public PsiBuilder.Marker fun(CSharpBuilderWrapper builderWrapper)
				{
					PsiBuilder.Marker mark = builderWrapper.mark();
					builderWrapper.advanceLexer();  // colon

					parseTypeList(builderWrapper);
					mark.done(EXTENDS_LIST);
					return mark;
				}
			}, builder, GLOBAL_KEYWORD);
		}

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
