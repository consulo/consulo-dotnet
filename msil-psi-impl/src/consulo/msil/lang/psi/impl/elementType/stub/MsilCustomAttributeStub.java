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
import consulo.msil.lang.psi.MsilCustomAttribute;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilCustomAttributeStub extends StubBase<MsilCustomAttribute>
{
	private final StringRef myTypeRef;

	public MsilCustomAttributeStub(StubElement parent, IStubElementType elementType, StringRef typeRef)
	{
		super(parent, elementType);
		myTypeRef = typeRef;
	}

	public MsilCustomAttributeStub(StubElement parent, IStubElementType elementType, String typeRef)
	{
		super(parent, elementType);
		myTypeRef = StringRef.fromNullableString(typeRef);
	}

	@Nullable
	public String getTypeRef()
	{
		return StringRef.toString(myTypeRef);
	}
}