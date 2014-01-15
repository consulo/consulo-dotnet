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

package org.mustbe.consulo.csharp.lang.psi.impl.stub;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpMethodDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpStubElements;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.typeStub.CSharpStubTypeInfo;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class CSharpMethodStub extends MemberStub<CSharpMethodDeclaration>
{
	private final CSharpStubTypeInfo myReturnType;

	public CSharpMethodStub(StubElement parent, @Nullable StringRef name, @Nullable StringRef qname, int modifierMask, CSharpStubTypeInfo returnType)
	{
		super(parent, CSharpStubElements.METHOD_DECLARATION, name, qname, modifierMask);
		myReturnType = returnType;
	}

	public CSharpMethodStub(StubElement parent, IStubElementType elementType, @Nullable StringRef name, StringRef qname, int modifierMask,
			CSharpStubTypeInfo returnType)
	{
		super(parent, elementType, name, qname, modifierMask);
		myReturnType = returnType;
	}

	@NotNull
	public CSharpStubTypeInfo getReturnType()
	{
		return myReturnType;
	}
}
