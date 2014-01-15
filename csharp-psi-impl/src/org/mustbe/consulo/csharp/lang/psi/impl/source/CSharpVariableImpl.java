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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;

/**
 * @author VISTALL
 * @since 06.01.14.
 */
public abstract class CSharpVariableImpl extends CSharpMemberImpl implements DotNetVariable
{
	private CachedValue<DotNetTypeRef> myCachedValue;

	public CSharpVariableImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef()
	{
		if(myCachedValue != null)
		{
			return myCachedValue.getValue();
		}
		myCachedValue = CachedValuesManager.getManager(getProject()).createCachedValue(new CachedValueProvider<DotNetTypeRef>()
		{
			@Nullable
			@Override
			public Result<DotNetTypeRef> compute()
			{
				return Result.createSingleDependency(CSharpPsiUtilImpl.toRuntimeType(CSharpVariableImpl.this), CSharpVariableImpl.this);
			}
		}, false);
		return myCachedValue.getValue();
	}

	@Override
	public boolean isConstant()
	{
		return false;
	}
}
