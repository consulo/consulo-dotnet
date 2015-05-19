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

import org.mustbe.consulo.msil.MsilLanguage;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public interface MsilTokens extends TokenType
{
	IElementType IDENTIFIER = new IElementType("IDENTIFIER", MsilLanguage.INSTANCE);

	IElementType NUMBER = new IElementType("NUMBER", MsilLanguage.INSTANCE);

	IElementType HEX_NUMBER = new IElementType("HEX_NUMBER", MsilLanguage.INSTANCE);

	IElementType COLONCOLON = new IElementType("COLONCOLON", MsilLanguage.INSTANCE);

	IElementType _CLASS_KEYWORD = new IElementType("_CLASS_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _CTOR_KEYWORD = new IElementType("_CTOR_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _CCTOR_KEYWORD = new IElementType("_CCTOR_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _METHOD_KEYWORD = new IElementType("_METHOD_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _FIELD_KEYWORD = new IElementType("_FIELD_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _PROPERTY_KEYWORD = new IElementType("_PROPERTY_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _EVENT_KEYWORD = new IElementType("_EVENT_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _ASSEMBLY_KEYWORD = new IElementType("_ASSEMBLY_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _CUSTOM_KEYWORD = new IElementType("_CUSTOM_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _PARAM_KEYWORD = new IElementType("_PARAM_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _GET_KEYWORD = new IElementType("_GET_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _SET_KEYWORD = new IElementType("_SET_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _ADDON_KEYWORD = new IElementType("_ADDON_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _REMOVEON_KEYWORD = new IElementType("_REMOVEON_KEYWORD", MsilLanguage.INSTANCE);

	IElementType CLASS_KEYWORD = new IElementType("CLASS_KEYWORD", MsilLanguage.INSTANCE);

	IElementType VALUETYPE_KEYWORD = new IElementType("VALUETYPE_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INT32_KEYWORD = new IElementType("INT32_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INT64_KEYWORD = new IElementType("INT64_KEYWORD", MsilLanguage.INSTANCE);

	IElementType VOID_KEYWORD = new IElementType("VOID_KEYWORD", MsilLanguage.INSTANCE);

	IElementType BOOL_KEYWORD = new IElementType("BOOL_KEYWORD", MsilLanguage.INSTANCE);

	IElementType OBJECT_KEYWORD = new IElementType("OBJECT_KEYWORD", MsilLanguage.INSTANCE);

	IElementType EXTENDS_KEYWORD = new IElementType("EXTENDS_KEYWORD", MsilLanguage.INSTANCE);

	IElementType STRING_KEYWORD = new IElementType("STRING_KEYWORD", MsilLanguage.INSTANCE);

	IElementType CHAR_KEYWORD = new IElementType("CHAR_KEYWORD", MsilLanguage.INSTANCE);

	IElementType UINT64_KEYWORD = new IElementType("UINT64_KEYWORD", MsilLanguage.INSTANCE);

	IElementType FLOAT32_KEYWORD = new IElementType("FLOAT32_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INT8_KEYWORD = new IElementType("INT8_KEYWORD", MsilLanguage.INSTANCE);

	IElementType UINT8_KEYWORD = new IElementType("UINT8_KEYWORD", MsilLanguage.INSTANCE);

	IElementType FLOAT64_KEYWORD = new IElementType("FLOAT64_KEYWORD", MsilLanguage.INSTANCE);

	IElementType UINT32_KEYWORD = new IElementType("UINT32_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INT16_KEYWORD = new IElementType("INT16_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INT_KEYWORD = new IElementType("INT_KEYWORD", MsilLanguage.INSTANCE);

	IElementType UINT_KEYWORD = new IElementType("UINT_KEYWORD", MsilLanguage.INSTANCE);

	IElementType UINT16_KEYWORD = new IElementType("UINT16_KEYWORD", MsilLanguage.INSTANCE);

	IElementType IMPLEMENTS_KEYWORD = new IElementType("IMPLEMENTS_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType PUBLIC_KEYWORD = new MsilModifierElementType("PUBLIC_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType STATIC_KEYWORD = new MsilModifierElementType("STATIC_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType ABSTRACT_KEYWORD = new MsilModifierElementType("ABSTRACT_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType FINAL_KEYWORD = new MsilModifierElementType("FINAL_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType PRIVATE_KEYWORD = new MsilModifierElementType("PRIVATE_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType LITERAL_KEYWORD = new MsilModifierElementType("LITERAL_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType ASSEMBLY_KEYWORD = new MsilModifierElementType("ASSEMBLY_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType FAMORASSEMBLY_KEYWORD = new MsilModifierElementType("FAMORASSEMBLY_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType HIDEBYSIG_KEYWORD = new MsilModifierElementType("HIDEBYSIG_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType INITONLY_KEYWORD = new MsilModifierElementType("INITONLY_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType VIRTUAL_KEYWORD = new MsilModifierElementType("VIRTUAL_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType PROTECTED_KEYWORD = new MsilModifierElementType("PROTECTED_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType VALUE_KEYWORD = new MsilModifierElementType("VALUE_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType BRACKET_OUT_KEYWORD = new MsilModifierElementType("BRACKET_OUT_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType BRACKET_IN_KEYWORD = new MsilModifierElementType("BRACKET_IN_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType BRACKET_OPT_KEYWORD = new MsilModifierElementType("BRACKET_OPT_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType INTERFACE_KEYWORD = new MsilModifierElementType("INTERFACE_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType SERIALIZABLE_KEYWORD = new MsilModifierElementType("SERIALIZABLE_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType SEALED_KEYWORD = new MsilModifierElementType("SEALED_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType SPECIALNAME_KEYWORD = new MsilModifierElementType("SPECIALNAME_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType RTSPECIALNAME_KEYWORD = new MsilModifierElementType("RTSPECIALNAME_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType NESTED_KEYWORD = new MsilModifierElementType("NESTED_KEYWORD", MsilLanguage.INSTANCE);

	MsilModifierElementType VARARG_KEYWORD = new MsilModifierElementType("VARARG_KEYWORD", MsilLanguage.INSTANCE);

	IElementType PLUS = new IElementType("PLUS =", MsilLanguage.INSTANCE);

	IElementType MINUS = new IElementType("MINUS", MsilLanguage.INSTANCE);

	IElementType PERC = new IElementType("PERC", MsilLanguage.INSTANCE);

	IElementType AND = new IElementType("AND", MsilLanguage.INSTANCE);

	IElementType COMMA = new IElementType("COMMA", MsilLanguage.INSTANCE);

	IElementType LBRACE = new IElementType("LBRACE", MsilLanguage.INSTANCE);

	IElementType RBRACE = new IElementType("RBRACE", MsilLanguage.INSTANCE);

	IElementType ELLIPSIS = new IElementType("ELLIPSIS", MsilLanguage.INSTANCE);

	IElementType BLOCK_COMMENT = new IElementType("BLOCK_COMMENT", MsilLanguage.INSTANCE);

	IElementType LINE_COMMENT = new IElementType("LINE_COMMENT", MsilLanguage.INSTANCE);

	IElementType LBRACKET = new IElementType("LBRACKET", MsilLanguage.INSTANCE);

	IElementType LPAR = new IElementType("LPAR", MsilLanguage.INSTANCE);

	IElementType EXCL = new IElementType("EXCL", MsilLanguage.INSTANCE);

	IElementType EQ = new IElementType("EQ", MsilLanguage.INSTANCE);

	IElementType GT = new IElementType("GT", MsilLanguage.INSTANCE);

	IElementType LT = new IElementType("LT", MsilLanguage.INSTANCE);

	IElementType RPAR = new IElementType("RPAR", MsilLanguage.INSTANCE);

	IElementType RBRACKET = new IElementType("RBRACKET", MsilLanguage.INSTANCE);
}
