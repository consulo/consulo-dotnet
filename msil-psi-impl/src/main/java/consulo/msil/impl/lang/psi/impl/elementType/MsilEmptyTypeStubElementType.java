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
import consulo.dotnet.psi.DotNetType;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.msil.impl.lang.psi.impl.elementType.stub.MsilEmptyTypeStub;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public abstract class MsilEmptyTypeStubElementType extends AbstractMsilStubElementType<MsilEmptyTypeStub, DotNetType>
{
	public MsilEmptyTypeStubElementType(@Nonnull @NonNls String debugName)
	{
		super(debugName);
	}

	@RequiredReadAction
	@Override
	public MsilEmptyTypeStub createStub(@Nonnull DotNetType type, StubElement stubElement)
	{
		return new MsilEmptyTypeStub(stubElement, this);
	}

	@Override
	public void serialize(@Nonnull MsilEmptyTypeStub msilEmptyTypeStub, @Nonnull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@Nonnull
	@Override
	public MsilEmptyTypeStub deserialize(@Nonnull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilEmptyTypeStub(stubElement, this);
	}
}
