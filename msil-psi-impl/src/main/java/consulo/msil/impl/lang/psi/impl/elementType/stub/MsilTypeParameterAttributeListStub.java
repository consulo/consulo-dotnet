/*
 * Copyright 2013-2015 must-be.org
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
import consulo.msil.lang.psi.MsilParameterAttributeList;

/**
 * @author VISTALL
 * @since 12.06.2015
 */
public class MsilTypeParameterAttributeListStub extends StubBase<MsilParameterAttributeList>
{
	private final String myGenericParameterName;

	public MsilTypeParameterAttributeListStub(StubElement parent, IStubElementType elementType, String genericParameterName)
	{
		super(parent, elementType);
		myGenericParameterName = genericParameterName;
	}

	public MsilTypeParameterAttributeListStub(StubElement parent, IStubElementType elementType, StringRef genericParameterName)
	{
		this(parent, elementType, StringRef.toString(genericParameterName));
	}

	public String getGenericParameterName()
	{
		return myGenericParameterName;
	}
}
