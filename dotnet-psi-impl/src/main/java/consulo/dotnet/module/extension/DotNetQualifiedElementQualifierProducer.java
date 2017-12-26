package consulo.dotnet.module.extension;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetQualifiedElement;

/**
 * @author VISTALL
 * @since 26-Dec-17
 */
public class DotNetQualifiedElementQualifierProducer implements DotNetElementQualifierProducer
{
	public static final DotNetQualifiedElementQualifierProducer INSTANCE = new DotNetQualifiedElementQualifierProducer();

	@Override
	public boolean isMyElement(@NotNull PsiElement element)
	{
		return element instanceof DotNetQualifiedElement;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getQualifiedName(@NotNull PsiElement element)
	{
		return ((DotNetQualifiedElement) element).getPresentableQName();
	}
}
