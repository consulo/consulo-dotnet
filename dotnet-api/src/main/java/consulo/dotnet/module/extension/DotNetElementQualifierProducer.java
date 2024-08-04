package consulo.dotnet.module.extension;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiElement;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 26-Dec-17
 */
public interface DotNetElementQualifierProducer
{
	boolean isMyElement(@Nonnull PsiElement element);

	@Nullable
	@RequiredReadAction
	String getQualifiedName(@Nonnull PsiElement element);
}
