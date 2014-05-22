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
import org.mustbe.consulo.dotnet.psi.DotNetNativeType;
import org.mustbe.consulo.msil.lang.psi.impl.MsilNativeTypeImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilNativeTypeStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilNativeTypeStubElementType extends AbstractMsilStubElementType<MsilNativeTypeStub, DotNetNativeType>
{
	public MsilNativeTypeStubElementType()
	{
		super("MSIL_NATIVE_TYPE");
	}

	@NotNull
	@Override
	public DotNetNativeType createPsi(@NotNull ASTNode astNode)
	{
		return new MsilNativeTypeImpl(astNode);
	}

	@NotNull
	@Override
	public DotNetNativeType createPsi(@NotNull MsilNativeTypeStub msilNativeTypeStub)
	{
		return new MsilNativeTypeImpl(msilNativeTypeStub, this);
	}

	@Override
	public MsilNativeTypeStub createStub(
			@NotNull DotNetNativeType dotNetNativeType, StubElement stubElement)
	{
		return new MsilNativeTypeStub(stubElement, this);
	}

	@Override
	public void serialize(
			@NotNull MsilNativeTypeStub msilNativeTypeStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@NotNull
	@Override
	public MsilNativeTypeStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilNativeTypeStub(stubElement, this);
	}
}