package org.mustbe.consulo.csharp.lang.parser.decl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import com.intellij.lang.PsiBuilder;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class MethodParsing extends MemberWithBodyParsing
{
	public static void parseMethodStartAtType(@NotNull CSharpBuilderWrapper builder, @NotNull PsiBuilder.Marker marker)
	{
		if(parseType(builder) != null)
		{
			parseMethodStartAfterType(builder, marker, false);
		}
		else
		{
			builder.error("Name expected");
			marker.done(METHOD_DECLARATION);
		}
	}

	public static void parseMethodStartAfterType(@NotNull CSharpBuilderWrapper builder, @NotNull PsiBuilder.Marker marker, boolean constructor)
	{
		expect(builder, IDENTIFIER, "Name expected");

		parseMethodStartAfterName(builder, marker, constructor);
	}

	public static void parseMethodStartAfterName(@NotNull CSharpBuilderWrapper builder, @NotNull PsiBuilder.Marker marker, boolean constructor)
	{
		GenericParameterParsing.parseList(builder);

		if(builder.getTokenType() == LPAR)
		{
			parseParameterList(builder);
		}
		else
		{
			builder.error("'(' expected");
		}

		if(constructor)
		{
			//TODO [VISTALL] base calls
		}
		else
		{
			GenericParameterParsing.parseGenericConstraintList(builder);
		}

		if(!expect(builder, SEMICOLON, null))
		{
			if(builder.getTokenType() == LBRACE)
			{
				parseCodeBlock(builder);
			}
		}

		marker.done(constructor ? CONSTRUCTOR_DECLARATION : METHOD_DECLARATION);
	}


	private static void parseParameterList(CSharpBuilderWrapper builder)
	{
		val mark = builder.mark();

		builder.advanceLexer();

		if(builder.getTokenType() != RPAR)
		{
			while(!builder.eof())
			{
				parseParameter(builder);

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

		expect(builder, RPAR, "')' expected");
		mark.done(PARAMETER_LIST);
	}

	private static void parseParameter(CSharpBuilderWrapper builder)
	{
		val mark = builder.mark();

		parseModifierList(builder);

		if(parseType(builder) == null)
		{
			builder.error("Type expected");
		}

		expect(builder, IDENTIFIER, "Name expected");

		mark.done(PARAMETER);
	}
}
