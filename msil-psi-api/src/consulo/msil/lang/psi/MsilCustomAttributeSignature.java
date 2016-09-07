package consulo.msil.lang.psi;

import org.jetbrains.annotations.NotNull;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetElement;

/**
 * @author VISTALL
 * @since 18.05.2015
 */
public interface MsilCustomAttributeSignature extends DotNetElement
{
	@NotNull
	@RequiredReadAction
	byte[] getBytes();
}
