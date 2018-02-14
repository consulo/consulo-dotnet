package consulo.dotnet.resolve;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayFactory;
import com.intellij.util.NotNullFunction;
import consulo.annotations.RequiredReadAction;
import consulo.dotnet.psi.DotNetQualifiedElement;

/**
 * @author VISTALL
 * @since 11.02.14
 */
public interface DotNetNamespaceAsElement extends DotNetQualifiedElement
{
	DotNetNamespaceAsElement[] EMPTY_ARRAY = new DotNetNamespaceAsElement[0];

	ArrayFactory<DotNetNamespaceAsElement> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new DotNetNamespaceAsElement[count];

	enum ChildrenFilter
	{
		ONLY_ELEMENTS,
		ONLY_NAMESPACES,
		NONE
	}

	@Nonnull
	@RequiredReadAction
	Collection<PsiElement> getChildren(@Nonnull GlobalSearchScope globalSearchScope, @Nonnull ChildrenFilter filter);

	@Nonnull
	@RequiredReadAction
	Collection<PsiElement> getChildren(@Nonnull GlobalSearchScope globalSearchScope, @Nonnull NotNullFunction<PsiElement, PsiElement> transformer, @Nonnull ChildrenFilter filter);

	@Nonnull
	@RequiredReadAction
	Collection<PsiElement> findChildren(@Nonnull String name, @Nonnull GlobalSearchScope globalSearchScope, @Nonnull ChildrenFilter filter);

	@Nonnull
	@RequiredReadAction
	Collection<PsiElement> findChildren(@Nonnull String name, @Nonnull GlobalSearchScope globalSearchScope, @Nonnull NotNullFunction<PsiElement, PsiElement> transformer, @Nonnull ChildrenFilter filter);
}
