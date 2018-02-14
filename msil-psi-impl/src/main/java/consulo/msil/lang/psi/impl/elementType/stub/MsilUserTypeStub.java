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

import javax.annotation.Nonnull;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import consulo.msil.lang.psi.MsilUserType;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilUserTypeStub extends StubBase<MsilUserType>
{
	private final MsilUserType.Target myTarget;
	private final String myReferenceText;

	public MsilUserTypeStub(StubElement parent, IStubElementType elementType, MsilUserType.Target target, String referenceText)
	{
		super(parent, elementType);
		myTarget = target;
		myReferenceText = referenceText;
	}

	public MsilUserTypeStub(StubElement parent, IStubElementType elementType, MsilUserType.Target target, StringRef referenceText)
	{
		this(parent, elementType, target, StringRef.toString(referenceText));
	}

	public String getReferenceText()
	{
		return myReferenceText;
	}

	@Nonnull
	public MsilUserType.Target getTarget()
	{
		return myTarget;
	}
}
