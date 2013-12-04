package org.mustbe.consulo.csharp.lang.psi;

import org.mustbe.consulo.csharp.lang.CSharpLanguage;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface CSharpSoftTokens extends CSharpTokens
{
	IElementType PARTIAL_KEYWORD = new IElementType("PARTIAL_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType WHERE_KEYWORD = new IElementType("WHERE_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType GLOBAL_KEYWORD = new IElementType("GLOBAL_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType ADD_KEYWORD = new IElementType("ADD_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType REMOVE_KEYWORD = new IElementType("REMOVE_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType SET_KEYWORD = new IElementType("SET_KEYWORD", CSharpLanguage.INSTANCE);

	IElementType GET_KEYWORD = new IElementType("GET_KEYWORD", CSharpLanguage.INSTANCE);

	TokenSet ALL = TokenSet.create(PARTIAL_KEYWORD, WHERE_KEYWORD, GLOBAL_KEYWORD, ADD_KEYWORD, REMOVE_KEYWORD, SET_KEYWORD, GET_KEYWORD);
}
