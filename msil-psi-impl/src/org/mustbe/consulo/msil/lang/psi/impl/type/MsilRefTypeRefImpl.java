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

package org.mustbe.consulo.msil.lang.psi.impl.type;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.resolve.DotNetRefTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilRefTypeRefImpl extends DotNetTypeRef.Delegate implements DotNetRefTypeRef
{
	public MsilRefTypeRefImpl(DotNetTypeRef typeRef)
	{
		super(typeRef);
	}

	@NotNull
	@Override
	public String getQualifiedText()
	{
		return super.getQualifiedText() + "&";
	}

	@NotNull
	@Override
	public String getPresentableText()
	{
		return super.getPresentableText() + "&";
	}

	@NotNull
	@Override
	public DotNetTypeRef getInnerTypeRef()
	{
		return getDelegate();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MsilRefTypeRefImpl && getInnerTypeRef().equals(((MsilRefTypeRefImpl) obj).getInnerTypeRef());
	}
}
