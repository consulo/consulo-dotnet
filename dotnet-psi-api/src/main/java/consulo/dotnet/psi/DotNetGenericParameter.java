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

import consulo.language.psi.PsiNameIdentifierOwner;
import consulo.util.collection.ArrayFactory;

/**
 * @author VISTALL
 * @since 30.11.13.
 */
public interface DotNetGenericParameter extends DotNetNamedElement, DotNetModifierListOwner, PsiNameIdentifierOwner, DotNetAttributeListOwner
{
	public static final DotNetGenericParameter[] EMPTY_ARRAY = new DotNetGenericParameter[0];

	public static ArrayFactory<DotNetGenericParameter> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new DotNetGenericParameter[count];

	int getIndex();
}
