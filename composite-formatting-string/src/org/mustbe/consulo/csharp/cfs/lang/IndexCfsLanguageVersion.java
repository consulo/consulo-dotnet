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

package org.mustbe.consulo.csharp.cfs.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.cfs.lang.lexer.CfsLexer;
import org.mustbe.consulo.csharp.cfs.lang.parser.CfsParser;
import com.intellij.lang.LanguageVersionWithDefinition;
import com.intellij.lang.LanguageVersionWithParsing;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 12.03.2015
 */
public class IndexCfsLanguageVersion extends BaseCfsLanguageVersion implements LanguageVersionWithParsing<CfsLanguage>,
		LanguageVersionWithDefinition<CfsLanguage>
{
	public IndexCfsLanguageVersion()
	{
		super("INDEX", CfsLanguage.INSTANCE);
	}

	@NotNull
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

	@NotNull
	@Override
	public PsiParser createParser(@Nullable Project project)
	{
		return new CfsParser(CfsTokens.INDEX);
	}
}
