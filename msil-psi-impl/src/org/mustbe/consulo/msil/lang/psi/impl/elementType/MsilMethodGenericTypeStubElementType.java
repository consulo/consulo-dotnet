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

package org.mustbe.consulo.msil.lang.psi.impl.elementType;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.msil.lang.psi.MsilMethodGenericType;
import org.mustbe.consulo.msil.lang.psi.impl.MsilMethodGenericTypeImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilMethodGenericTypeStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilMethodGenericTypeStubElementType extends AbstractMsilStubElementType<MsilMethodGenericTypeStub, MsilMethodGenericType>
{
	public MsilMethodGenericTypeStubElementType()
	{
		super("MSIL_METHOD_GENERIC_TYPE");
	}

	@NotNull
	@Override
	public MsilMethodGenericType createPsi(@NotNull ASTNode astNode)
	{
		return new MsilMethodGenericTypeImpl(astNode);
	}

	@NotNull
	@Override
	public MsilMethodGenericType createPsi(@NotNull MsilMethodGenericTypeStub msilMethodGenericTypeStub)
	{
		return new MsilMethodGenericTypeImpl(msilMethodGenericTypeStub, this);
	}

	@Override
	public MsilMethodGenericTypeStub createStub(@NotNull MsilMethodGenericType msilMethodGenericType, StubElement stubElement)
	{
		int index = msilMethodGenericType.getIndex();
		return new MsilMethodGenericTypeStub(stubElement, this, index);
	}

	@Override
	public void serialize(@NotNull MsilMethodGenericTypeStub stub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeInt(stub.getIndex());
	}

	@NotNull
	@Override
	public MsilMethodGenericTypeStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		int index = inputStream.readInt();
		return new MsilMethodGenericTypeStub(stubElement, this, index);
	}
}
