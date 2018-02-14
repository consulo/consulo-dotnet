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

package consulo.csharp.cfs.ide.highlight;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import consulo.csharp.cfs.lang.BaseCfsLanguageVersion;
import consulo.csharp.cfs.lang.CfsTokens;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 31.08.14
 */
public class CfsSyntaxHighlighter extends SyntaxHighlighterBase
{
	private static Map<IElementType, TextAttributesKey> ourKeys = new HashMap<IElementType, TextAttributesKey>();

	static
	{
		safeMap(ourKeys, CfsTokens.ALIGN, DefaultLanguageHighlighterColors.NUMBER);
		safeMap(ourKeys, CfsTokens.INDEX, DefaultLanguageHighlighterColors.NUMBER);
		safeMap(ourKeys, CfsTokens.FORMAT, DefaultLanguageHighlighterColors.INSTANCE_FIELD);
	}

	private final BaseCfsLanguageVersion myLanguageVersion;

	public CfsSyntaxHighlighter(BaseCfsLanguageVersion languageVersion)
	{
		myLanguageVersion = languageVersion;
	}

	@Nonnull
	@Override
	public Lexer getHighlightingLexer()
	{
		return myLanguageVersion.createLexer();
	}

	@Nonnull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType elementType)
	{
		return pack(ourKeys.get(elementType));
	}
}
