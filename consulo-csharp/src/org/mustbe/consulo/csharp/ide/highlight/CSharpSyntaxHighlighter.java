package org.mustbe.consulo.csharp.ide.highlight;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.lexer.CSharpLexer;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public class CSharpSyntaxHighlighter extends SyntaxHighlighterBase
{
	private static Map<IElementType, TextAttributesKey> ourKeys = new HashMap<IElementType, TextAttributesKey>();

	static
	{
		safeMap(ourKeys, CSharpTokens.LINE_COMMENT, DefaultLanguageHighlighterColors.LINE_COMMENT);
		safeMap(ourKeys, CSharpTokenSets.STRINGS, DefaultLanguageHighlighterColors.STRING);
		safeMap(ourKeys, CSharpTokenSets.KEYWORDS, DefaultLanguageHighlighterColors.KEYWORD);
	}

	@NotNull
	@Override
	public Lexer getHighlightingLexer()
	{
		return new CSharpLexer();
	}

	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType elementType)
	{
		return pack(ourKeys.get(elementType));
	}
}
