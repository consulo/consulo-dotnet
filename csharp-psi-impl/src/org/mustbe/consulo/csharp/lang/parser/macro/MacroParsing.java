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

import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import com.intellij.lang.PsiBuilder;
import lombok.val;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class MacroParsing extends SharingParsingHelpers
{
	public static boolean parse(CSharpBuilderWrapper builder, MacroesInfo macroesInfo)
	{
		PsiBuilder.Marker mark = builder.mark();

		val token = builder.getTokenType();
		if(token == MACRO_DEFINE_KEYWORD)
		{
			builder.advanceLexer();

			if(builder.getTokenType() == MACRO_VALUE)
			{
				String tokenText = builder.getTokenText();
				macroesInfo.define(tokenText);
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
			val currentOffset = builder.getCurrentOffset();

			builder.advanceLexer();

			if(builder.getTokenType() == MACRO_VALUE)
			{
				String tokenText = builder.getTokenText();
				builder.advanceLexer();
				if(macroesInfo.isDefined(tokenText))
				{
					macroesInfo.addActiveBlock(MACRO_IF_KEYWORD, currentOffset);

					skipUntilStop(builder);
					mark.done(MACRO_BLOCK_START);
				}
				else
				{
					skipUntilStop(builder);
					mark.done(MACRO_BLOCK_START);

					mark = mark.precede();

					PsiBuilder.Marker marker = builder.mark();
					while(!builder.eof())
					{
						if(builder.getTokenType() == MACRO_ENDIF_KEYWORD)
						{
							break;
						}
						builder.advanceLexer();
					}
					marker.done(MACRO_BODY);

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
				}
			}
			else
			{
				builder.error("Identifier expected");
				skipUntilStop(builder);
				mark.done(MACRO_BLOCK_START);
			}
			return true;
		}
		else if(token == MACRO_REGION_KEYWORD)
		{
			val currentOffset = builder.getCurrentOffset();

			builder.advanceLexer();

			macroesInfo.addActiveBlock(MACRO_REGION_KEYWORD, currentOffset);

			skipUntilStop(builder);
			mark.done(MACRO_BLOCK_START);
			return true;
		}
		else if(token == MACRO_ENDREGION_KEYWORD)
		{
			val currentOffset = builder.getCurrentOffset();

			builder.advanceLexer();
			if(macroesInfo.findStartActiveBlockForStop(MACRO_REGION_KEYWORD, currentOffset) == null)
			{
				builder.error("'#endregion' without '#region'");
			}

			skipUntilStop(builder);
			mark.done(MACRO_BLOCK_STOP);
			return true;
		}
		else if(token == MACRO_ENDIF_KEYWORD)
		{
			val currentOffset = builder.getCurrentOffset();

			builder.advanceLexer();
			if(macroesInfo.findStartActiveBlockForStop(MACRO_IF_KEYWORD, currentOffset) == null)
			{
				builder.error("'#endif' without '#if'");
			}

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

	private static void skipUntilStop(CSharpBuilderWrapper builder)
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
