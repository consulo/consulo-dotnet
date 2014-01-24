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

package org.mustbe.consulo.csharp.lang.psi;

import org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes.*;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public interface CSharpStubElements
{
	CSharpFileStubElementType FILE = new CSharpFileStubElementType();
	CSharpMacroStubElementType MACRO_FILE = new CSharpMacroStubElementType();
	CSharpNamespaceStubElementType NAMESPACE_DECLARATION = new CSharpNamespaceStubElementType();
	CSharpTypeStubElementType TYPE_DECLARATION = new CSharpTypeStubElementType();
	CSharpMethodStubElementType METHOD_DECLARATION = new CSharpMethodStubElementType();
	CSharpConstructorStubElementType CONSTRUCTOR_DECLARATION = new CSharpConstructorStubElementType();
	CSharpConversionMethodStubElementType CONVERSION_METHOD_DECLARATION = new CSharpConversionMethodStubElementType();
	CSharpPropertyElementType PROPERTY_DECLARATION = new CSharpPropertyElementType();
	CSharpEventElementType EVENT_DECLARATION = new CSharpEventElementType();
	CSharpFieldStubElementType FIELD_DECLARATION = new CSharpFieldStubElementType();
	CSharpEnumConstantStubElementType ENUM_CONSTANT_DECLARATION = new CSharpEnumConstantStubElementType();
	CSharpTypeListElementType EXTENDS_LIST = new CSharpTypeListElementType("EXTENDS_LIST");
	CSharpParameterListStubElementType PARAMETER_LIST = new CSharpParameterListStubElementType();
	CSharpParameterStubElementType PARAMETER = new CSharpParameterStubElementType();
	CSharpUsingNamespaceListStubElementType USING_NAMESPACE_LIST = new CSharpUsingNamespaceListStubElementType();
	CSharpUsingNamespaceStatementStubElementType USING_NAMESPACE_STATEMENT = new CSharpUsingNamespaceStatementStubElementType();
	CSharpGenericParameterListStubElementType GENERIC_PARAMETER_LIST = new CSharpGenericParameterListStubElementType();
	CSharpGenericParameterStubElementType GENERIC_PARAMETER = new CSharpGenericParameterStubElementType();

	TokenSet QUALIFIED_MEMBERS = TokenSet.create(NAMESPACE_DECLARATION, TYPE_DECLARATION,
			METHOD_DECLARATION, CONSTRUCTOR_DECLARATION, PROPERTY_DECLARATION,
			EVENT_DECLARATION, FIELD_DECLARATION, ENUM_CONSTANT_DECLARATION, CONVERSION_METHOD_DECLARATION);

	TokenSet QUALIFIED_MEMBERS_WITH_USING = TokenSet.orSet(QUALIFIED_MEMBERS, TokenSet.create(USING_NAMESPACE_LIST));
}
