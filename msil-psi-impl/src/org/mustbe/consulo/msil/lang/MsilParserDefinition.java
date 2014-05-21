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

package org.mustbe.consulo.msil.lang;

import java.io.Reader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.msil.lang.lexer._MsilLexer;
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.impl.MsilFileImpl;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilParserDefinition implements ParserDefinition
{
	@NotNull
	@Override
	public Lexer createLexer(@Nullable Project project, @NotNull LanguageVersion languageVersion)
	{
		return new FlexAdapter(new _MsilLexer((Reader)null));
	}

	@NotNull
	@Override
	public PsiParser createParser(
			@Nullable Project project, @NotNull LanguageVersion languageVersion)
	{
		return new PsiParser()
		{
			@NotNull
			@Override
			public ASTNode parse(
					@NotNull IElementType elementType, @NotNull PsiBuilder builder, @NotNull LanguageVersion languageVersion)
			{
				PsiBuilder.Marker mark = builder.mark();
				while(!builder.eof())
				{
					builder.advanceLexer();
				}
				mark.done(elementType);
				return builder.getTreeBuilt();
			}
		};
	}

	@NotNull
	@Override
	public IFileElementType getFileNodeType()
	{
		return MsilStubElements.FILE;
	}

	@NotNull
	@Override
	public TokenSet getWhitespaceTokens(@NotNull LanguageVersion languageVersion)
	{
		return MsilTokenSets.WHITESPACES;
	}

	@NotNull
	@Override
	public TokenSet getCommentTokens(@NotNull LanguageVersion languageVersion)
	{
		return MsilTokenSets.COMMENTS;
	}

	@NotNull
	@Override
	public TokenSet getStringLiteralElements(@NotNull LanguageVersion languageVersion)
	{
		return TokenSet.EMPTY;
	}

	@NotNull
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

	@NotNull
	@Override
	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode2)
	{
		return SpaceRequirements.MAY;
	}
}
