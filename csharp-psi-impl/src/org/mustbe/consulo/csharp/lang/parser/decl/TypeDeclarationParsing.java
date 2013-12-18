/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.csharp.lang.parser.decl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.parser.macro.MacroesInfo;
import com.intellij.lang.PsiBuilder;
import com.intellij.util.NotNullFunction;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class TypeDeclarationParsing extends SharingParsingHelpers
{
	public static void parse(CSharpBuilderWrapper builder, PsiBuilder.Marker marker, MacroesInfo macroesInfo)
	{
		builder.advanceLexer();

		expect(builder, IDENTIFIER, "Name expected");

		GenericParameterParsing.parseList(builder);

		if(builder.getTokenType() == COLON)
		{
			parseWithSoftElements(new NotNullFunction<CSharpBuilderWrapper, PsiBuilder.Marker>()
			{
				@NotNull
				@Override
				public PsiBuilder.Marker fun(CSharpBuilderWrapper builderWrapper)
				{
					PsiBuilder.Marker mark = builderWrapper.mark();
					builderWrapper.advanceLexer();  // colon

					parseTypeList(builderWrapper);
					mark.done(EXTENDS_LIST);
					return mark;
				}
			}, builder, GLOBAL_KEYWORD);
		}

		GenericParameterParsing.parseGenericConstraintList(builder);

		if(expect(builder, LBRACE, "'{' expected"))
		{
			while(!builder.eof() && builder.getTokenType() != RBRACE)
			{
				if(!DeclarationParsing.parse(builder, macroesInfo, true))
				{
					break;
				}
			}

			expect(builder, RBRACE, "'}' expected");
		}

		marker.done(TYPE_DECLARATION);
	}
}
