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

import org.mustbe.consulo.dotnet.psi.DotNetXXXAccessor;
import org.mustbe.consulo.msil.lang.psi.MsilXXXAcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 24.05.14
 */
public class MsilXXXAccessorStub extends StubBase<MsilXXXAcessor>
{
	private int myIndex;
	private StringRef myName;

	public MsilXXXAccessorStub(StubElement parent, IStubElementType elementType, DotNetXXXAccessor.Kind accessor, String name)
	{
		this(parent, elementType, accessor == null ? -1 : accessor.ordinal(), StringRef.fromNullableString(name));
	}

	public MsilXXXAccessorStub(StubElement parent, IStubElementType elementType, int i, StringRef name)
	{
		super(parent, elementType);
		myIndex = i;
		assert myIndex != -1;
		myName = name;
	}

	public String getMethodName()
	{
		return StringRef.toString(myName);
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
