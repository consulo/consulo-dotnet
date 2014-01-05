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

package org.mustbe.consulo.dotnet.psi.stub.index;

import org.mustbe.consulo.dotnet.psi.DotNetEventDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetFieldDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetPropertyDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import com.intellij.psi.stubs.StubIndexKey;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public interface DotNetIndexKeys
{
	StubIndexKey<String, DotNetNamedElement> MEMBER_BY_NAMESPACE_QNAME_INDEX = StubIndexKey.createIndexKey(".net.member.by.namespace.qname.index");

	StubIndexKey<String, DotNetNamespaceDeclaration> NAMESPACE_BY_QNAME_INDEX = StubIndexKey.createIndexKey(".net.namespace.by.qname.index");
	StubIndexKey<String, DotNetMethodDeclaration> METHOD_INDEX = StubIndexKey.createIndexKey(".net.method.index");
	StubIndexKey<String, DotNetMethodDeclaration> METHOD_BY_QNAME_INDEX = StubIndexKey.createIndexKey(".net.method.by.qname.index");
	StubIndexKey<String, DotNetTypeDeclaration> TYPE_INDEX = StubIndexKey.createIndexKey(".net.type.index");
	StubIndexKey<String, DotNetTypeDeclaration> TYPE_BY_QNAME_INDEX = StubIndexKey.createIndexKey(".net.type.by.qname.index");

	StubIndexKey<String, DotNetEventDeclaration> EVENT_INDEX = StubIndexKey.createIndexKey(".net.event.index");
	StubIndexKey<String, DotNetPropertyDeclaration> PROPERTY_INDEX = StubIndexKey.createIndexKey(".net.property.index");
	StubIndexKey<String, DotNetFieldDeclaration> FIELD_INDEX = StubIndexKey.createIndexKey(".net.field.index");
}
