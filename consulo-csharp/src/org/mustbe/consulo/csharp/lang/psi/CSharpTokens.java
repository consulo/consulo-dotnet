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

	IElementType INT_KEYWORD = new IElementType("INT_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType STATIC_KEYWORD = new IElementType("STATIC_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType PUBLIC_KEYWORD = new IElementType("PUBLIC_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType NAMESPACE_KEYWORD = new IElementType("NAMESPACE_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType EVENT_KEYWORD = new IElementType("EVENT_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType DELEGATE_KEYWORD = new IElementType("DELEGATE_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType CLASS_KEYWORD = new IElementType("CLASS_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType INTERFACE_KEYWORD = new IElementType("INTERFACE_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType STRUCT_KEYWORD = new IElementType("STRUCT_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType ENUM_KEYWORD = new IElementType("ENUM_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType NEW_KEYWORD = new IElementType("NEW_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType TYPEOF_KEYWORD = new IElementType("TYPEOF_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType LBRACE = new IElementType("LBRACE", CSharpLanguage.INSTANCE);

	IElementType RBRACE = new IElementType("RBRACE", CSharpLanguage.INSTANCE);

	IElementType LPAR = new IElementType("LPAR", CSharpLanguage.INSTANCE);

	IElementType RPAR = new IElementType("RPAR", CSharpLanguage.INSTANCE);

	IElementType LBRACKET = new IElementType("LBRACKET", CSharpLanguage.INSTANCE);

	IElementType RBRACKET = new IElementType("RBRACKET", CSharpLanguage.INSTANCE);

	IElementType COMMA = new IElementType("COMMA", CSharpLanguage.INSTANCE);

	IElementType SEMICOLON = new IElementType("SEMICOLON", CSharpLanguage.INSTANCE);

	IElementType DOT = new IElementType("DOT", CSharpLanguage.INSTANCE);

	IElementType LINE_COMMENT = new IElementType("LINE_COMMENT", CSharpLanguage.INSTANCE);

	IElementType CHARACTER_LITERAL = new IElementType("CHARACTER_LITERAL", CSharpLanguage.INSTANCE);

	IElementType STRING_LITERAL = new IElementType("STRING_LITERAL", CSharpLanguage.INSTANCE);

	IElementType VERBATIM_STRING_LITERAL = new IElementType("VERBATIM_STRING_LITERAL", CSharpLanguage.INSTANCE);

	IElementType IDENTIFIER = new IElementType("IDENTIFIER", CSharpLanguage.INSTANCE);
}
