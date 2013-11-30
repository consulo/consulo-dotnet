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

	TokenSet ALL = TokenSet.create(PARTIAL_KEYWORD, WHERE_KEYWORD);
}
