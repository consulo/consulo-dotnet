package org.mustbe.consulo.csharp.lang.lexer;

import java.io.Reader;

import com.intellij.lexer.FlexAdapter;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public class CSharpLexer extends FlexAdapter
{
	public CSharpLexer()
	{
		super(new _CSharpLexer((Reader) null));
	}
}
