package consulo.msil.lang.psi;

import javax.annotation.Nonnull;
import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetElement;

/**
 * @author VISTALL
 * @since 18.05.2015
 */
public interface MsilCustomAttributeSignature extends DotNetElement
{
	@Nonnull
	@RequiredReadAction
	byte[] getBytes();
}
