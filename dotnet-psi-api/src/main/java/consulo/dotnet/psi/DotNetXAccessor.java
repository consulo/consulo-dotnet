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

import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiNameIdentifierOwner;
import consulo.util.collection.ArrayFactory;

import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 28.11.13.
 * Index Method
 * Property
 * Event
 */
public interface DotNetXAccessor extends DotNetModifierListOwner, PsiNameIdentifierOwner, DotNetNamedElement, DotNetCodeBlockOwner
{
	DotNetXAccessor[] EMPTY_ARRAY = new DotNetXAccessor[0];

	ArrayFactory<DotNetXAccessor> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new DotNetXAccessor[count];

	String VALUE = "value";

	enum Kind
	{
		GET,
		SET,
		ADD,
		REMOVE;

		public static final Kind[] VALUES = values();
	}

	@Nullable
	PsiElement getAccessorElement();

	@Nullable
	Kind getAccessorKind();
}
