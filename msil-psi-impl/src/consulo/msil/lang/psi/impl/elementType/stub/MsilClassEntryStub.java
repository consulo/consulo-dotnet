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

package consulo.msil.lang.psi.impl.elementType.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import consulo.msil.lang.psi.MsilClassEntry;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilClassEntryStub extends StubBase<MsilClassEntry>
{
	private final StringRef myNamespace;
	private final StringRef myName;
	private final StringRef myVmQName;

	public MsilClassEntryStub(StubElement parent, IStubElementType elementType, String namespace, String name, String vmQNamed)
	{
		super(parent, elementType);
		myName = StringRef.fromNullableString(name);
		myNamespace = StringRef.fromNullableString(namespace);
		myVmQName = StringRef.fromNullableString(vmQNamed);
	}

	public MsilClassEntryStub(StubElement parent, IStubElementType elementType, StringRef namespace, StringRef name, StringRef vmQName)
	{
		super(parent, elementType);
		myName = name;
		myNamespace = namespace;
		myVmQName = vmQName;
	}

	public String getName()
	{
		return StringRef.toString(myName);
	}

	public String getVmQName()
	{
		return StringRef.toString(myVmQName);
	}

	public String getNamespace()
	{
		return StringRef.toString(myNamespace);
	}

	public boolean isNested()
	{
		return getParentStub() instanceof MsilClassEntryStub;
	}
}
