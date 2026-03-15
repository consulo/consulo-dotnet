/*
 * Copyright 2013-2014 must-be.org
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

package consulo.msil.impl.lang;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import consulo.language.file.FileViewProvider;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.lexer.Lexer;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.version.LanguageVersion;
import consulo.msil.MsilLanguage;
import consulo.msil.impl.lang.lexer.MsilLexer;
import consulo.msil.impl.lang.parser.MsilParser;
import consulo.msil.impl.lang.psi.MsilStubElements;
import consulo.msil.impl.lang.psi.MsilTokenSets;
import consulo.msil.impl.lang.psi.impl.MsilFileImpl;


/**
 * @author VISTALL
 * @since 21.05.14
 */
@ExtensionImpl
public class MsilParserDefinition implements ParserDefinition
{
	@Override
	public Language getLanguage()
	{
		return MsilLanguage.INSTANCE;
	}

	@Override
	public Lexer createLexer(LanguageVersion languageVersion)
	{
		return new MsilLexer();
	}

	@Override
	public PsiParser createParser(LanguageVersion languageVersion)
	{
		return new MsilParser();
	}

	@Override
	public IFileElementType getFileNodeType()
	{
		return MsilStubElements.FILE;
	}

	@Override
	public TokenSet getWhitespaceTokens(LanguageVersion languageVersion)
	{
		return MsilTokenSets.WHITESPACES;
	}

	@Override
	public TokenSet getCommentTokens(LanguageVersion languageVersion)
	{
		return MsilTokenSets.COMMENTS;
	}

	@Override
	public TokenSet getStringLiteralElements(LanguageVersion languageVersion)
	{
		return TokenSet.EMPTY;
	}

	@Override
	public PsiElement createElement(ASTNode astNode)
	{
		return new ASTWrapperPsiElement(astNode);
	}

	@Override
	public PsiFile createFile(FileViewProvider fileViewProvider)
	{
		return new MsilFileImpl(fileViewProvider);
	}
}
