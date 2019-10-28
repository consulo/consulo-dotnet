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

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetXAccessor;
import consulo.msil.lang.psi.MsilXAcessor;
import consulo.msil.lang.psi.impl.MsilXAccessorImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilXAccessorStub;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 24.05.14
 */
public class MsilXAccessorStubElementType extends AbstractMsilStubElementType<MsilXAccessorStub, MsilXAcessor>
{
	public MsilXAccessorStubElementType()
	{
		super("MSIL_XACCESSOR");
	}

	@Nonnull
	@Override
	public MsilXAcessor createElement(@Nonnull ASTNode astNode)
	{
		return new MsilXAccessorImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilXAcessor createPsi(@Nonnull MsilXAccessorStub msilXAccessorStub)
	{
		return new MsilXAccessorImpl(msilXAccessorStub, this);
	}

	@Nonnull
	@RequiredReadAction
	@Override
	public MsilXAccessorStub createStub(@Nonnull MsilXAcessor accessor, StubElement stubElement)
	{
		DotNetXAccessor.Kind accessorType = accessor.getAccessorKind();
		String name = accessor.getMethodName();
		return new MsilXAccessorStub(stubElement, this, accessorType == null ? -1 : accessorType.ordinal(), name);
	}

	@Override
	public void serialize(@Nonnull MsilXAccessorStub stub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeVarInt(stub.getIndex());
		stubOutputStream.writeName(stub.getMethodName());
	}

	@Nonnull
	@Override
	public MsilXAccessorStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		int i = inputStream.readVarInt();
		StringRef ref = inputStream.readName();
		return new MsilXAccessorStub(stubElement, this, i, ref);
	}
}
