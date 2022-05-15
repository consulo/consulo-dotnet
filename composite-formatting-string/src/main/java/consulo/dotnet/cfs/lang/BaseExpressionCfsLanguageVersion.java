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

import consulo.dotnet.cfs.lang.lexer.CfsLexer;
import consulo.dotnet.cfs.lang.parser.CfsParser;
import consulo.language.Language;
import consulo.language.ast.IElementType;
import consulo.language.lexer.Lexer;
import consulo.language.parser.PsiParser;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 12.03.2015
 */
public abstract class BaseExpressionCfsLanguageVersion extends BaseCfsLanguageVersion
{
	protected IElementType myExpressionElementType;

	public BaseExpressionCfsLanguageVersion(@Nonnull Language baseLanguage)
	{
		super(baseLanguage.getName() + "_EXPRESSION", CfsLanguage.INSTANCE);
	}

	public abstract IElementType createExpressionElementType();

	@Nonnull
	@Override
	public PsiParser createParser()
	{
		if(myExpressionElementType == null)
		{
			myExpressionElementType = createExpressionElementType();
		}
		return new CfsParser(myExpressionElementType);
	}

	@Nonnull
	@Override
	public Lexer createInnerLexer()
	{
		if(myExpressionElementType == null)
		{
			myExpressionElementType = createExpressionElementType();
		}
		return new CfsLexer(myExpressionElementType);
	}
}
