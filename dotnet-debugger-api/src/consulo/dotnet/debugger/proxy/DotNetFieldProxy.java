package consulo.dotnet.debugger.proxy;

import org.consulo.lombok.annotations.ArrayFactoryFields;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author VISTALL
 * @since 19.04.2016
 */
@ArrayFactoryFields
public interface DotNetFieldProxy extends DotNetFieldOrPropertyProxy
{
	boolean isLiteral();

	@Nullable
	Number getEnumConstantValue(@NotNull DotNetStackFrameProxy stackFrameProxy);
}
