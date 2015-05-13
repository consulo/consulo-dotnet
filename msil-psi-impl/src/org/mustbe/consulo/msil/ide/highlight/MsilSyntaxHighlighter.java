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

package org.mustbe.consulo.msil.ide.highlight;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.msil.lang.lexer._MsilLexer;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilSyntaxHighlighter extends SyntaxHighlighterBase
{
	@NotNull
	@Override
	public Lexer getHighlightingLexer()
	{
		return new _MsilLexer();
	}

	@NotNull
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
		else if(elementType == MsilTokens.NUMBER)
		{
			return pack(DefaultLanguageHighlighterColors.NUMBER);
		}
		return new TextAttributesKey[0];
	}
}
