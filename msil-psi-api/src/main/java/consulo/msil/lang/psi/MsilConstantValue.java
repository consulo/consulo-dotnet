package consulo.msil.lang.psi;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetExpression;
import consulo.language.ast.IElementType;
import jakarta.annotation.Nullable;

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
