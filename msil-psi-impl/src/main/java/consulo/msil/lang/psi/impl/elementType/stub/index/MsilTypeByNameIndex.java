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

import javax.annotation.Nonnull;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import consulo.msil.lang.psi.MsilClassEntry;

/**
 * @author VISTALL
 * @since 05.07.15
 */
public class MsilTypeByNameIndex extends StringStubIndexExtension<MsilClassEntry>
{
	@Nonnull
	public static MsilTypeByNameIndex getInstance()
	{
		return EP_NAME.findExtension(MsilTypeByNameIndex.class);
	}

	@Nonnull
	@Override
	public StubIndexKey<String, MsilClassEntry> getKey()
	{
		return MsilIndexKeys.TYPE_BY_NAME_INDEX;
	}
}
