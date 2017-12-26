/*
 * Copyright 2013-2015 must-be.org
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
import consulo.msil.lang.psi.MsilTypeParameterAttributeList;
import consulo.msil.lang.psi.impl.MsilTypeParameterAttributeListImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilTypeParameterAttributeListStub;

/**
 * @author VISTALL
 * @since 12.06.2015
 */
public class MsilTypeParameterAttributeListStubElementType extends AbstractMsilStubElementType<MsilTypeParameterAttributeListStub, MsilTypeParameterAttributeList>
{
	public MsilTypeParameterAttributeListStubElementType()
	{
		super("MSIL_TYPE_PARAMETER_ATTRIBUTE_LIST");
	}

	@NotNull
	@Override
	public MsilTypeParameterAttributeList createElement(@NotNull ASTNode astNode)
	{
		return new MsilTypeParameterAttributeListImpl(astNode);
	}

	@NotNull
	@Override
	public MsilTypeParameterAttributeList createPsi(@NotNull MsilTypeParameterAttributeListStub msilParameterAttributeListStub)
	{
		return new MsilTypeParameterAttributeListImpl(msilParameterAttributeListStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilTypeParameterAttributeListStub createStub(@NotNull MsilTypeParameterAttributeList list, StubElement stubElement)
	{
		String name = list.getGenericParameterName();
		return new MsilTypeParameterAttributeListStub(stubElement, this, name);
	}

	@Override
	public void serialize(@NotNull MsilTypeParameterAttributeListStub list, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(list.getGenericParameterName());
	}

	@NotNull
	@Override
	public MsilTypeParameterAttributeListStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef name = inputStream.readName();
		return new MsilTypeParameterAttributeListStub(stubElement, this, name);
	}
}
