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

package org.mustbe.consulo.msil.lang.psi;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageVersion;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class ModifierElementType extends IElementType implements DotNetModifier
{
	public ModifierElementType(
			@NotNull @NonNls String debugName, @Nullable Language language)
	{
		super(debugName, language);
	}

	public ModifierElementType(
			@NotNull @NonNls String debugName, @Nullable Language language, @Nullable LanguageVersion languageVersion)
	{
		super(debugName, language, languageVersion);
	}

	protected ModifierElementType(
			@NotNull @NonNls String debugName, @Nullable Language language, @Nullable LanguageVersion languageVersion, boolean register)
	{
		super(debugName, language, languageVersion, register);
	}

	@Override
	public String name()
	{
		return toString();
	}
}
