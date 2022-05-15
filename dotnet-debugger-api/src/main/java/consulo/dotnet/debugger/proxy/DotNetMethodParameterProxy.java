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

package consulo.dotnet.debugger.proxy;

import consulo.util.collection.ArrayFactory;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public interface DotNetMethodParameterProxy extends DotNetVariableProxy
{
	public static final DotNetMethodParameterProxy[] EMPTY_ARRAY = new DotNetMethodParameterProxy[0];

	public static ArrayFactory<DotNetMethodParameterProxy> ARRAY_FACTORY = new ArrayFactory<DotNetMethodParameterProxy>()
	{
		@Nonnull
		@Override
		public DotNetMethodParameterProxy[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new DotNetMethodParameterProxy[count];
		}
	};

	int getIndex();
}
