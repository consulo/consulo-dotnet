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
import consulo.dotnet.psi.DotNetNativeType;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.impl.lang.psi.MsilTokenSets;
import consulo.msil.impl.lang.psi.impl.MsilNativeTypeImpl;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilNativeTypeStub;
import consulo.util.collection.ArrayUtil;

import java.io.IOException;

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

	@Override
	public DotNetNativeType createElement(ASTNode astNode)
	{
		return new MsilNativeTypeImpl(astNode);
	}

	@Override
	public DotNetNativeType createPsi(MsilNativeTypeStub msilNativeTypeStub)
	{
		return new MsilNativeTypeImpl(msilNativeTypeStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilNativeTypeStub createStub(DotNetNativeType dotNetNativeType, StubElement stubElement)
	{
		return new MsilNativeTypeStub(stubElement, this, dotNetNativeType.getTypeElement().getNode().getElementType());
	}

	@Override
	public void serialize(MsilNativeTypeStub msilNativeTypeStub, StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeVarInt(ArrayUtil.indexOf(MsilTokenSets.NATIVE_TYPES_AS_ARRAY, msilNativeTypeStub.getTypeElementType()));
	}

	@Override
	public MsilNativeTypeStub deserialize(StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		IElementType elementType = MsilTokenSets.NATIVE_TYPES_AS_ARRAY[inputStream.readVarInt()];
		return new MsilNativeTypeStub(stubElement, this, elementType);
	}
}
