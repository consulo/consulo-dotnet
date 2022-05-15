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

package consulo.msil.impl.lang.psi;

import consulo.language.ast.TokenSet;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public interface MsilStubTokenSets extends MsilStubElements
{
	TokenSet TYPE_STUBS = TokenSet.create(NATIVE_TYPE, REFERENCE_TYPE, POINTER_TYPE, TYPE_BY_REF, TYPE_WITH_TYPE_ARGUMENTS, ARRAY_TYPE,
			CLASS_GENERIC_TYPE, METHOD_GENERIC_TYPE);

	TokenSet MEMBER_STUBS = TokenSet.create(ASSEMBLY, CLASS, METHOD, FIELD, PROPERTY, EVENT);
}
