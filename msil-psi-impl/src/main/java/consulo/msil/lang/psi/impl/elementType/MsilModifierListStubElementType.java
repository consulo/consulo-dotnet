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

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import consulo.annotations.RequiredReadAction;
import consulo.msil.lang.psi.MsilModifierList;
import consulo.msil.lang.psi.impl.MsilModifierListImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilModifierListStub;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilModifierListStubElementType extends AbstractMsilStubElementType<MsilModifierListStub, MsilModifierList>
{
	public MsilModifierListStubElementType()
	{
		super("MSIL_MODIFIER_LIST");
	}

	@Nonnull
	@Override
	public MsilModifierList createElement(@Nonnull ASTNode astNode)
	{
		return new MsilModifierListImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilModifierList createPsi(@Nonnull MsilModifierListStub msilModifierListStub)
	{
		return new MsilModifierListImpl(msilModifierListStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilModifierListStub createStub(@Nonnull MsilModifierList msilModifierList, StubElement stubElement)
	{
		return new MsilModifierListStub(stubElement, this, msilModifierList);
	}

	@Override
	public void serialize(@Nonnull MsilModifierListStub msilModifierListStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeVarInt(msilModifierListStub.getModifiers());
	}

	@Nonnull
	@Override
	public MsilModifierListStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		int modifiers = inputStream.readVarInt();
		return new MsilModifierListStub(stubElement, this, modifiers);
	}
}
