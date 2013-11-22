package org.mustbe.consulo.csharp;

import com.intellij.lang.Language;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public class CSharpLanguage extends Language
{
	public static final Language INSTANCE = new CSharpLanguage();

	public CSharpLanguage()
	{
		super("C#");
	}
}
