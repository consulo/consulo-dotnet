package org.mustbe.consulo.csharp.lang;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageVersion;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public class CSharpLanguage extends Language
{
	public static final CSharpLanguage INSTANCE = new CSharpLanguage();

	public CSharpLanguage()
	{
		super("C#");
	}

	@NotNull
	@Override
	protected LanguageVersion[] findVersions()
	{
		return CSharpLanguageVersion.VALUES;
	}
}
