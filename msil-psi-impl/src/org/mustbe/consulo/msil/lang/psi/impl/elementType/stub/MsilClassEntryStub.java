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

package org.mustbe.consulo.msil.lang.psi.impl.elementType.stub;

import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilClassEntryStub extends StubBase<MsilClassEntry>
{
	private final StringRef myNamespace;
	private final StringRef myName;
	private final boolean myNested;

	public MsilClassEntryStub(StubElement parent, IStubElementType elementType, String namespace, String name, boolean nested)
	{
		super(parent, elementType);
		myName = StringRef.fromNullableString(name);
		myNamespace = StringRef.fromNullableString(namespace);
		myNested = nested;
	}

	public MsilClassEntryStub(StubElement parent, IStubElementType elementType, StringRef namespace, StringRef name, boolean nested)
	{
		super(parent, elementType);
		myName = name;
		myNamespace = namespace;
		myNested = nested;
	}

	public String getName()
	{
		return StringRef.toString(myName);
	}

	public String getNamespace()
	{
		return StringRef.toString(myNamespace);
	}

	public boolean isNested()
	{
		return myNested;
	}
}
