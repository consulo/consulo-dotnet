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

package org.mustbe.consulo.csharp.lang.parser.stmt;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.parser.exp.ExpressionParsing;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.NullableFunction;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public class StatementParsing extends SharingParsingHelpers
{
	public static PsiBuilder.Marker parse(CSharpBuilderWrapper wrapper)
	{
		return parseWithSoftElements(new NullableFunction<CSharpBuilderWrapper, PsiBuilder.Marker>()
		{
			@Nullable
			@Override
			public PsiBuilder.Marker fun(CSharpBuilderWrapper builderWrapper)
			{
				return parse0(builderWrapper);
			}
		}, wrapper, VAR_KEYWORD);
	}

	private static PsiBuilder.Marker parse0(CSharpBuilderWrapper wrapper)
	{
		IElementType tokenType = wrapper.getTokenType();
		PsiBuilder.Marker marker = parseVariableDecl(wrapper, tokenType == CONST_KEYWORD);
		if(marker == null)
		{
			return null;
		}

		return marker;
	}

	private static PsiBuilder.Marker parseVariableDecl(CSharpBuilderWrapper wrapper, boolean constToken)
	{
		if(constToken)
		{
			wrapper.advanceLexer();
		}

		PsiBuilder.Marker marker = parseType(wrapper);
		if(marker == null)
		{
			return null;
		}

		if(wrapper.getTokenType() == IDENTIFIER)
		{
			marker = marker.precede();

			wrapper.advanceLexer();

			if(expect(wrapper, SEMICOLON, null))
			{
				marker.done(LOCAL_VARIABLE);
			}
			else
			{
				if(expect(wrapper, EQ, "'=' expected"))
				{
					PsiBuilder.Marker parse = ExpressionParsing.parse(wrapper);
					if(parse == null)
					{
						wrapper.error("Expression expected");
					}
					else
					{
						expect(wrapper, SEMICOLON, "';' expected");
					}
				}
				marker.done(LOCAL_VARIABLE);
			}
			return marker;
		}

		marker.drop();
		return null;
	}
}
