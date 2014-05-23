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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetElement;
import org.mustbe.consulo.msil.MsilLanguage;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public abstract class AbstractMsilStubElementType<T extends StubElement, E extends DotNetElement> extends IStubElementType<T, E>
{
	public AbstractMsilStubElementType(@NotNull @NonNls String debugName)
	{
		super(debugName, MsilLanguage.INSTANCE);
	}

	@NotNull
	public abstract E createPsi(@NotNull ASTNode astNode);

	@Override
	@NotNull
	public abstract E createPsi(@NotNull T t);

	@NotNull
	@Override
	public String getExternalId()
	{
		return "msil." + toString();
	}


	@Override
	public void indexStub(@NotNull T t, @NotNull IndexSink indexSink)
	{

	}
}
