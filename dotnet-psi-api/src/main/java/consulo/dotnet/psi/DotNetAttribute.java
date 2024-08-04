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
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 19.12.13.
 */
public interface DotNetAttribute extends DotNetElement
{
	public static final DotNetAttribute[] EMPTY_ARRAY = new DotNetAttribute[0];

	public static ArrayFactory<DotNetAttribute> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new DotNetAttribute[count];

	@Nullable
	DotNetTypeDeclaration resolveToType();

	@Nonnull
	DotNetTypeRef toTypeRef();
}
