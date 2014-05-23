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
import org.mustbe.consulo.msil.lang.psi.MsilModifierList;
import org.mustbe.consulo.msil.lang.psi.impl.MsilModifierListImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilModifierListStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;

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

	@NotNull
	@Override
	public MsilModifierList createPsi(@NotNull ASTNode astNode)
	{
		return new MsilModifierListImpl(astNode);
	}

	@NotNull
	@Override
	public MsilModifierList createPsi(@NotNull MsilModifierListStub msilModifierListStub)
	{
		return new MsilModifierListImpl(msilModifierListStub, this);
	}

	@Override
	public MsilModifierListStub createStub(@NotNull MsilModifierList msilModifierList, StubElement stubElement)
	{
		return new MsilModifierListStub(stubElement, this, msilModifierList);
	}

	@Override
	public void serialize(@NotNull MsilModifierListStub msilModifierListStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeInt(msilModifierListStub.getModifiers());
	}

	@NotNull
	@Override
	public MsilModifierListStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		int modifiers = inputStream.readInt();
		return new MsilModifierListStub(stubElement, this, modifiers);
	}
}
