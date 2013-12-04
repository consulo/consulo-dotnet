package org.mustbe.consulo.csharp.lang.parser.decl;

import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 04.12.13.
 */
public class MemberWithBodyParsing extends SharingParsingHelpers
{
	protected static void parseAccessors(CSharpBuilderWrapper builder, IElementType to, TokenSet tokenSet)
	{
		if(expect(builder, LBRACE, "'{' expected"))
		{
			while(!builder.eof())
			{
				if(parseAccessor(builder, to, tokenSet) == null)
				{
					break;
				}
			}

			expect(builder, RBRACE, "'}' expected");
		}
	}

	protected static PsiBuilder.Marker parseAccessor(CSharpBuilderWrapper builder, IElementType to, TokenSet tokenSet)
	{
		PsiBuilder.Marker marker = builder.mark();

		parseModifierList(builder);

		builder.enableSoftKeywords(tokenSet);
		boolean contains = tokenSet.contains(builder.getTokenType());
		builder.disableSoftKeywords(tokenSet);

		if(contains)
		{
			builder.advanceLexer();

			if(builder.getTokenType() == LBRACE)
			{
				parseCodeBlock(builder);
			}
			else
			{
				expect(builder, SEMICOLON, "';' expected");
			}

			marker.done(to);
		}
		else
		{
			marker.drop();
			marker = null;
		}
		return marker;
	}

	public static void parseCodeBlock(CSharpBuilderWrapper builder)
	{
		if(builder.getTokenType() == LBRACE)
		{
			PsiBuilder.Marker mark = builder.mark();
			builder.advanceLexer();

			//TODO [VISTALL] temp code

			int brace = 0;

			while(!builder.eof())
			{
				IElementType tokenType = builder.getTokenType();
				if(tokenType == LBRACE)
				{
					brace ++;
					builder.advanceLexer();
				}
				else if(tokenType == RBRACE)
				{
					brace --;
					if(brace < 0)
					{
						break;
					}
					builder.advanceLexer();
				}
				else
				{
					builder.advanceLexer();
				}
			}

			expect(builder, RBRACE, "'}' expected");
			mark.done(CODE_BLOCK);
		}
		else
		{
			builder.error("'{' expected");
		}
	}
}
