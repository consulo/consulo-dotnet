package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface DotNetModifierListOwner extends DotNetElement
{
	boolean hasModifier(@NotNull IElementType modifier);

	@Nullable
	DotNetModifierList getModifierList();
}
