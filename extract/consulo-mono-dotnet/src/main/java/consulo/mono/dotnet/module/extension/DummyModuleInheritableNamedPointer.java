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

package consulo.mono.dotnet.module.extension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.openapi.module.Module;
import consulo.module.extension.ModuleInheritableNamedPointer;
import consulo.module.extension.MutableModuleInheritableNamedPointer;
import consulo.util.pointers.Named;

/**
 * @author VISTALL
 * @since 23.04.14
 */
public abstract class DummyModuleInheritableNamedPointer<T extends Named> implements MutableModuleInheritableNamedPointer<T>
{
	@Nullable
	@Override
	public Module getModule()
	{
		return null;
	}

	@Nullable
	@Override
	public String getModuleName()
	{
		return null;
	}

	@Override
	public boolean isNull()
	{
		return get() == null;
	}

	@Nonnull
	@Override
	public String getName()
	{
		T t = get();
		if(t == null)
		{
			return "";
		}
		return t.getName();
	}

	@Override
	public void set(ModuleInheritableNamedPointer<T> value)
	{

	}

	@Override
	public void set(@Nullable String moduleName, @Nullable String name)
	{

	}

	@Override
	public void set(@Nullable Module module, @Nullable T named)
	{

	}
}
