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
import consulo.msil.lang.psi.MsilParameter;
import consulo.msil.lang.psi.impl.MsilParameterImpl;
import consulo.msil.lang.psi.impl.elementType.stub.MsilParameterStub;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilParameterStubElementType extends AbstractMsilStubElementType<MsilParameterStub, MsilParameter>
{
	public MsilParameterStubElementType()
	{
		super("MSIL_PARAMETER");
	}

	@NotNull
	@Override
	public MsilParameter createElement(@NotNull ASTNode astNode)
	{
		return new MsilParameterImpl(astNode);
	}

	@NotNull
	@Override
	public MsilParameter createPsi(@NotNull MsilParameterStub msilParameterStub)
	{
		return new MsilParameterImpl(msilParameterStub, this);
	}

	@Override
	public MsilParameterStub createStub(@NotNull MsilParameter msilParameter, StubElement stubElement)
	{
		String name = msilParameter.getName();
		return new MsilParameterStub(stubElement, this, name);
	}

	@Override
	public void serialize(@NotNull MsilParameterStub msilParameterStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilParameterStub.getName());
	}

	@NotNull
	@Override
	public MsilParameterStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef name = inputStream.readName();
		return new MsilParameterStub(stubElement, this, name);
	}
}
