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

package consulo.msil.lang.psi.impl.elementType;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetXXXAccessor;
import consulo.msil.lang.psi.MsilXXXAcessor;
import consulo.msil.lang.psi.impl.MsilXXXAccessorImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilXXXAccessorStub;

/**
 * @author VISTALL
 * @since 24.05.14
 */
public class MsilXXXAccessorStubElementType extends AbstractMsilStubElementType<MsilXXXAccessorStub, MsilXXXAcessor>
{
	public MsilXXXAccessorStubElementType()
	{
		super("MSIL_XXX_ACCESSOR");
	}

	@NotNull
	@Override
	public MsilXXXAcessor createElement(@NotNull ASTNode astNode)
	{
		return new MsilXXXAccessorImpl(astNode);
	}

	@NotNull
	@Override
	public MsilXXXAcessor createPsi(@NotNull MsilXXXAccessorStub msilXXXAccessorStub)
	{
		return new MsilXXXAccessorImpl(msilXXXAccessorStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilXXXAccessorStub createStub(@NotNull MsilXXXAcessor accessor, StubElement stubElement)
	{
		DotNetXXXAccessor.Kind accessorType = accessor.getAccessorKind();
		String name = accessor.getMethodName();
		return new MsilXXXAccessorStub(stubElement, this, accessorType == null ? -1 : accessorType.ordinal(), name);
	}

	@Override
	public void serialize(@NotNull MsilXXXAccessorStub stub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeVarInt(stub.getIndex());
		stubOutputStream.writeName(stub.getMethodName());
	}

	@NotNull
	@Override
	public MsilXXXAccessorStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		int i = inputStream.readVarInt();
		StringRef ref = inputStream.readName();
		return new MsilXXXAccessorStub(stubElement, this, i, ref);
	}
}
