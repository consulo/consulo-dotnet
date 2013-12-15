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

package org.mustbe.consulo.csharp.lang.psi.impl.stub.index;

import org.mustbe.consulo.csharp.lang.psi.CSharpNamespaceDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import com.intellij.psi.stubs.StubIndexKey;

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public interface CSharpIndexKeys
{
	StubIndexKey<String, CSharpNamespaceDeclaration> NAMESPACE_INDEX = StubIndexKey.createIndexKey("csharp.namespace.index");
	StubIndexKey<String, CSharpNamespaceDeclaration> NAMESPACE_BY_QNAME_INDEX = StubIndexKey.createIndexKey("csharp.namespace.by.qname.index");
	StubIndexKey<String, CSharpTypeDeclaration> TYPE_INDEX = StubIndexKey.createIndexKey("csharp.type.index");
	StubIndexKey<String, CSharpTypeDeclaration> TYPE_BY_QNAME_INDEX = StubIndexKey.createIndexKey("csharp.type.by.qname.index");
}
