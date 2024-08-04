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

import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiElement;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface DotNetModifierList extends DotNetElement
{
	void addModifier(@Nonnull DotNetModifier modifier);

	void removeModifier(@Nonnull DotNetModifier modifier);

	@Nonnull
	DotNetModifier[] getModifiers();

	@Nonnull
	@RequiredReadAction
	DotNetAttribute[] getAttributes();

	boolean hasModifier(@Nonnull DotNetModifier modifier);

	boolean hasModifierInTree(@Nonnull DotNetModifier modifier);

	@Nullable
	PsiElement getModifierElement(DotNetModifier modifier);

	@Nonnull
	List<PsiElement> getModifierElements(@Nonnull DotNetModifier modifier);
}
