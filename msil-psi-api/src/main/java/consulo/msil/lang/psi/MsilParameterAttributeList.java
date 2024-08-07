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

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetElement;
import consulo.util.collection.ArrayFactory;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public interface MsilParameterAttributeList extends DotNetElement
{
	public static final MsilParameterAttributeList[] EMPTY_ARRAY = new MsilParameterAttributeList[0];

	public static ArrayFactory<MsilParameterAttributeList> ARRAY_FACTORY = new ArrayFactory<MsilParameterAttributeList>()
	{
		@Nonnull
		@Override
		public MsilParameterAttributeList[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new MsilParameterAttributeList[count];
		}
	};

	@RequiredReadAction
	int getIndex();

	@Nonnull
	@RequiredReadAction
	MsilCustomAttribute[] getAttributes();

	@Nullable
	@RequiredReadAction
	MsilConstantValue getValue();
}
