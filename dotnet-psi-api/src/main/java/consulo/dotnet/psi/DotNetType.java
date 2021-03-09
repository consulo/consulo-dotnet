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

import com.intellij.util.ArrayFactory;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.resolve.DotNetTypeRef;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface DotNetType extends DotNetElement
{
	public static final DotNetType[] EMPTY_ARRAY = new DotNetType[0];

	public static ArrayFactory<DotNetType> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new DotNetType[count];

	@Nonnull
	@RequiredReadAction
	DotNetTypeRef toTypeRef();
}
