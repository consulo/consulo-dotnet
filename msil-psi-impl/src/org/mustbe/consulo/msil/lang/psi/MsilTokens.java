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

	IElementType QIDENTIFIER = new IElementType("IDENTIFIER", MsilLanguage.INSTANCE);

	IElementType _CLASS_KEYWORD = new IElementType("_CLASS_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _METHOD_KEYWORD = new IElementType("_METHOD_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _FIELD_KEYWORD = new IElementType("_FIELD_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _PROPERTY_KEYWORD = new IElementType("_PROPERTY_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _EVENT_KEYWORD = new IElementType("_EVENT_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _ASSEMBLY_KEYWORD = new IElementType("_ASSEMBLY_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _CUSTOM_KEYWORD = new IElementType("_CUSTOM_KEYWORD", MsilLanguage.INSTANCE);

	IElementType _PARAM_KEYWORD = new IElementType("_PARAM_KEYWORD", MsilLanguage.INSTANCE);

	IElementType CLASS_KEYWORD = new IElementType("CLASS_KEYWORD", MsilLanguage.INSTANCE);

	IElementType VALUETYPE_KEYWORD = new IElementType("CLASS_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INT32_KEYWORD = new IElementType("INT32_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INT64_KEYWORD = new IElementType("INT64_KEYWORD", MsilLanguage.INSTANCE);

	IElementType VOID_KEYWORD = new IElementType("VOID_KEYWORD", MsilLanguage.INSTANCE);

	IElementType BOOL_KEYWORD = new IElementType("BOOL_KEYWORD", MsilLanguage.INSTANCE);

	IElementType OBJECT_KEYWORD = new IElementType("OBJECT_KEYWORD", MsilLanguage.INSTANCE);

	IElementType EXTENDS_KEYWORD = new IElementType("EXTENDS_KEYWORD", MsilLanguage.INSTANCE);

	IElementType STRING_KEYWORD = new IElementType("STRING_KEYWORD", MsilLanguage.INSTANCE);

	IElementType CHAR_KEYWORD = new IElementType("CHAR_KEYWORD", MsilLanguage.INSTANCE);

	IElementType UINT64_KEYWORD = new IElementType("UINT64_KEYWORD", MsilLanguage.INSTANCE);

	IElementType FLOAT_KEYWORD = new IElementType("FLOAT_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INT8_KEYWORD = new IElementType("INT8_KEYWORD", MsilLanguage.INSTANCE);

	IElementType UINT8_KEYWORD = new IElementType("UINT8_KEYWORD", MsilLanguage.INSTANCE);

	IElementType FLOAT64_KEYWORD = new IElementType("FLOAT64_KEYWORD", MsilLanguage.INSTANCE);

	IElementType UINT32_KEYWORD = new IElementType("UINT32_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INT16_KEYWORD = new IElementType("INT16_KEYWORD", MsilLanguage.INSTANCE);

	IElementType UINT16_KEYWORD = new IElementType("UINT16_KEYWORD", MsilLanguage.INSTANCE);

	IElementType IMPLEMENTS_KEYWORD = new IElementType("IMPLEMENTS_KEYWORD", MsilLanguage.INSTANCE);

	IElementType PUBLIC_KEYWORD = new IElementType("PUBLIC_KEYWORD", MsilLanguage.INSTANCE);

	IElementType STATIC_KEYWORD = new IElementType("STATIC_KEYWORD", MsilLanguage.INSTANCE);

	IElementType ABSTRACT_KEYWORD = new IElementType("ABSTRACT_KEYWORD", MsilLanguage.INSTANCE);

	IElementType FINAL_KEYWORD = new IElementType("FINAL_KEYWORD", MsilLanguage.INSTANCE);

	IElementType PRIVATE_KEYWORD = new IElementType("PRIVATE_KEYWORD", MsilLanguage.INSTANCE);

	IElementType LITERAL_KEYWORD = new IElementType("LITERAL_KEYWORD", MsilLanguage.INSTANCE);

	IElementType ASSEMBLY_KEYWORD = new IElementType("ASSEMBLY_KEYWORD", MsilLanguage.INSTANCE);

	IElementType HIDEBYSIG_KEYWORD = new IElementType("HIDEBYSIG_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INITONLY_KEYWORD = new IElementType("INITONLY_KEYWORD", MsilLanguage.INSTANCE);

	IElementType VIRTUAL_KEYWORD = new IElementType("VIRTUAL_KEYWORD", MsilLanguage.INSTANCE);

	IElementType PROTECTED_KEYWORD = new IElementType("PROTECTED_KEYWORD", MsilLanguage.INSTANCE);

	IElementType VALUE_KEYWORD = new IElementType("VALUE_KEYWORD", MsilLanguage.INSTANCE);

	IElementType BRACKET_OUT_KEYWORD = new IElementType("BRACKET_OUT_KEYWORD", MsilLanguage.INSTANCE);

	IElementType INTERFACE_KEYWORD = new IElementType("INTERFACE_KEYWORD", MsilLanguage.INSTANCE);

	IElementType SERIALIZABLE_KEYWORD = new IElementType("SERIALIZABLE_KEYWORD", MsilLanguage.INSTANCE);

	IElementType SEALED_KEYWORD = new IElementType("SEALED_KEYWORD", MsilLanguage.INSTANCE);

	IElementType SPECIALNAME_KEYWORD = new IElementType("SPECIALNAME_KEYWORD", MsilLanguage.INSTANCE);

	IElementType PERC = new IElementType("PERC", MsilLanguage.INSTANCE);

	IElementType AND = new IElementType("AND", MsilLanguage.INSTANCE);

	IElementType COMMA = new IElementType("COMMA", MsilLanguage.INSTANCE);

	IElementType LBRACE = new IElementType("LBRACE", MsilLanguage.INSTANCE);

	IElementType RBRACE = new IElementType("RBRACE", MsilLanguage.INSTANCE);

	IElementType BLOCK_COMMENT = new IElementType("BLOCK_COMMENT", MsilLanguage.INSTANCE);

	IElementType LINE_COMMENT = new IElementType("LINE_COMMENT", MsilLanguage.INSTANCE);

	IElementType LBRACKET = new IElementType("LBRACKET", MsilLanguage.INSTANCE);

	IElementType LPAR = new IElementType("LPAR", MsilLanguage.INSTANCE);

	IElementType EQ = new IElementType("EQ", MsilLanguage.INSTANCE);

	IElementType GT = new IElementType("GT", MsilLanguage.INSTANCE);

	IElementType LT = new IElementType("LT", MsilLanguage.INSTANCE);

	IElementType RPAR = new IElementType("RPAR", MsilLanguage.INSTANCE);

	IElementType RBRACKET = new IElementType("RBRACKET", MsilLanguage.INSTANCE);
}
