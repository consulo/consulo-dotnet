package org.mustbe.consulo.csharp.lang.parser.decl;

import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.NullableFunction;
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

	public static PsiBuilder.Marker parseGenericConstraintList(CSharpBuilderWrapper builder)
	{
		val marker = builder.mark();

		boolean empty = true;
		while(true)
		{
			val constraintMarker = parseWithSoftElements(new NullableFunction<CSharpBuilderWrapper, PsiBuilder.Marker>()
			{
				@Override
				public PsiBuilder.Marker fun(CSharpBuilderWrapper builderWrapper)
				{
					return parseGenericConstraint(builderWrapper);
				}
			}, builder, WHERE_KEYWORD);

			if(constraintMarker == null)
			{
				break;
			}
			else
			{
				empty = false;
			}
		}

		if(empty)
		{
			marker.drop();
		}
		else
		{
			marker.done(GENERIC_CONSTRAINT_LIST);
		}
		return marker;
	}

	private static PsiBuilder.Marker parseGenericConstraint(CSharpBuilderWrapper builder)
	{
		if(builder.getTokenType() != WHERE_KEYWORD)
		{
			return null;
		}

		PsiBuilder.Marker marker = builder.mark();

		builder.advanceLexer();

		doneOneElement(builder, IDENTIFIER, REFERENCE_EXPRESSION, "Identifier expected");
		expect(builder, COLON, "Identifier expected");

		PsiBuilder.Marker value = builder.mark();
		IElementType doneElement = null;
		if(builder.getTokenType() == NEW_KEYWORD)
		{
			builder.advanceLexer();
			expect(builder, LPAR, "'(' expected");
			expect(builder, RPAR, "')' expected");

			doneElement = NEW_GENERIC_CONSTRAINT_VALUE;
		}

		if(doneElement == null)
		{
			value.drop();
		}
		else
		{
			value.done(doneElement);
		}

		marker.done(GENERIC_CONSTRAINT);
		return marker;
	}
}
