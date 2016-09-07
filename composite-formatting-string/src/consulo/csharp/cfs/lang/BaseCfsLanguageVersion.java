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

package consulo.csharp.cfs.lang;

import org.jetbrains.annotations.NotNull;
import com.intellij.lexer.Lexer;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;
import consulo.lang.BaseLanguageVersion;
import consulo.lang.LanguageVersionWithParsing;

/**
 * @author VISTALL
 * @since 12.03.2015
 */
public abstract class BaseCfsLanguageVersion extends BaseLanguageVersion<CfsLanguage> implements LanguageVersionWithParsing<CfsLanguage>
{
	public BaseCfsLanguageVersion(String name, CfsLanguage language)
	{
		super(name, language);
	}

	@NotNull
	@Override
	public Lexer createLexer()
	{
		return new MergingLexerAdapter(createInnerLexer(), TokenSet.create(CfsTokens.TEXT));
	}

	@NotNull
	public abstract Lexer createInnerLexer();

	@NotNull
	@Override
	public TokenSet getWhitespaceTokens()
	{
		return TokenSet.EMPTY;
	}

	@NotNull
	@Override
	public TokenSet getCommentTokens()
	{
		return TokenSet.EMPTY;
	}

	@NotNull
	@Override
	public TokenSet getStringLiteralElements()
	{
		return TokenSet.EMPTY;
	}
}

