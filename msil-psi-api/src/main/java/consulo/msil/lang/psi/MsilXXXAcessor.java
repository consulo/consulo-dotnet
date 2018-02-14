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

package consulo.msil.lang.psi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.util.ArrayFactory;
import consulo.dotnet.psi.DotNetParameter;
import consulo.dotnet.psi.DotNetXXXAccessor;
import consulo.dotnet.resolve.DotNetTypeRef;

/**
 * @author VISTALL
 * @since 24.05.14
 */
public interface MsilXXXAcessor extends DotNetXXXAccessor
{
	public static final MsilXXXAcessor[] EMPTY_ARRAY = new MsilXXXAcessor[0];

	public static ArrayFactory<MsilXXXAcessor> ARRAY_FACTORY = new ArrayFactory<MsilXXXAcessor>()
	{
		@Nonnull
		@Override
		public MsilXXXAcessor[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new MsilXXXAcessor[count];
		}
	};

	@Nullable
	DotNetTypeRef getReturnType();

	@Nullable
	String getMethodName();

	@Nonnull
	DotNetParameter[] getParameters();

	@Nonnull
	DotNetTypeRef[] getParameterTypeRefs();

	@Nullable
	MsilMethodEntry resolveToMethod();
}
