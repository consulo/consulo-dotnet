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

package consulo.dotnet.psi;

import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.util.collection.ArrayFactory;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 16.12.13.
 */
public interface DotNetExpression extends DotNetElement
{
	public static final DotNetExpression[] EMPTY_ARRAY = new DotNetExpression[0];

	public static ArrayFactory<DotNetExpression> ARRAY_FACTORY = new ArrayFactory<DotNetExpression>()
	{
		@Nonnull
		@Override
		public DotNetExpression[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new DotNetExpression[count];
		}
	};

	@Nonnull
	DotNetTypeRef toTypeRef(boolean resolveFromParent);
}
