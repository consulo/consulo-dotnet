package consulo.dotnet.psi.search.searches;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.ProgressIndicatorProvider;
import consulo.application.util.function.Processor;
import consulo.content.scope.SearchScope;
import consulo.dotnet.psi.DotNetTypeDeclaration;
import consulo.language.psi.PsiBundle;
import jakarta.inject.Inject;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 03-Jul-22
 */
@ExtensionImpl
class DefaultTypeInheritorsSearchExecutor implements TypeInheritorsSearchExecutor
{
	@Inject
	DefaultTypeInheritorsSearchExecutor()
	{
	}

	@Override
	public boolean execute(@Nonnull TypeInheritorsSearch.SearchParameters parameters, @Nonnull Processor<? super DotNetTypeDeclaration> processor)
	{
		final String baseVmQName = parameters.getVmQName();
		final SearchScope searchScope = parameters.getScope();

		TypeInheritorsSearch.LOGGER.assertTrue(searchScope != null);

		ProgressIndicator progress = ProgressIndicatorProvider.getGlobalProgressIndicator();
		if(progress != null)
		{
			progress.pushState();
			progress.setText(PsiBundle.message("psi.search.inheritors.of.class.progress", baseVmQName));
		}

		boolean result = TypeInheritorsSearch.processInheritors(processor, baseVmQName, searchScope, parameters);

		if(progress != null)
		{
			progress.popState();
		}

		return result;
	}
}
