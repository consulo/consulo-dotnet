package org.mustbe.consulo.csharp.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.lexer.CSharpLexer;
import org.mustbe.consulo.csharp.lang.parser.CSharpParser;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import org.mustbe.consulo.dotnet.DotNetVersion;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.LanguageVersionWithParsing;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.ArrayUtil;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public enum CSharpLanguageVersion implements LanguageVersion<CSharpLanguage>, LanguageVersionWithParsing<CSharpLanguage>
{
	_1_0(DotNetVersion._1_0),
	_1_2(DotNetVersion._1_1),
	_2_0(DotNetVersion._2_0),
	_3_0(DotNetVersion._2_0, DotNetVersion._3_0, DotNetVersion._3_5),
	_4_0(DotNetVersion._4_0),
	_5_0(DotNetVersion._4_5, DotNetVersion._4_5_1);

	public static final CSharpLanguageVersion LAST = _5_0;
	public static final CSharpLanguageVersion[] VALUES = ArrayUtil.reverseArray(CSharpLanguageVersion.values());

	private DotNetVersion[] myDotNetVersions;

	CSharpLanguageVersion(DotNetVersion... dotNetVersions)
	{
		myDotNetVersions = dotNetVersions;
	}

	@NotNull
	@Override
	public PsiParser createParser(@Nullable Project project)
	{
		return new CSharpParser();
	}

	@NotNull
	@Override
	public Lexer createLexer(@Nullable Project project)
	{
		return new CSharpLexer();
	}

	@NotNull
	@Override
	public TokenSet getCommentTokens()
	{
		return CSharpTokenSets.COMMENTS;
	}

	@NotNull
	@Override
	public TokenSet getStringLiteralElements()
	{
		return CSharpTokenSets.STRINGS;
	}

	@NotNull
	@Override
	public TokenSet getWhitespaceTokens()
	{
		return CSharpTokenSets.WHITESPACES;
	}

	@NotNull
	@Override
	public String getName()
	{
		return name();
	}

	@Override
	public CSharpLanguage getLanguage()
	{
		return CSharpLanguage.INSTANCE;
	}

	@NotNull
	public static CSharpLanguageVersion convertFromVersion(DotNetVersion version)
	{
		for(CSharpLanguageVersion value : VALUES)
		{
			if(ArrayUtil.contains(version, value.myDotNetVersions))
			{
				return value;
			}
		}
		return LAST;
	}
}
