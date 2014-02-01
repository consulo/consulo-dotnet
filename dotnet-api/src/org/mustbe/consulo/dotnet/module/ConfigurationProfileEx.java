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

package org.mustbe.consulo.dotnet.module;

import javax.swing.JComponent;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Key;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public interface ConfigurationProfileEx<T extends ConfigurationProfileEx<T>>
{
	@NotNull
	Key<?> getKey();

	boolean equalsEx(@NotNull T ex);

	void loadState(Element element);

	void getState(Element element);

	@Nullable
	JComponent createConfigurablePanel(@NotNull ModifiableRootModel modifiableRootModel, @Nullable Runnable runnable);

	@NotNull
	ConfigurationProfileEx<T> clone();
}
