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

import consulo.dotnet.psi.DotNetXAccessor;
import consulo.index.io.StringRef;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import consulo.language.psi.stub.StubElement;
import consulo.msil.lang.psi.MsilXAcessor;

/**
 * @author VISTALL
 * @since 24.05.14
 */
public class MsilXAccessorStub extends StubBase<MsilXAcessor>
{
	private int myIndex;
	private String myName;

	public MsilXAccessorStub(StubElement parent, IStubElementType elementType, int index, String name)
	{
		super(parent, elementType);
		myIndex = index;
		myName = name;
	}

	public MsilXAccessorStub(StubElement parent, IStubElementType elementType, int i, StringRef name)
	{
		this(parent, elementType, i, StringRef.toString(name));
	}

	public String getMethodName()
	{
		return myName;
	}

	public int getIndex()
	{
		return myIndex;
	}

	public DotNetXAccessor.Kind getAccessorType()
	{
		return myIndex == -1 ? null : DotNetXAccessor.Kind.VALUES[myIndex];
	}
}
