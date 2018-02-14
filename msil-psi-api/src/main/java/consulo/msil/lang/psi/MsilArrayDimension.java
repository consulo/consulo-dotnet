/*
 * Copyright 2013-2015 must-be.org
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
import com.intellij.util.ArrayFactory;
import consulo.dotnet.psi.DotNetElement;

/**
 * @author VISTALL
 * @since 19.05.2015
 */
public interface MsilArrayDimension extends DotNetElement
{
	public static final MsilArrayDimension[] EMPTY_ARRAY = new MsilArrayDimension[0];

	public static ArrayFactory<MsilArrayDimension> ARRAY_FACTORY = new ArrayFactory<MsilArrayDimension>()
	{
		@Nonnull
		@Override
		public MsilArrayDimension[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new MsilArrayDimension[count];
		}
	};

	int getLowerValue();
}
