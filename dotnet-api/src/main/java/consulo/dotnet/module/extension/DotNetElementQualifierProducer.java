package consulo.dotnet.module.extension;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.psi.PsiElement;
import consulo.annotations.RequiredReadAction;

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
