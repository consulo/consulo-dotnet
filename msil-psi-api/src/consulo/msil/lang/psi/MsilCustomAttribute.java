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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.util.ArrayFactory;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetAttribute;
import consulo.dotnet.psi.DotNetParameterList;
import consulo.dotnet.psi.DotNetType;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public interface MsilCustomAttribute extends DotNetAttribute
{
	public static final MsilCustomAttribute[] EMPTY_ARRAY = new MsilCustomAttribute[0];

	public static ArrayFactory<MsilCustomAttribute> ARRAY_FACTORY = new ArrayFactory<MsilCustomAttribute>()
	{
		@NotNull
		@Override
		public MsilCustomAttribute[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new MsilCustomAttribute[count];
		}
	};

	@Nullable
	@RequiredReadAction
	DotNetType getType();

	@NotNull
	@RequiredReadAction
	DotNetParameterList getParameterList();

	@NotNull
	@RequiredReadAction
	MsilCustomAttributeSignature getSignature();
}
