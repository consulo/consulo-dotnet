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
import consulo.dotnet.psi.DotNetXXXAccessor;
import consulo.msil.lang.psi.MsilXXXAcessor;

/**
 * @author VISTALL
 * @since 24.05.14
 */
public class MsilXXXAccessorStub extends StubBase<MsilXXXAcessor>
{
	private int myIndex;
	private String myName;

	public MsilXXXAccessorStub(StubElement parent, IStubElementType elementType, int index, String name)
	{
		super(parent, elementType);
		myIndex = index;
		myName = name;
	}

	public MsilXXXAccessorStub(StubElement parent, IStubElementType elementType, int i, StringRef name)
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

	public DotNetXXXAccessor.Kind getAccessorType()
	{
		return myIndex == -1 ? null : DotNetXXXAccessor.Kind.VALUES[myIndex];
	}
}
