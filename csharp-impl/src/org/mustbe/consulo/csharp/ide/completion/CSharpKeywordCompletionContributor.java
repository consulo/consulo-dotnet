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

package org.mustbe.consulo.csharp.ide.completion;

import org.jetbrains.annotations.NotNull;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.StandardPatterns;
import com.intellij.util.ProcessingContext;

/**
 * @author VISTALL
 * @since 07.01.14.
 */
public class CSharpKeywordCompletionContributor extends CompletionContributor
{
	public CSharpKeywordCompletionContributor()
	{
		extend(CompletionType.BASIC, StandardPatterns.psiElement(), new CompletionProvider<CompletionParameters>()
		{
			@Override
			protected void addCompletions(@NotNull CompletionParameters completionParameters, ProcessingContext processingContext,
					@NotNull CompletionResultSet completionResultSet)
			{
				/*IElementType[] types = CSharpTokenSets.KEYWORDS.getTypes();
				for(IElementType type : types)
				{
					String keyword = type.toString().replace("_KEYWORD", "").toLowerCase();
					if(keyword.startsWith("macro"))
					{
						continue;
					}
					LookupElementBuilder builder = LookupElementBuilder.create(keyword);
					builder = builder.bold();
					completionResultSet.addElement(builder);
				}   */
			}
		});
	}
}
