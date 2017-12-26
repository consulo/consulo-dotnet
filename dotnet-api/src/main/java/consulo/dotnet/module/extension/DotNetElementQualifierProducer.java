package consulo.dotnet.module.extension;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;
import consulo.annotations.RequiredReadAction;

/**
 * @author VISTALL
 * @since 26-Dec-17
 */
public interface DotNetElementQualifierProducer
{
	boolean isMyElement(@NotNull PsiElement element);

	@Nullable
	@RequiredReadAction
	String getQualifiedName(@NotNull PsiElement element);
}
