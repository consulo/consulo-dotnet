/*
 * Copyright 2013-2015 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.dotnet.cfs.lang;

import consulo.language.ast.TokenSet;
import consulo.language.lexer.Lexer;
import consulo.language.lexer.MergingLexerAdapter;
import consulo.language.version.LanguageVersion;
import consulo.language.version.LanguageVersionWithParsing;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 12.03.2015
 */
public abstract class BaseCfsLanguageVersion extends LanguageVersion implements LanguageVersionWithParsing
{
	public BaseCfsLanguageVersion(String name, CfsLanguage language)
	{
		super(name, name, language);
	}

	@Nonnull
	@Override
	public Lexer createLexer()
	{
		return new MergingLexerAdapter(createInnerLexer(), TokenSet.create(CfsTokens.TEXT));
	}

	@Nonnull
	public abstract Lexer createInnerLexer();

	@Nonnull
	@Override
	public TokenSet getWhitespaceTokens()
	{
		return TokenSet.EMPTY;
	}

	@Nonnull
	@Override
	public TokenSet getCommentTokens()
	{
		return TokenSet.EMPTY;
	}

	@Nonnull
	@Override
	public TokenSet getStringLiteralElements()
	{
		return TokenSet.EMPTY;
	}
}

