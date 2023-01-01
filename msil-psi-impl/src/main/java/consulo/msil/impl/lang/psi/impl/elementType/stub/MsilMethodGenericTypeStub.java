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

import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import consulo.language.psi.stub.StubElement;
import consulo.msil.lang.psi.MsilMethodGenericType;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilMethodGenericTypeStub extends StubBase<MsilMethodGenericType>
{
	private final int myIndex;

	public MsilMethodGenericTypeStub(StubElement parent, IStubElementType elementType, int index)
	{
		super(parent, elementType);
		myIndex = index;
	}

	public int getIndex()
	{
		return myIndex;
	}
}
