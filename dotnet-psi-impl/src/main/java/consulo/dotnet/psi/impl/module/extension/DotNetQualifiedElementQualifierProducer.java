package consulo.dotnet.psi.impl.module.extension;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.module.extension.DotNetElementQualifierProducer;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.language.psi.PsiElement;
import jakarta.annotation.Nonnull;

import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 26-Dec-17
 */
public class DotNetQualifiedElementQualifierProducer implements DotNetElementQualifierProducer
{
	public static final DotNetQualifiedElementQualifierProducer INSTANCE = new DotNetQualifiedElementQualifierProducer();

	@Override
	public boolean isMyElement(@Nonnull PsiElement element)
	{
		return element instanceof DotNetQualifiedElement;
	}

	@RequiredReadAction
	@Nullable
	@Override
	public String getQualifiedName(@Nonnull PsiElement element)
	{
		return ((DotNetQualifiedElement) element).getPresentableQName();
	}
}
