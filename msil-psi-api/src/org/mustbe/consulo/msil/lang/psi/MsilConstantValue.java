package org.mustbe.consulo.msil.lang.psi;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.RequiredReadAction;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 19.05.2015
 */
public interface MsilConstantValue extends DotNetExpression
{
	@Nullable
	@RequiredReadAction
	IElementType getValueType();

	@Nullable
	@RequiredReadAction
	String getValueText();
}
