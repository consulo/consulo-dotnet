package consulo.dotnet.psi.resolve;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.util.collection.ArrayFactory;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

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
	default Collection<PsiElement> getChildren(@Nonnull GlobalSearchScope globalSearchScope, @Nonnull ChildrenFilter filter)
	{
		return getChildren(globalSearchScope, DotNetTypeTransformer.INSTANCE, filter);
	}

	@Nonnull
	@RequiredReadAction
	Collection<PsiElement> getChildren(@Nonnull GlobalSearchScope globalSearchScope, @Nonnull Function<PsiElement, PsiElement> transformer, @Nonnull ChildrenFilter filter);

	@Nonnull
	@RequiredReadAction
	Collection<PsiElement> findChildren(@Nonnull String name, @Nonnull GlobalSearchScope globalSearchScope, @Nonnull ChildrenFilter filter);

	@Nonnull
	@RequiredReadAction
	Collection<PsiElement> findChildren(@Nonnull String name,
										@Nonnull GlobalSearchScope globalSearchScope,
										@Nonnull Function<PsiElement, PsiElement> transformer,
										@Nonnull ChildrenFilter filter);

	@RequiredReadAction
	default boolean processChildren(@Nonnull GlobalSearchScope globalSearchScope,
									@Nonnull Function<PsiElement, PsiElement> transformer,
									@Nonnull ChildrenFilter filter,
									@Nonnull Predicate<PsiElement> processor)
	{
		Collection<PsiElement> children = getChildren(globalSearchScope, transformer, filter);

		for(PsiElement child : children)
		{
			if(!processor.test(child))
			{
				return false;
			}
		}
		return true;
	}
}
