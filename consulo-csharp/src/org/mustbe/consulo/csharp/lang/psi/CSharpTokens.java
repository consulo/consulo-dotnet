package org.mustbe.consulo.csharp.lang.psi;

import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public interface CSharpTokens extends TokenType
{
	IElementType STRING_KEYWORD = new IElementType("STRING_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType USING_KEYWORD = new IElementType("USING_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType VOID_KEYWORD = new IElementType("VOID_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType STATIC_KEYWORD = new IElementType("STATIC_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType CLASS_KEYWORD = new IElementType("CLASS_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType LINE_COMMENT = new IElementType("LINE_COMMENT", CSharpLanguage.INSTANCE);

	IElementType CHARACTER_LITERAL = new IElementType("CHARACTER_LITERAL", CSharpLanguage.INSTANCE);

	IElementType STRING_LITERAL = new IElementType("STRING_LITERAL", CSharpLanguage.INSTANCE);

	IElementType VERBATIM_STRING_LITERAL = new IElementType("VERBATIM_STRING_LITERAL", CSharpLanguage.INSTANCE);

	IElementType IDENTIFIER = new IElementType("IDENTIFIER", CSharpLanguage.INSTANCE);
}
