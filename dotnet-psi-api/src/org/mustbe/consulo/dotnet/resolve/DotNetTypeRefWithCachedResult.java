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

package org.mustbe.consulo.dotnet.resolve;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 12-May-16
 */
public abstract class DotNetTypeRefWithCachedResult implements DotNetTypeRef
{
	private DotNetTypeResolveResult myResult;

	@NotNull
	@Override
	public final String getPresentableText()
	{
		return toString();
	}

	@NotNull
	@Override
	public final String getQualifiedText()
	{
		return toString();
	}

	@RequiredReadAction
	@NotNull
	@Override
	public final DotNetTypeResolveResult resolve()
	{
		if(myResult == null)
		{
			myResult = resolveResult();
		}
		return myResult;
	}

	@RequiredReadAction
	@NotNull
	@Override
	@Deprecated
	public final DotNetTypeResolveResult resolve(@Nullable("always null") @Deprecated PsiElement scope)
	{
		return resolve();
	}

	@RequiredReadAction
	@NotNull
	protected abstract DotNetTypeResolveResult resolveResult();

	@RequiredReadAction
	@NotNull
	public abstract String toString();
}
