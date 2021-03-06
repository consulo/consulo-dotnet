/*
 * Copyright 2013-2016 must-be.org
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

package consulo.msil.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.CachedValuesManager;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.resolve.DotNetTypeRef;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public abstract class MsilTypeImpl<T extends StubElement> extends MsilStubElementImpl<T> implements DotNetType
{
	protected MsilTypeImpl(@Nonnull ASTNode node)
	{
		super(node);
	}

	protected MsilTypeImpl(@Nonnull T stub, @Nonnull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@RequiredReadAction
	@Nonnull
	@Override
	public final DotNetTypeRef toTypeRef()
	{
		return CachedValuesManager.getProjectPsiDependentCache(this, MsilTypeImpl::toTypeRefImpl);
	}

	@RequiredReadAction
	@Nonnull
	protected abstract DotNetTypeRef toTypeRefImpl();

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitElement(this);
	}
}
