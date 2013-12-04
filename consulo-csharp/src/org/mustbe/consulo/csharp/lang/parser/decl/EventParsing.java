package org.mustbe.consulo.csharp.lang.parser.decl;

import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import com.intellij.lang.PsiBuilder;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public class EventParsing extends MemberWithBodyParsing
{
	public static void parse(CSharpBuilderWrapper builder, PsiBuilder.Marker marker)
	{
		if(parseType(builder) == null)
		{
			builder.error("Type expected");
		}
		else
		{
			if(expect(builder, IDENTIFIER, "Name expected"))
			{
				if(!expect(builder, SEMICOLON, null))
				{
					parseAccessors(builder, EVENT_ACCESSOR, EVENT_ACCESSOR_START);
				}
			}
		}

		marker.done(EVENT_DECLARATION);
	}

}
