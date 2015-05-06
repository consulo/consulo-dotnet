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

package org.mustbe.consulo.csharp.cfs.ide.highlight;

import org.consulo.fileTypes.LanguageVersionableSyntaxHighlighterFactory;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.cfs.lang.BaseCfsLanguageVersion;
import org.mustbe.consulo.csharp.cfs.lang.CfsLanguage;
import com.intellij.lang.LanguageVersion;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;

/**
 * @author VISTALL
 * @since 31.08.14
 */
public class CfsSyntaxHighlighterFactory extends LanguageVersionableSyntaxHighlighterFactory
{
	public CfsSyntaxHighlighterFactory()
	{
		super(CfsLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public SyntaxHighlighter getSyntaxHighlighter(@NotNull LanguageVersion languageVersion)
	{
		return new CfsSyntaxHighlighter((BaseCfsLanguageVersion)languageVersion);
	}
}