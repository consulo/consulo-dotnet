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

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.resolve.DotNetTypeRef;
import consulo.language.psi.PsiNameIdentifierOwner;
import consulo.util.collection.ArrayFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface DotNetTypeDeclaration extends DotNetQualifiedElement, DotNetModifierListOwner, DotNetGenericParameterListOwner, PsiNameIdentifierOwner, DotNetMemberOwner
{
	final DotNetTypeDeclaration[] EMPTY_ARRAY = new DotNetTypeDeclaration[0];

	ArrayFactory<DotNetTypeDeclaration> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new DotNetTypeDeclaration[count];

	@RequiredReadAction
	boolean isInterface();

	@RequiredReadAction
	boolean isStruct();

	@RequiredReadAction
	boolean isEnum();

	@RequiredReadAction
	boolean isNested();

	@Nullable
	@RequiredReadAction
	DotNetTypeList getExtendList();

	@Nonnull
	@RequiredReadAction
	DotNetTypeRef[] getExtendTypeRefs();

	@RequiredReadAction
	boolean isInheritor(@Nonnull String otherVmQName, boolean deep);

	@Nonnull
	@RequiredReadAction
	DotNetTypeRef getTypeRefForEnumConstants();

	@Nullable
	@RequiredReadAction
	String getVmQName();

	@Nullable
	@RequiredReadAction
	String getVmName();
}
