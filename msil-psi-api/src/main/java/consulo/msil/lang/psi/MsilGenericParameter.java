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
import consulo.dotnet.psi.DotNetGenericParameter;
import consulo.dotnet.psi.resolve.DotNetPsiSearcher;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.util.collection.ArrayFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 13.12.14
 */
public interface MsilGenericParameter extends DotNetGenericParameter
{
	MsilGenericParameter[] EMPTY_ARRAY = new MsilGenericParameter[0];

	ArrayFactory<MsilGenericParameter> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new MsilGenericParameter[count];

	@Nonnull
	@RequiredReadAction
	DotNetTypeRef[] getExtendTypeRefs();

	@Nonnull
	@Deprecated
	@RequiredReadAction
	DotNetPsiSearcher.TypeResoleKind getTypeKind();

	@Nullable
	@RequiredReadAction
	MsilUserType.Target getTarget();

	@RequiredReadAction
	boolean hasDefaultConstructor();
}
