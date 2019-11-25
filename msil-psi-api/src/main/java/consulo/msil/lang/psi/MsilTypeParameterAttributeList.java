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
import javax.annotation.Nullable;

import com.intellij.util.ArrayFactory;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetElement;

/**
 * @author VISTALL
 * @since 12.06.2015
 */
public interface MsilTypeParameterAttributeList extends DotNetElement
{
	public static final MsilTypeParameterAttributeList[] EMPTY_ARRAY = new MsilTypeParameterAttributeList[0];

	public static ArrayFactory<MsilTypeParameterAttributeList> ARRAY_FACTORY = new ArrayFactory<MsilTypeParameterAttributeList>()
	{
		@Nonnull
		@Override
		public MsilTypeParameterAttributeList[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new MsilTypeParameterAttributeList[count];
		}
	};

	@Nullable
	@RequiredReadAction
	String getGenericParameterName();

	@Nonnull
	@RequiredReadAction
	MsilCustomAttribute[] getAttributes();
}
