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

package org.mustbe.consulo.msil.lang.psi;

import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public interface MsilTokenSets extends MsilTokens
{
	TokenSet _KEYWORDS = TokenSet.create(_CLASS_KEYWORD, _FIELD_KEYWORD, _PROPERTY_KEYWORD, _METHOD_KEYWORD, _ASSEMBLY_KEYWORD,
			_EVENT_KEYWORD, _CUSTOM_KEYWORD, _PARAM_KEYWORD);

	TokenSet KEYWORDS = TokenSet.create(INT32_KEYWORD, INT64_KEYWORD, CLASS_KEYWORD, VALUETYPE_KEYWORD, VOID_KEYWORD, BOOL_KEYWORD, OBJECT_KEYWORD,
			IMPLEMENTS_KEYWORD, EXTENDS_KEYWORD, STRING_KEYWORD, UINT64_KEYWORD, FLOAT_KEYWORD, INT8_KEYWORD, UINT8_KEYWORD, FLOAT64_KEYWORD,
			UINT32_KEYWORD, INT16_KEYWORD, UINT16_KEYWORD, CHAR_KEYWORD);

	TokenSet COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT);

	TokenSet WHITESPACES = TokenSet.create(WHITE_SPACE);
}
