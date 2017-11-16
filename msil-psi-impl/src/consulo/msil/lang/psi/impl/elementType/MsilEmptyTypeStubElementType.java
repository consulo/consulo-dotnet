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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetType;
import consulo.msil.lang.psi.impl.elementType.stub.MsilEmptyTypeStub;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public abstract class MsilEmptyTypeStubElementType extends AbstractMsilStubElementType<MsilEmptyTypeStub, DotNetType>
{
	public MsilEmptyTypeStubElementType(@NotNull @NonNls String debugName)
	{
		super(debugName);
	}

	@RequiredReadAction
	@Override
	public MsilEmptyTypeStub createStub(@NotNull DotNetType type, StubElement stubElement)
	{
		return new MsilEmptyTypeStub(stubElement, this);
	}

	@Override
	public void serialize(@NotNull MsilEmptyTypeStub msilEmptyTypeStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@NotNull
	@Override
	public MsilEmptyTypeStub deserialize(@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilEmptyTypeStub(stubElement, this);
	}
}
