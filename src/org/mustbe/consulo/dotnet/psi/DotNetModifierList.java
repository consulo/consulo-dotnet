package org.mustbe.consulo.dotnet.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public interface DotNetModifierList extends DotNetElement
{
	boolean hasModifier(@NotNull IElementType modifier);
}
