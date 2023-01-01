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

package consulo.msil.impl.lang.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetType;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.util.LanguageCachedValueUtil;

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
		return LanguageCachedValueUtil.getProjectPsiDependentCache(this, MsilTypeImpl::toTypeRefImpl);
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
