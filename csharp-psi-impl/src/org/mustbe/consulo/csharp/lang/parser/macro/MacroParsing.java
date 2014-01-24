/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.csharp.lang.parser.macro;

import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.psi.CSharpMacroElements;
import org.mustbe.consulo.csharp.lang.psi.CSharpMacroTokens;
import com.intellij.lang.PsiBuilder;
import lombok.val;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class MacroParsing implements CSharpMacroTokens, CSharpMacroElements
{
	public static boolean parse(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		val token = builder.getTokenType();
		if(token == MACRO_DEFINE_KEYWORD)
		{
			builder.advanceLexer();

			if(builder.getTokenType() == MACRO_VALUE)
			{
				builder.advanceLexer();
			}
			else
			{
				builder.error("Identifier expected");
			}
			skipUntilStop(builder);
			mark.done(MACRO_DEFINE);
			return true;
		}
		else if(token == MACRO_IF_KEYWORD)
		{
			PsiBuilder.Marker startMarker = builder.mark();
			builder.advanceLexer();

			PsiBuilder.Marker parse = MacroExpressionParsing.parse(builder);
			if(parse == null)
			{
				builder.error("Expression expected");
			}

			SharingParsingHelpers.expect(builder, MACRO_STOP, null);
			startMarker.done(MACRO_BLOCK_START);

			while(!builder.eof())
			{
				if(builder.getTokenType() == MACRO_ENDIF_KEYWORD)
				{
					break;
				}
				builder.advanceLexer();
			}

			if(builder.getTokenType() == MACRO_ENDIF_KEYWORD)
			{
				PsiBuilder.Marker endIfMarker = builder.mark();
				builder.advanceLexer();
				skipUntilStop(builder);
				endIfMarker.done(MACRO_BLOCK_STOP);
			}
			else
			{
				builder.error("'#endif' expected");
			}

			mark.done(MACRO_BLOCK);

			return true;
		}
		else if(token == MACRO_REGION_KEYWORD)
		{
			PsiBuilder.Marker startMarker = builder.mark();
			builder.advanceLexer();
			skipUntilStop(builder);
			startMarker.done(MACRO_BLOCK_START);

			while(!builder.eof())
			{
				if(builder.getTokenType() == MACRO_ENDREGION_KEYWORD)
				{
					break;
				}
				builder.advanceLexer();
			}

			if(builder.getTokenType() == MACRO_ENDREGION_KEYWORD)
			{
				PsiBuilder.Marker endIfMarker = builder.mark();
				builder.advanceLexer();
				skipUntilStop(builder);
				endIfMarker.done(MACRO_BLOCK_STOP);
			}
			else
			{
				builder.error("'#endregion' expected");
			}

			mark.done(MACRO_BLOCK);

			return true;
		}
		else if(token == MACRO_ENDREGION_KEYWORD)
		{
			builder.advanceLexer();

			builder.error("'#endregion' without '#region'");

			skipUntilStop(builder);
			mark.done(MACRO_BLOCK_STOP);
			return true;
		}
		else if(token == MACRO_ENDIF_KEYWORD)
		{
			builder.advanceLexer();
			builder.error("'#endif' without '#if'");

			skipUntilStop(builder);
			mark.done(MACRO_BLOCK_STOP);
			return true;
		}
		else
		{
			mark.drop();
			return false;
		}
	}

	private static void skipUntilStop(PsiBuilder builder)
	{
		while(!builder.eof())
		{
			if(builder.getTokenType() == MACRO_STOP)
			{
				builder.remapCurrentToken(WHITE_SPACE);
				builder.advanceLexer();
				break;
			}
			else
			{
				builder.advanceLexer();
			}
		}
	}
}
