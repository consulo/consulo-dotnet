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

package consulo.msil.lang.psi.impl.elementType.stub.index;

import consulo.language.psi.stub.IntStubIndexExtension;
import consulo.language.psi.stub.StubIndexKey;
import consulo.msil.lang.psi.MsilClassEntry;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilTypeByQNameIndex extends IntStubIndexExtension<MsilClassEntry>
{
	@Nonnull
	public static MsilTypeByQNameIndex getInstance()
	{
		return EP_NAME.findExtensionOrFail(MsilTypeByQNameIndex.class);
	}

	@Nonnull
	@Override
	public StubIndexKey<Integer, MsilClassEntry> getKey()
	{
		return MsilIndexKeys.TYPE_BY_QNAME_INDEX;
	}
}
