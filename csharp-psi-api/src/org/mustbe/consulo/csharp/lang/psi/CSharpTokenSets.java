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

package org.mustbe.consulo.csharp.lang.psi;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 22.11.13.
 */
public interface CSharpTokenSets extends CSharpSoftTokens
{
	TokenSet PRIMITIVE_TYPES = TokenSet.create(CSharpTokens.STRING_KEYWORD, CSharpTokens.VOID_KEYWORD, CSharpTokens.INT_KEYWORD, CSharpTokens.BOOL_KEYWORD, CSharpTokens.BYTE_KEYWORD, CSharpTokens.CHAR_KEYWORD, CSharpTokens.DECIMAL_KEYWORD,
			CSharpTokens.DOUBLE_KEYWORD, CSharpTokens.FLOAT_KEYWORD, CSharpTokens.LONG_KEYWORD, CSharpTokens.OBJECT_KEYWORD, CSharpTokens.SBYTE_KEYWORD, CSharpTokens.SHORT_KEYWORD, CSharpTokens.UINT_KEYWORD, CSharpTokens.ULONG_KEYWORD, CSharpTokens.USHORT_KEYWORD,
			CSharpTokens.DYNAMIC_KEYWORD, CSharpTokens.VAR_KEYWORD);

	TokenSet TYPE_DECLARATION_START = TokenSet.create(CSharpTokens.CLASS_KEYWORD, CSharpTokens.INTERFACE_KEYWORD, CSharpTokens.STRUCT_KEYWORD, CSharpTokens.ENUM_KEYWORD);

	TokenSet EVENT_ACCESSOR_START = TokenSet.create(CSharpSoftTokens.ADD_KEYWORD, CSharpSoftTokens.REMOVE_KEYWORD);

	TokenSet PROPERTY_ACCESSOR_START = TokenSet.create(CSharpSoftTokens.SET_KEYWORD, CSharpSoftTokens.GET_KEYWORD);

	TokenSet MODIFIERS = TokenSet.create(CSharpTokens.STATIC_KEYWORD, CSharpTokens.PUBLIC_KEYWORD, CSharpSoftTokens.PARTIAL_KEYWORD, CSharpTokens.IN_KEYWORD, CSharpTokens.OUT_KEYWORD, CSharpTokens.INTERNAL_KEYWORD,
			CSharpTokens.ABSTRACT_KEYWORD, CSharpTokens.PRIVATE_KEYWORD, CSharpTokens.SEALED_KEYWORD, CSharpTokens.UNSAFE_KEYWORD, CSharpTokens.OVERRIDE_KEYWORD, CSharpTokens.REF_KEYWORD, CSharpTokens.EXTERN_KEYWORD, CSharpTokens.VIRTUAL_KEYWORD,
			CSharpTokens.PROTECTED_KEYWORD, CSharpTokens.VOLATILE_KEYWORD, CSharpTokens.PARAMS_KEYWORD, CSharpTokens.READONLY_KEYWORD, CSharpSoftTokens.ASYNC_KEYWORD);

	TokenSet KEYWORDS = TokenSet.create(CSharpTokens.STRING_KEYWORD, CSharpTokens.STATIC_KEYWORD, CSharpTokens.CLASS_KEYWORD, CSharpTokens.USING_KEYWORD, CSharpTokens.VOID_KEYWORD, CSharpTokens.NAMESPACE_KEYWORD, CSharpTokens.NEW_KEYWORD,
			CSharpTokens.TYPEOF_KEYWORD, CSharpTokens.PUBLIC_KEYWORD, CSharpTokens.INTERFACE_KEYWORD, CSharpTokens.STRUCT_KEYWORD, CSharpTokens.ENUM_KEYWORD, CSharpTokens.INT_KEYWORD, CSharpTokens.DELEGATE_KEYWORD, CSharpTokens.IN_KEYWORD, CSharpTokens.OUT_KEYWORD,
			CSharpSoftTokens.WHERE_KEYWORD, CSharpTokens.EVENT_KEYWORD, CSharpSoftTokens.GLOBAL_KEYWORD, CSharpSoftTokens.ADD_KEYWORD, CSharpSoftTokens.REMOVE_KEYWORD, CSharpSoftTokens.SET_KEYWORD, CSharpSoftTokens.GET_KEYWORD, CSharpTokens.BOOL_KEYWORD, CSharpTokens.BYTE_KEYWORD,
			CSharpTokens.CHAR_KEYWORD, CSharpTokens.DECIMAL_KEYWORD, CSharpTokens.DOUBLE_KEYWORD, CSharpTokens.FLOAT_KEYWORD, CSharpTokens.LONG_KEYWORD, CSharpTokens.OBJECT_KEYWORD, CSharpTokens.SBYTE_KEYWORD, CSharpTokens.SHORT_KEYWORD, CSharpTokens.UINT_KEYWORD,
			CSharpTokens.ULONG_KEYWORD, CSharpTokens.USHORT_KEYWORD, CSharpTokens.INTERNAL_KEYWORD, CSharpTokens.ABSTRACT_KEYWORD, CSharpTokens.PRIVATE_KEYWORD, CSharpTokens.SEALED_KEYWORD, CSharpTokens.UNSAFE_KEYWORD, CSharpTokens.OVERRIDE_KEYWORD,
			CSharpTokens.REF_KEYWORD, CSharpTokens.EXTERN_KEYWORD, CSharpTokens.VIRTUAL_KEYWORD, CSharpTokens.PROTECTED_KEYWORD, CSharpTokens.VOLATILE_KEYWORD, CSharpTokens.PARAMS_KEYWORD, CSharpTokens.READONLY_KEYWORD, CSharpTokens.DYNAMIC_KEYWORD,
			CSharpTokens.VAR_KEYWORD, CSharpTokens.CONST_KEYWORD);

	TokenSet COMMENTS = TokenSet.create(CSharpTokens.LINE_COMMENT, CSharpTokens.LINE_DOC_COMMENT, CSharpTokens.BLOCK_COMMENT);

	TokenSet STRINGS = TokenSet.create(CSharpTokens.CHARACTER_LITERAL, CSharpTokens.STRING_LITERAL, CSharpTokens.VERBATIM_STRING_LITERAL);

	TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE);
}
