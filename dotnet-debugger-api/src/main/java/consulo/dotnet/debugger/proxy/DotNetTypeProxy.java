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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import consulo.dotnet.debugger.proxy.light.LightTypeProxy;
import consulo.util.collection.ArrayUtil;

/**
 * @author VISTALL
 * @since 18.04.2016
 */
public interface DotNetTypeProxy
{
	boolean isAnnotatedBy(@Nonnull String attributeVmQName);

	@Nullable
	DotNetTypeProxy getDeclarationType();

	@Nonnull
	String getName();

	@Nonnull
	String getFullName();

	@Nonnull
	default String[] getGenericParameters()
	{
		return ArrayUtil.EMPTY_STRING_ARRAY;
	}

	boolean isArray();

	@Nullable
	DotNetTypeProxy getBaseType();

	@Nonnull
	DotNetTypeProxy[] getInterfaces();

	@Nonnull
	DotNetFieldProxy[] getFields();

	@Nonnull
	DotNetPropertyProxy[] getProperties();

	@Nonnull
	DotNetMethodProxy[] getMethods();

	boolean isNested();

	@Nullable
	DotNetMethodProxy findMethodByName(@Nonnull String name, boolean deep, DotNetTypeProxy... params);

	boolean isAssignableFrom(@Nonnull DotNetTypeProxy otherType);

	@Nonnull
	default DotNetTypeProxy lightCopy()
	{
		return new LightTypeProxy(this);
	}
}
