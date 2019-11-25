package consulo.dotnet.module.extension;

import com.intellij.psi.PsiElement;
import consulo.annotation.access.RequiredReadAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
