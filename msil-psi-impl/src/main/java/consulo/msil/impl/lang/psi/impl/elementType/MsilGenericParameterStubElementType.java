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

package consulo.msil.impl.lang.psi.impl.elementType;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.index.io.StringRef;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.lang.psi.MsilGenericParameter;
import consulo.msil.lang.psi.MsilUserType;
import consulo.msil.impl.lang.psi.impl.MsilGenericParameterImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilGenericParameterStub;

import java.io.IOException;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilGenericParameterStubElementType extends AbstractMsilStubElementType<MsilGenericParameterStub, MsilGenericParameter>
{
	public MsilGenericParameterStubElementType()
	{
		super("MSIL_GENERIC_PARAMETER");
	}

	@Override
	public DotNetGenericParameter createElement(ASTNode astNode)
	{
		return new MsilGenericParameterImpl(astNode);
	}

	@Override
	public MsilGenericParameter createPsi(MsilGenericParameterStub msilGenericParameterStub)
	{
		return new MsilGenericParameterImpl(msilGenericParameterStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilGenericParameterStub createStub(MsilGenericParameter parameter, StubElement stubElement)
	{
		String name = parameter.getName();
		int mod = MsilGenericParameterStub.toModifiers(parameter);
		MsilUserType.Target typeKind = parameter.getTarget();
		return new MsilGenericParameterStub(stubElement, this, name, mod, typeKind);
	}

	@Override
	public void serialize(MsilGenericParameterStub msilGenericParameterStub, StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilGenericParameterStub.getName());
		stubOutputStream.writeVarInt(msilGenericParameterStub.getModifierMask());
		stubOutputStream.writeVarInt(msilGenericParameterStub.getTargetIndex());
	}

	@Override
	public MsilGenericParameterStub deserialize(StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef ref = inputStream.readName();
		int mod = inputStream.readVarInt();
		int typeKindIndex = inputStream.readVarInt();
		return new MsilGenericParameterStub(stubElement, this, ref, mod, typeKindIndex);
	}
}
