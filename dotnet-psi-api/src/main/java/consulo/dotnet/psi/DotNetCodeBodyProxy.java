package consulo.dotnet.psi;

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
		public DotNetElement getElement()
		{
			return null;
		}

		@Override
		public void replace(@Nullable DotNetElement element)
		{
			throw new UnsupportedOperationException();
		}
	};

	@Nullable
	@RequiredReadAction
	default DotNetExpression asExpression() {
		DotNetElement element = getElement();
		return element instanceof DotNetExpression ? (DotNetExpression) element : null;
	}

	@Nullable
	@RequiredReadAction
	default DotNetStatement asStatement()
	{
		DotNetElement element = getElement();
		return element instanceof DotNetStatement ? (DotNetStatement) element : null;
	}

	@Nullable
	@RequiredReadAction
	DotNetElement getElement();

	void replace(@Nullable DotNetElement element);
}
