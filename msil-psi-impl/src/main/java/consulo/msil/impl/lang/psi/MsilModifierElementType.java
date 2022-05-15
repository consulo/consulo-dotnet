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

package consulo.msil.impl.lang.psi;

import consulo.dotnet.psi.DotNetModifier;
import consulo.language.Language;
import consulo.language.ast.IElementType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilModifierElementType extends IElementType implements DotNetModifier
{
	public MsilModifierElementType(@Nonnull String debugName, @Nullable Language language)
	{
		super(debugName, language);
	}

	protected MsilModifierElementType(@Nonnull String debugName, @Nullable Language language, boolean register)
	{
		super(debugName, language, register);
	}

	@Override
	public String getPresentableText()
	{
		return toString().replace("_KEYWORD", "").toLowerCase(Locale.ROOT);
	}
}
