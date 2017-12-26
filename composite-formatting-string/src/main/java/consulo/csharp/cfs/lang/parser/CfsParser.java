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

package consulo.csharp.cfs.lang.parser;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderUtil;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import consulo.csharp.cfs.lang.CfsElements;
import consulo.csharp.cfs.lang.CfsTokens;
import consulo.lang.LanguageVersion;

/**
 * @author VISTALL
 * @since 31.08.14
 */
public class CfsParser implements PsiParser
{
	private final IElementType myArgumentElementType;

	public CfsParser(IElementType argumentElementType)
	{
		myArgumentElementType = argumentElementType;
	}

	@NotNull
	@Override
	public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder, @NotNull LanguageVersion languageVersion)
	{
		PsiBuilder.Marker mark = builder.mark();
		while(!builder.eof())
		{
			if(builder.getTokenType() == CfsTokens.START)
			{
				PsiBuilder.Marker itemMark = builder.mark();
				builder.advanceLexer();

				if(!PsiBuilderUtil.expect(builder, myArgumentElementType))
				{
					builder.error("Argument expected");
				}

				if(builder.getTokenType() == CfsTokens.COMMA)
				{
					builder.advanceLexer();
					if(!PsiBuilderUtil.expect(builder, CfsTokens.ALIGN))
					{
						builder.error("Align expected");
					}
				}

				if(builder.getTokenType() == CfsTokens.COLON)
				{
					builder.advanceLexer();
					if(!PsiBuilderUtil.expect(builder, CfsTokens.FORMAT))
					{
						builder.error("Format expected");
					}
				}

				if(!PsiBuilderUtil.expect(builder, CfsTokens.END))
				{
					builder.error("} expected");
				}
				itemMark.done(CfsElements.ITEM);
			}
			else
			{
				builder.advanceLexer();
			}
		}
		mark.done(elementType);
		return builder.getTreeBuilt();
	}
}
