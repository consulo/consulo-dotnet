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

import org.jetbrains.annotations.Nullable;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import consulo.msil.lang.psi.MsilParameter;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilParameterStub extends StubBase<MsilParameter>
{
	private final String myName;

	public MsilParameterStub(StubElement parent, IStubElementType elementType, String name)
	{
		super(parent, elementType);
		myName = name;
	}

	@Nullable
	public String getName()
	{
		return myName;
	}
}
