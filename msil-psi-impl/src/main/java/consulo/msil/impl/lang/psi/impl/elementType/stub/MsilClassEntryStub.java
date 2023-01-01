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

package consulo.msil.impl.lang.psi.impl.elementType.stub;

import consulo.index.io.StringRef;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import consulo.language.psi.stub.StubElement;
import consulo.msil.lang.psi.MsilClassEntry;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilClassEntryStub extends StubBase<MsilClassEntry>
{
	private final String myNamespace;
	private final String myName;
	private final String myVmQName;

	public MsilClassEntryStub(StubElement parent, IStubElementType elementType, String namespace, String name, String vmQNamed)
	{
		super(parent, elementType);
		myName = name;
		myNamespace = namespace;
		myVmQName = vmQNamed;
	}

	public MsilClassEntryStub(StubElement parent, IStubElementType elementType, StringRef namespace, StringRef name, StringRef vmQName)
	{
		this(parent, elementType, StringRef.toString(namespace), StringRef.toString(name), StringRef.toString(vmQName));
	}

	public String getName()
	{
		return myName;
	}

	public String getVmQName()
	{
		return myVmQName;
	}

	public String getNamespace()
	{
		return myNamespace;
	}

	public boolean isNested()
	{
		return getParentStub() instanceof MsilClassEntryStub;
	}
}
