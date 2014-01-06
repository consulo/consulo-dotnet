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
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpVariableStub;
import org.mustbe.consulo.dotnet.psi.DotNetType;
import org.mustbe.consulo.dotnet.psi.DotNetVariable;
import org.mustbe.consulo.dotnet.resolve.DotNetRuntimeType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;

/**
 * @author VISTALL
 * @since 06.01.14.
 */
public abstract class CSharpStubVariableImpl<S extends CSharpVariableStub<?>> extends CSharpStubMemberImpl<S> implements DotNetVariable
{
	private CachedValue<DotNetRuntimeType> myCachedValue;

	public CSharpStubVariableImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpStubVariableImpl(@NotNull S stub, @NotNull IStubElementType<? extends S, ?> nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public boolean isConstant()
	{
		S stub = getStub();
		if(stub != null)
		{
			return stub.isConstant();
		}
		return false;
	}

	@NotNull
	@Override
	public DotNetRuntimeType toRuntimeType()
	{
		if(myCachedValue != null)
		{
			return myCachedValue.getValue();
		}
		myCachedValue = CachedValuesManager.getManager(getProject()).createCachedValue(new CachedValueProvider<DotNetRuntimeType>()
		{
			@Nullable
			@Override
			public Result<DotNetRuntimeType> compute()
			{
				DotNetType type = getType();
				DotNetRuntimeType runtimeType = type == null ? DotNetRuntimeType.ERROR_TYPE : type.toRuntimeType();
				return Result.createSingleDependency(runtimeType, CSharpStubVariableImpl.this);
			}
		}, false);
		return myCachedValue.getValue();
	}
}
