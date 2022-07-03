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
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.language.psi.stub.StringStubIndexExtension;
import consulo.language.psi.stub.StubIndexKey;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 23.09.14
 */
@ExtensionImpl
public class MsilElementByQNameIndex extends StringStubIndexExtension<DotNetQualifiedElement>
{
	@Nonnull
	public static MsilElementByQNameIndex getInstance()
	{
		return EP_NAME.findExtensionOrFail(MsilElementByQNameIndex.class);
	}

	@Nonnull
	@Override
	public StubIndexKey<String, DotNetQualifiedElement> getKey()
	{
		return MsilIndexKeys.ELEMENT_BY_QNAME_INDEX;
	}
}
