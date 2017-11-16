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
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetGenericParameterList;
import consulo.msil.lang.psi.impl.MsilGenericParameterListImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilGenericParameterListStub;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilGenericParameterListStubElementType extends AbstractMsilStubElementType<MsilGenericParameterListStub, DotNetGenericParameterList>
{
	public MsilGenericParameterListStubElementType()
	{
		super("MSIL_GENERIC_PARAMETER_LIST");
	}

	@NotNull
	@Override
	public DotNetGenericParameterList createElement(@NotNull ASTNode astNode)
	{
		return new MsilGenericParameterListImpl(astNode);
	}

	@NotNull
	@Override
	public DotNetGenericParameterList createPsi(@NotNull MsilGenericParameterListStub msilGenericParameterListStub)
	{
		return new MsilGenericParameterListImpl(msilGenericParameterListStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilGenericParameterListStub createStub(@NotNull DotNetGenericParameterList dotNetGenericParameterList, StubElement stubElement)
	{
		return new MsilGenericParameterListStub(stubElement, this);
	}

	@Override
	public void serialize(@NotNull MsilGenericParameterListStub msilGenericParameterListStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@NotNull
	@Override
	public MsilGenericParameterListStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilGenericParameterListStub(stubElement, this);
	}
}
