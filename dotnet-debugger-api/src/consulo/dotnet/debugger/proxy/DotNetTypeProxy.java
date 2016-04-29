/*
 * Copyright 2013-2016 must-be.org
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

package consulo.dotnet.debugger.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public interface DotNetTypeProxy
{
	boolean isAnnotatedBy(@NotNull String attributeVmQName);

	@Nullable
	DotNetTypeProxy getDeclarationType();

	@NotNull
	String getName();

	@NotNull
	String getFullName();

	boolean isArray();

	@Nullable
	DotNetTypeProxy getBaseType();

	@NotNull
	DotNetTypeProxy[] getInterfaces();

	@NotNull
	DotNetFieldProxy[] getFields();

	@NotNull
	DotNetPropertyProxy[] getProperties();

	@NotNull
	DotNetMethodProxy[] getMethods();

	boolean isNested();

	@Nullable
	DotNetMethodProxy findMethodByName(@NotNull String name, boolean deep, DotNetTypeProxy... params);

	boolean isAssignableFrom(@NotNull DotNetTypeProxy otherType);
}
