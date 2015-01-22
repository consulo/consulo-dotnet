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

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public interface MsilTokenSets extends MsilTokens
{
	TokenSet IDENTIFIERS = TokenSet.create(IDENTIFIER, QIDENTIFIER);

	TokenSet IDENTIFIERS_AND_CTOR = TokenSet.create(IDENTIFIER, QIDENTIFIER, _CTOR_KEYWORD, _CCTOR_KEYWORD);

	TokenSet GENERIC_CONSTRAINT_KEYWORDS = TokenSet.create(VALUETYPE_KEYWORD, CLASS_KEYWORD);

	TokenSet _KEYWORDS = TokenSet.create(_CLASS_KEYWORD, _FIELD_KEYWORD, _PROPERTY_KEYWORD, _METHOD_KEYWORD, _ASSEMBLY_KEYWORD, _EVENT_KEYWORD,
			_CUSTOM_KEYWORD, _PARAM_KEYWORD, _ADDON_KEYWORD, _REMOVEON_KEYWORD, _SET_KEYWORD, _GET_KEYWORD, _CTOR_KEYWORD, _CCTOR_KEYWORD);

	MsilModifierElementType[] MODIFIERS_AS_ARRAY = {
			PUBLIC_KEYWORD,
			STATIC_KEYWORD,
			ABSTRACT_KEYWORD,
			FINAL_KEYWORD,
			PRIVATE_KEYWORD,
			LITERAL_KEYWORD,
			ASSEMBLY_KEYWORD,
			FAMORASSEMBLY_KEYWORD,
			INITONLY_KEYWORD,
			HIDEBYSIG_KEYWORD,
			VIRTUAL_KEYWORD,
			PROTECTED_KEYWORD,
			BRACKET_OUT_KEYWORD,
			BRACKET_OPT_KEYWORD,
			VALUE_KEYWORD,
			INTERFACE_KEYWORD,
			SERIALIZABLE_KEYWORD,
			SEALED_KEYWORD,
			SPECIALNAME_KEYWORD,
			RTSPECIALNAME_KEYWORD,
			NESTED_KEYWORD,
			BRACKET_IN_KEYWORD
	};

	TokenSet MODIFIERS = TokenSet.create(MODIFIERS_AS_ARRAY);

	TokenSet REFERENCE_TYPE_START = TokenSet.create(CLASS_KEYWORD, VALUETYPE_KEYWORD);

	IElementType[] NATIVE_TYPES_AS_ARRAY = {
			INT32_KEYWORD,
			INT64_KEYWORD,
			VOID_KEYWORD,
			BOOL_KEYWORD,
			OBJECT_KEYWORD,
			STRING_KEYWORD,
			UINT64_KEYWORD,
			FLOAT_KEYWORD,
			INT8_KEYWORD,
			UINT8_KEYWORD,
			FLOAT64_KEYWORD,
			UINT32_KEYWORD,
			INT16_KEYWORD,
			UINT16_KEYWORD,
			CHAR_KEYWORD,
			INT_KEYWORD,
			UINT_KEYWORD
	};

	TokenSet XXX_ACCESSOR_START = TokenSet.create(_GET_KEYWORD,
			_SET_KEYWORD,
			_ADDON_KEYWORD,
			_REMOVEON_KEYWORD);

	TokenSet NATIVE_TYPES = TokenSet.create(NATIVE_TYPES_AS_ARRAY);

	TokenSet KEYWORDS = TokenSet.orSet(MODIFIERS, REFERENCE_TYPE_START, NATIVE_TYPES, TokenSet.create(EXTENDS_KEYWORD, IMPLEMENTS_KEYWORD));

	TokenSet COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT);

	TokenSet WHITESPACES = TokenSet.create(WHITE_SPACE);
}
