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
import com.intellij.util.io.StringRef;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetType;
import consulo.msil.lang.psi.MsilCustomAttribute;
import consulo.msil.lang.psi.impl.MsilCustomAttributeImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilCustomAttributeStub;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilCustomAttributeStubElementType extends AbstractMsilStubElementType<MsilCustomAttributeStub, MsilCustomAttribute>
{
	public MsilCustomAttributeStubElementType()
	{
		super("MSIL_CUSTOM_ATTRIBTE");
	}

	@Nonnull
	@Override
	public MsilCustomAttribute createElement(@Nonnull ASTNode astNode)
	{
		return new MsilCustomAttributeImpl(astNode);
	}

	@Nonnull
	@Override
	public MsilCustomAttribute createPsi(@Nonnull MsilCustomAttributeStub msilCustomAttributeStub)
	{
		return new MsilCustomAttributeImpl(msilCustomAttributeStub, this);
	}

	@RequiredReadAction
	@Override
	public MsilCustomAttributeStub createStub(@Nonnull MsilCustomAttribute msilCustomAttribute, StubElement stubElement)
	{
		DotNetType type = msilCustomAttribute.getType();
		String ref = type == null ? null : type.getText();
		return new MsilCustomAttributeStub(stubElement, this, ref);
	}

	@Override
	public void serialize(@Nonnull MsilCustomAttributeStub msilCustomAttributeStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilCustomAttributeStub.getTypeRef());
	}

	@Nonnull
	@Override
	public MsilCustomAttributeStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef ref = inputStream.readName();
		return new MsilCustomAttributeStub(stubElement, this, ref);
	}
}
