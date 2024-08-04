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

package consulo.msil.impl.ide.highlight;

import consulo.codeEditor.DefaultLanguageHighlighterColors;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.ast.IElementType;
import consulo.language.editor.highlight.SyntaxHighlighterBase;
import consulo.language.lexer.Lexer;
import consulo.msil.impl.lang.lexer.MsilLexer;
import consulo.msil.impl.lang.psi.MsilTokenSets;
import consulo.msil.impl.lang.psi.MsilTokens;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilSyntaxHighlighter extends SyntaxHighlighterBase
{
	@Nonnull
	@Override
	public Lexer getHighlightingLexer()
	{
		return new MsilLexer();
	}

	@Nonnull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType elementType)
	{
		if(MsilTokenSets._KEYWORDS.contains(elementType))
		{
			return pack(DefaultLanguageHighlighterColors.MACRO_KEYWORD);
		}
		else if(MsilTokenSets.COMMENTS.contains(elementType))
		{
			return pack(DefaultLanguageHighlighterColors.LINE_COMMENT);
		}
		else if(MsilTokenSets.KEYWORDS.contains(elementType))
		{
			return pack(DefaultLanguageHighlighterColors.KEYWORD);
		}
		else if(elementType == MsilTokens.STRING_LITERAL)
		{
			return pack(DefaultLanguageHighlighterColors.STRING);
		}
		else if(elementType == MsilTokens.NUMBER_LITERAL || elementType == MsilTokens.HEX_NUMBER_LITERAL || elementType == MsilTokens.DOUBLE_LITERAL)
		{
			return pack(DefaultLanguageHighlighterColors.NUMBER);
		}
		return new TextAttributesKey[0];
	}
}
