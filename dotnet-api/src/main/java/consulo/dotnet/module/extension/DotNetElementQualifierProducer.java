package consulo.dotnet.module.extension;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiElement;

import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 26-Dec-17
 */
public interface DotNetElementQualifierProducer
{
	boolean isMyElement(PsiElement element);

	@Nullable
	@RequiredReadAction
	String getQualifiedName(PsiElement element);
}
