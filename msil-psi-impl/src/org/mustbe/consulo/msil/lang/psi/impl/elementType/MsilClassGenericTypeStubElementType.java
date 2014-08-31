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
import org.mustbe.consulo.msil.lang.psi.MsilClassGenericType;
import org.mustbe.consulo.msil.lang.psi.impl.MsilClassGenericTypeImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilClassGenericTypeStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilClassGenericTypeStubElementType extends AbstractMsilStubElementType<MsilClassGenericTypeStub, MsilClassGenericType>
{
	public MsilClassGenericTypeStubElementType()
	{
		super("MSIL_CLASS_GENERIC_TYPE");
	}

	@NotNull
	@Override
	public MsilClassGenericType createElement(@NotNull ASTNode astNode)
	{
		return new MsilClassGenericTypeImpl(astNode);
	}

	@NotNull
	@Override
	public MsilClassGenericType createPsi(@NotNull MsilClassGenericTypeStub msilClassGenericTypeStub)
	{
		return new MsilClassGenericTypeImpl(msilClassGenericTypeStub, this);
	}

	@Override
	public MsilClassGenericTypeStub createStub(@NotNull MsilClassGenericType msilClassGenericType, StubElement stubElement)
	{
		String name = msilClassGenericType.getGenericName();
		return new MsilClassGenericTypeStub(stubElement, this, name);
	}

	@Override
	public void serialize(@NotNull MsilClassGenericTypeStub msilClassGenericTypeStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilClassGenericTypeStub.getName());
	}

	@NotNull
	@Override
	public MsilClassGenericTypeStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef name = inputStream.readName();
		return new MsilClassGenericTypeStub(stubElement, this, name);
	}
}
