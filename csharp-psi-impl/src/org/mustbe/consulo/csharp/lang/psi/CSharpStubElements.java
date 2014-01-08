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

import org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes.CSharpConstructorStubElementType;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes.CSharpEnumConstantStubElementType;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes.CSharpEventElementType;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes.CSharpFieldStubElementType;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes.CSharpFileStubElementType;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes.CSharpMethodStubElementType;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes.CSharpNamespaceStubElementType;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes.CSharpPropertyElementType;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes.CSharpTypeStubElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public interface CSharpStubElements
{
	CSharpFileStubElementType FILE = new CSharpFileStubElementType();
	CSharpNamespaceStubElementType NAMESPACE_DECLARATION = new CSharpNamespaceStubElementType();
	CSharpTypeStubElementType TYPE_DECLARATION = new CSharpTypeStubElementType();
	CSharpMethodStubElementType METHOD_DECLARATION = new CSharpMethodStubElementType();
	CSharpConstructorStubElementType CONSTRUCTOR_DECLARATION = new CSharpConstructorStubElementType();
	CSharpPropertyElementType PROPERTY_DECLARATION = new CSharpPropertyElementType();
	CSharpEventElementType EVENT_DECLARATION = new CSharpEventElementType();
	CSharpFieldStubElementType FIELD_DECLARATION = new CSharpFieldStubElementType();
	CSharpEnumConstantStubElementType ENUM_CONSTANT_DECLARATION = new CSharpEnumConstantStubElementType();

	TokenSet QUALIFIED_MEMBERS = TokenSet.create(CSharpStubElements.NAMESPACE_DECLARATION, CSharpStubElements.TYPE_DECLARATION,
			CSharpStubElements.METHOD_DECLARATION, CSharpStubElements.CONSTRUCTOR_DECLARATION, CSharpStubElements.PROPERTY_DECLARATION,
			CSharpStubElements.EVENT_DECLARATION, CSharpStubElements.FIELD_DECLARATION, CSharpStubElements.ENUM_CONSTANT_DECLARATION);
}
