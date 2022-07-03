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

package consulo.msil.impl.lang.psi.impl.elementType.stub.index;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.psi.stub.StringStubIndexExtension;
import consulo.language.psi.stub.StubIndexKey;
import consulo.msil.lang.psi.MsilClassEntry;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 05.07.15
 */
@ExtensionImpl
public class MsilTypeByNameIndex extends StringStubIndexExtension<MsilClassEntry>
{
	@Nonnull
	public static MsilTypeByNameIndex getInstance()
	{
		return EP_NAME.findExtensionOrFail(MsilTypeByNameIndex.class);
	}

	@Nonnull
	@Override
	public StubIndexKey<String, MsilClassEntry> getKey()
	{
		return MsilIndexKeys.TYPE_BY_NAME_INDEX;
	}
}
