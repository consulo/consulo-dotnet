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
import consulo.dotnet.resolve.DotNetPsiSearcher;
import consulo.msil.lang.psi.MsilUserType;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceTypeStub extends StubBase<MsilUserType>
{
	private final DotNetPsiSearcher.TypeResoleKind myTypeResoleKind;
	private final StringRef myReferenceText;

	public MsilReferenceTypeStub(StubElement parent,
			IStubElementType elementType,
			DotNetPsiSearcher.TypeResoleKind typeResoleKind,
			String referenceText)
	{
		super(parent, elementType);
		myTypeResoleKind = typeResoleKind;
		myReferenceText = StringRef.fromNullableString(referenceText);
	}

	public MsilReferenceTypeStub(StubElement parent,
			IStubElementType elementType,
			DotNetPsiSearcher.TypeResoleKind typeResoleKind,
			StringRef referenceText)
	{
		super(parent, elementType);
		myTypeResoleKind = typeResoleKind;
		myReferenceText = referenceText;
	}

	public String getReferenceText()
	{
		return StringRef.toString(myReferenceText);
	}

	public DotNetPsiSearcher.TypeResoleKind getTypeResoleKind()
	{
		return myTypeResoleKind;
	}
}
