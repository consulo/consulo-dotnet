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

import consulo.annotation.component.ExtensionImpl;
import consulo.dotnet.cfs.lang.lexer.CfsLexer;
import consulo.dotnet.cfs.lang.parser.CfsParser;
import consulo.language.lexer.Lexer;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.version.LanguageVersionWithDefinition;
import consulo.language.version.LanguageVersionWithParsing;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 12.03.2015
 */
@ExtensionImpl
public class IndexCfsLanguageVersion extends BaseCfsLanguageVersion implements LanguageVersionWithParsing, LanguageVersionWithDefinition
{
	public IndexCfsLanguageVersion()
	{
		super("INDEX", CfsLanguage.INSTANCE);
	}

	@Nonnull
	@Override
	public Lexer createInnerLexer()
	{
		return new CfsLexer(CfsTokens.INDEX);
	}

	@Override
	public boolean isMyElement(@Nullable PsiElement element)
	{
		return true;
	}

	@Override
	public boolean isMyFile(@Nullable Project project, @Nullable VirtualFile virtualFile)
	{
		return true;
	}

	@Nonnull
	@Override
	public PsiParser createParser()
	{
		return new CfsParser(CfsTokens.INDEX);
	}
}
