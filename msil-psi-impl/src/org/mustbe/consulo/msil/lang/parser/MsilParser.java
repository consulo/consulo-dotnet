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

package org.mustbe.consulo.msil.lang.parser;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.msil.lang.psi.MsilElements;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderUtil;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilParser implements PsiParser, MsilTokens, MsilElements
{
	@NotNull
	@Override
	public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder, @NotNull LanguageVersion languageVersion)
	{
		PsiBuilder.Marker mark = builder.mark();
		while(!builder.eof())
		{
			IElementType tokenType = builder.getTokenType();
			if(tokenType == _CLASS_KEYWORD)
			{
				parseClass(builder);
			}
			else
			{
				builder.advanceLexer();
			}
		}
		mark.done(elementType);
		return builder.getTreeBuilt();
	}

	private void parseClass(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		parseModifierList(builder);

		if(!PsiBuilderUtil.expect(builder, IDENTIFIER))
		{
			builder.error("Expected name");
		}

		mark.done(CLASS);
	}

	private void parseModifierList(PsiBuilder builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		while(MsilTokenSets.MODIFIERS.contains(builder.getTokenType()))
		{
			builder.advanceLexer();
		}
		mark.done(MODIFIER_LIST);
	}
}
