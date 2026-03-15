package consulo.dotnet.psi.resolve;

import consulo.annotation.access.RequiredReadAction;
import consulo.dotnet.psi.DotNetQualifiedElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.util.collection.ArrayFactory;

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

	@RequiredReadAction
	default Collection<PsiElement> getChildren(GlobalSearchScope globalSearchScope, ChildrenFilter filter)
	{
		return getChildren(globalSearchScope, DotNetTypeTransformer.INSTANCE, filter);
	}

	@RequiredReadAction
	Collection<PsiElement> getChildren(GlobalSearchScope globalSearchScope, Function<PsiElement, PsiElement> transformer, ChildrenFilter filter);

	@RequiredReadAction
	Collection<PsiElement> findChildren(String name, GlobalSearchScope globalSearchScope, ChildrenFilter filter);

	@RequiredReadAction
	Collection<PsiElement> findChildren(String name,
										GlobalSearchScope globalSearchScope,
										Function<PsiElement, PsiElement> transformer,
										ChildrenFilter filter);

	@RequiredReadAction
	default boolean processChildren(GlobalSearchScope globalSearchScope,
									Function<PsiElement, PsiElement> transformer,
									ChildrenFilter filter,
									Predicate<PsiElement> processor)
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
