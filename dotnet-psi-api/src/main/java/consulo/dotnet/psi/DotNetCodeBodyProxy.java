package consulo.dotnet.psi;

import com.intellij.psi.PsiElement;
import consulo.annotations.RequiredReadAction;

import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 2019-10-28
 */
public interface DotNetCodeBodyProxy
{
	DotNetCodeBodyProxy EMPTY = new DotNetCodeBodyProxy()
	{
		@RequiredReadAction
		@Nullable
		@Override
		public PsiElement getElement()
		{
			return null;
		}

		@Override
		public void replace(@Nullable PsiElement element)
		{
			throw new UnsupportedOperationException();
		}
	};

	@Nullable
	@RequiredReadAction
	default DotNetExpression asExpression() {
		PsiElement element = getElement();
		return element instanceof DotNetExpression ? (DotNetExpression) element : null;
	}

	@Nullable
	@RequiredReadAction
	default DotNetStatement asStatement()
	{
		PsiElement element = getElement();
		return element instanceof DotNetStatement ? (DotNetStatement) element : null;
	}

	@Nullable
	@RequiredReadAction
	PsiElement getElement();

	void replace(@Nullable PsiElement element);
}
