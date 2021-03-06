package consulo.msil.lang.psi;

import javax.annotation.Nullable;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetExpression;
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
