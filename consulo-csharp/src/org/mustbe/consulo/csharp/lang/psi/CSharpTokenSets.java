package org.mustbe.consulo.csharp.lang.psi;

import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public interface CSharpTokenSets extends CSharpTokens
{
	TokenSet NATIVE_KEYWORDS = TokenSet.create(STRING_KEYWORD, VOID_KEYWORD);

	TokenSet MODIFIERS = TokenSet.create(STATIC_KEYWORD);

	TokenSet KEYWORDS = TokenSet.create(STRING_KEYWORD, STATIC_KEYWORD, CLASS_KEYWORD, USING_KEYWORD, VOID_KEYWORD);

	TokenSet COMMENTS = TokenSet.create(LINE_COMMENT);

	TokenSet STRINGS = TokenSet.create(CHARACTER_LITERAL, STRING_LITERAL, VERBATIM_STRING_LITERAL);

	TokenSet WHITESPACES = TokenSet.create(WHITE_SPACE);
}
